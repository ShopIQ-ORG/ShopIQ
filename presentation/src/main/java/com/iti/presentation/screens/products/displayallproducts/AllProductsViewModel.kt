package com.iti.presentation.screens.products.displayallproducts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.models.Product
import com.iti.domain.models.Result
import com.iti.domain.models.User
import com.iti.domain.usecases.auth.GetCurrentUserUseCase
import com.iti.domain.usecases.products.AddProductToFavoritesUseCase
import com.iti.domain.usecases.products.GetFavoriteProductsUseCase
import com.iti.domain.usecases.products.RemoveProductFromFavoritesUseCase
import com.iti.domain.repositories.auth.AuthRepository
import com.iti.domain.usecases.products.GetProductsPaginatedUseCase
import com.iti.presentation.R
import com.iti.presentation.util.UiText
import com.iti.presentation.screens.products.displayallproducts.AllProductsContract.FilterState
import com.iti.presentation.screens.products.displayallproducts.AllProductsContract.SortOption
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AllProductsViewModel(
    private val addProductToFavoritesUseCase: AddProductToFavoritesUseCase,
    private val removeProductFromFavoritesUseCase: RemoveProductFromFavoritesUseCase,
    private val getFavoriteProductsUseCase: GetFavoriteProductsUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val authRepository: AuthRepository,
    private val getProductsPaginatedUseCase: GetProductsPaginatedUseCase,
    private val filterManager: AllProductsFilterManager
) : ViewModel() {

    private val _state = MutableStateFlow(AllProductsContract.State())
    val state: StateFlow<AllProductsContract.State> = _state.asStateFlow()

    private val _effect = Channel<AllProductsContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private val favoriteOverrides = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    private val allProductsStateFlow = MutableStateFlow<List<Product>>(emptyList())
    private var allProducts: List<Product> = emptyList()
    private var isLoaded = false

    init {
        viewModelScope.launch {
            combine(
                allProductsStateFlow,
                getFavoriteProductsUseCase(),
                favoriteOverrides
            ) { products, favoritesResult, overrides ->
                val favoriteIds = if (favoritesResult is Result.Success) {
                    favoritesResult.data.map { it.id }.toSet()
                } else {
                    emptySet()
                }
                products.map { product ->
                    val isFavoriteInDb = product.id in favoriteIds
                    val isFavorite = overrides[product.id] ?: isFavoriteInDb
                    product.copy(isFavorite = isFavorite)
                }
            }.collect { updatedProducts ->
                allProducts = updatedProducts
                applyAll()
            }
        }
    }

    // ─── Public API ──────────────────────────────────────────────────────────────

    fun sendIntent(intent: AllProductsContract.Intent) {
        when (intent) {
            // load
            is AllProductsContract.Intent.LoadData           -> load(intent.brandName, intent.subCategoryName)
            is AllProductsContract.Intent.Retry              -> load(_state.value.activeBrand, _state.value.activeSubCategory)
            is AllProductsContract.Intent.LoadMore           -> loadMore()

            // product interaction
            is AllProductsContract.Intent.ProductClicked     -> emitEffect(
                AllProductsContract.Effect.NavigateToProduct(intent.product.id)
            )
            is AllProductsContract.Intent.ProductFavoriteClicked -> toggleFavorite(intent.product)

            // legacy brand chip
            is AllProductsContract.Intent.ClearFilter        -> clearLegacyBrandFilter()

            // direct category selection
            is AllProductsContract.Intent.SelectCategory     -> selectCategory(intent.category)

            // search
            is AllProductsContract.Intent.OpenSearch         -> _state.update { it.copy(isSearchActive = true) }
            is AllProductsContract.Intent.CloseSearch        -> _state.update { it.copy(isSearchActive = false, searchQuery = "") }.also { applyAll() }
            is AllProductsContract.Intent.SearchQueryChanged -> onSearchQueryChanged(intent.query)
            is AllProductsContract.Intent.PopularSearchClicked -> onSearchQueryChanged(intent.term)
            is AllProductsContract.Intent.RecentSearchClicked  -> onSearchQueryChanged(intent.term)
            is AllProductsContract.Intent.ClearRecentSearches  -> _state.update { it.copy(recentSearches = emptyList()) }

            // filter sheet
            is AllProductsContract.Intent.OpenFilterSheet    -> _state.update {
                it.copy(isFilterSheetOpen = true, pendingFilterState = it.filterState)
            }
            is AllProductsContract.Intent.CloseFilterSheet   -> _state.update { it.copy(isFilterSheetOpen = false) }
            is AllProductsContract.Intent.PendingCategoryToggled -> {
                _state.update { s ->
                    val current = s.pendingFilterState.selectedCategories.toMutableSet()
                    if (current.contains(intent.category)) current.remove(intent.category) else current.add(intent.category)
                    s.copy(pendingFilterState = s.pendingFilterState.copy(selectedCategories = current))
                }
            }
            is AllProductsContract.Intent.PendingClearCategories -> {
                _state.update { s ->
                    s.copy(pendingFilterState = s.pendingFilterState.copy(selectedCategories = emptySet()))
                }
            }
            is AllProductsContract.Intent.PendingSelectAllCategories -> {
                _state.update { s ->
                    s.copy(pendingFilterState = s.pendingFilterState.copy(selectedCategories = s.availableCategories.toSet()))
                }
            }
            is AllProductsContract.Intent.PendingSubCategoryToggled -> {
                _state.update { s ->
                    val current = s.pendingFilterState.selectedSubCategories.toMutableSet()
                    if (current.contains(intent.subCategory)) current.remove(intent.subCategory) else current.add(intent.subCategory)
                    s.copy(pendingFilterState = s.pendingFilterState.copy(selectedSubCategories = current))
                }
            }
            is AllProductsContract.Intent.PendingClearSubCategories -> {
                _state.update { s ->
                    s.copy(pendingFilterState = s.pendingFilterState.copy(selectedSubCategories = emptySet()))
                }
            }
            is AllProductsContract.Intent.PendingSelectAllSubCategories -> {
                _state.update { s ->
                    s.copy(pendingFilterState = s.pendingFilterState.copy(selectedSubCategories = s.availableSubCategories.toSet()))
                }
            }
            is AllProductsContract.Intent.PendingBrandToggled -> onPendingBrandToggled(intent.brand)
            is AllProductsContract.Intent.PendingBrandSearchChanged -> {
                _state.update { it.copy(pendingFilterState = it.pendingFilterState.copy(brandSearchQuery = intent.query)) }
            }
            is AllProductsContract.Intent.PendingSelectAllBrands -> {
                _state.update { s ->
                    s.copy(pendingFilterState = s.pendingFilterState.copy(selectedBrands = s.availableBrands.toSet()))
                }
            }
            is AllProductsContract.Intent.ApplyFilters       -> applyFilters()
            is AllProductsContract.Intent.ResetFilters       -> resetFilters()

            // sort sheet
            is AllProductsContract.Intent.OpenSortSheet      -> _state.update {
                it.copy(isSortSheetOpen = true, pendingSortOption = it.sortOption)
            }
            is AllProductsContract.Intent.CloseSortSheet     -> _state.update { it.copy(isSortSheetOpen = false) }
            is AllProductsContract.Intent.PendingSortChanged -> _state.update { it.copy(pendingSortOption = intent.option) }
            is AllProductsContract.Intent.ApplySort          -> applySort()
        }
    }

    private fun toggleFavorite(product: Product) {
        // FAST check for guest status
        if (authRepository.isGuest()) {
            emitEffect(AllProductsContract.Effect.ShowAuthRequired)
            return
        }

        viewModelScope.launch {
            val productId = product.id
            val isFavorite = product.isFavorite

            try {
                // Optimistic update via override map - IMMEDIATE
                favoriteOverrides.update { it + (productId to !isFavorite) }

                if (isFavorite) {
                    removeProductFromFavoritesUseCase(productId)
                } else {
                    addProductToFavoritesUseCase(product)
                }
                
                // Clear override after a short delay
                kotlinx.coroutines.delay(1000)
                favoriteOverrides.update { it - productId }
            } catch (e: Exception) {
                // Revert optimistic update
                favoriteOverrides.update { it - productId }
            }
        }
    }

    // ─── Private Helpers ─────────────────────────────────────────────────────────

    private fun load(brandName: String?, subCategoryName: String? = null) {
        isLoaded = false
        allProductsStateFlow.value = emptyList()
        allProducts = emptyList()
        val initialFilter = FilterState(
            selectedBrands = if (brandName != null) setOf(brandName) else emptySet(),
            selectedSubCategories = if (subCategoryName != null) setOf(subCategoryName) else emptySet()
        )
        _state.update {
            it.copy(
                screenState = AllProductsContract.ScreenState.Loading,
                activeBrand = brandName,
                activeSubCategory = subCategoryName,
                isFilterSheetOpen = false,
                isSortSheetOpen = false,
                isSearchActive = false,
                searchQuery = "",
                filterState = initialFilter,
                pendingFilterState = initialFilter,
                sortOption = SortOption.BEST_SELLING,
                pendingSortOption = SortOption.BEST_SELLING,
                hasNextPage = false,
                endCursor = null,
                isLoadingMore = false
            )
        }
        viewModelScope.launch {
            getProductsPaginatedUseCase(count = 50, after = null).collect { result ->
                when (result) {
                    is Result.Loading -> _state.update {
                        it.copy(screenState = AllProductsContract.ScreenState.Loading)
                    }
                    is Result.Success -> {
                        isLoaded = true
                        allProductsStateFlow.value = result.data.products
                        val categories = result.data.products.flatMap { it.tags }.distinct().filter { it.isNotBlank() }.sorted()
                        val subCategories = result.data.products.map { it.productType }.distinct().filter { it.isNotBlank() }.sorted()
                        val brands = result.data.products.map { it.vendor }.distinct().filter { it.isNotBlank() }.sorted()
                        _state.update {
                            it.copy(
                                availableCategories = categories,
                                availableSubCategories = subCategories,
                                availableBrands = brands,
                                hasNextPage = result.data.hasNextPage,
                                endCursor = result.data.endCursor
                            )
                        }
                    }
                    is Result.Failure -> _state.update {
                        it.copy(
                            screenState = AllProductsContract.ScreenState.Failure(
                                result.exception.message
                                    ?.let { msg -> UiText.Plain(msg) }
                                    ?: UiText.StringResource(R.string.error_loading_products)
                            )
                        )
                    }
                }
            }
        }
    }

    private fun loadMore() {
        val currentState = _state.value
        if (currentState.isLoadingMore || !currentState.hasNextPage || currentState.endCursor == null) return

        _state.update { it.copy(isLoadingMore = true) }
        viewModelScope.launch {
            getProductsPaginatedUseCase(count = 50, after = currentState.endCursor).collect { result ->
                when (result) {
                    is Result.Loading -> { /* Handled by isLoadingMore state */ }
                    is Result.Success -> {
                        allProductsStateFlow.value = allProductsStateFlow.value + result.data.products
                        val categories = allProductsStateFlow.value.map { it.productType }.distinct().filter { it.isNotBlank() }.sorted()
                        val subCategories = allProductsStateFlow.value.flatMap { it.tags }.distinct().filter { it.isNotBlank() }.sorted()
                        val brands = allProductsStateFlow.value.map { it.vendor }.distinct().filter { it.isNotBlank() }.sorted()
                        _state.update {
                            it.copy(
                                availableCategories = categories,
                                availableSubCategories = subCategories,
                                availableBrands = brands,
                                hasNextPage = result.data.hasNextPage,
                                endCursor = result.data.endCursor,
                                isLoadingMore = false
                            )
                        }
                    }
                    is Result.Failure -> {
                        _state.update { it.copy(isLoadingMore = false) }
                    }
                }
            }
        }
    }

    private fun applyAll() {
        if (!isLoaded) return
        val state = _state.value
        val filteredProducts = filterManager.apply(
            products = allProducts,
            query = state.searchQuery,
            filterState = state.filterState,
            sortOption = state.sortOption
        )
        _state.update { it.copy(screenState = AllProductsContract.ScreenState.Success(filteredProducts)) }
    }

    private fun onSearchQueryChanged(query: String) {
        _state.update { it.copy(searchQuery = query, isSearchActive = true) }
        applyAll()
    }

    private fun onPendingBrandToggled(brand: String) {
        _state.update { s ->
            val current = s.pendingFilterState.selectedBrands.toMutableSet()
            if (current.contains(brand)) current.remove(brand) else current.add(brand)
            s.copy(pendingFilterState = s.pendingFilterState.copy(selectedBrands = current))
        }
    }

    private fun clearLegacyBrandFilter() {
        _state.update {
            it.copy(
                activeBrand = null,
                filterState = it.filterState.copy(selectedBrands = emptySet()),
                pendingFilterState = it.pendingFilterState.copy(selectedBrands = emptySet())
            )
        }
        applyAll()
    }

    private fun selectCategory(category: String?) {
        _state.update {
            val cats = if (category != null) setOf(category) else emptySet()
            it.copy(
                filterState = it.filterState.copy(selectedCategories = cats),
                pendingFilterState = it.pendingFilterState.copy(selectedCategories = cats)
            )
        }
        applyAll()
    }

    private fun applyFilters() {
        // commit pending → active, save search term to recent
        val query = _state.value.searchQuery.trim()
        _state.update { s ->
            val recent = if (query.isNotEmpty()) {
                (listOf(query) + s.recentSearches).distinct().take(5)
            } else s.recentSearches
            val newFilterState = s.pendingFilterState.copy(brandSearchQuery = "")
            val isBrandStillActive = s.activeBrand != null &&
                    newFilterState.selectedBrands.contains(s.activeBrand) &&
                    newFilterState.selectedBrands.size == 1
            val isSubCategoryStillActive = s.activeSubCategory != null &&
                    newFilterState.selectedSubCategories.contains(s.activeSubCategory) &&
                    newFilterState.selectedSubCategories.size == 1
            s.copy(
                filterState = newFilterState,
                isFilterSheetOpen = false,
                recentSearches = recent,
                activeBrand = if (isBrandStillActive) s.activeBrand else null,
                activeSubCategory = if (isSubCategoryStillActive) s.activeSubCategory else null
            )
        }
        applyAll()
    }

    private fun resetFilters() {
        _state.update { it.copy(pendingFilterState = FilterState()) }
    }

    private fun applySort() {
        _state.update { it.copy(sortOption = it.pendingSortOption, isSortSheetOpen = false) }
        applyAll()
    }

    private fun emitEffect(effect: AllProductsContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
