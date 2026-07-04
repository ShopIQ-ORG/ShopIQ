package com.iti.presentation.screens.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.models.Product
import com.iti.domain.models.Result
import com.iti.domain.usecases.ai.GetChatHistoryUseCase
import com.iti.domain.usecases.auth.GetCurrentUserUseCase
import com.iti.domain.usecases.products.AddProductToFavoritesUseCase
import com.iti.domain.usecases.products.GetAdsUseCase
import com.iti.domain.usecases.auth.LogoutUseCase
import com.iti.domain.usecases.products.GetBrandsUseCase
import com.iti.domain.usecases.products.GetFavoriteProductsUseCase
import com.iti.domain.usecases.products.GetProductsByNumberUseCase
import com.iti.domain.usecases.products.RemoveProductFromFavoritesUseCase
import com.iti.domain.repositories.auth.AuthRepository
import com.iti.domain.repositories.products.ProductsRepository
import com.iti.presentation.R
import com.iti.presentation.screens.home.HomeContract
import com.iti.presentation.util.UiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getProductsByNumberUseCase: GetProductsByNumberUseCase,
    private val getBrandsUseCase: GetBrandsUseCase,
    private val getAdsUseCase: GetAdsUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val addProductToFavoritesUseCase: AddProductToFavoritesUseCase,
    private val removeProductFromFavoritesUseCase: RemoveProductFromFavoritesUseCase,
    private val getFavoriteProductsUseCase: GetFavoriteProductsUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val authRepository: AuthRepository,
    private val getChatHistoryUseCase: GetChatHistoryUseCase,
    private val productsRepository: ProductsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeContract.State())
    val state: StateFlow<HomeContract.State> = _state.asStateFlow()

    private val _effect = Channel<HomeContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    // To track local favorite overrides during async DB updates to prevent flickering
    private val favoriteOverrides = MutableStateFlow<Map<String, Boolean>>(emptyMap())

    init {
        loadCurrentUser()
        sendIntent(HomeContract.Intent.LoadData)
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            val result = getCurrentUserUseCase()
            if (result is Result.Success) {
                val user = result.data
                _state.update { it.copy(currentUser = user) }
                if (user is com.iti.domain.models.User.AuthenticatedUser) {
                    loadAiRecommendations(user.uid)
                }
            }
        }
    }

    private fun loadAiRecommendations(userId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingRecommendations = true) }
            try {
                // Take the first emission (snapshot) of chat history
                val historyResult = getChatHistoryUseCase(userId).first {
                    it !is Result.Loading
                }

                if (historyResult !is Result.Success) {
                    _state.update { it.copy(isLoadingRecommendations = false) }
                    return@launch
                }

                // Collect unique recommended product IDs from last 30 AI messages
                val recommendedIds = historyResult.data
                    .filter { it.sender == "ai" && it.recommendedProductIds.isNotEmpty() }
                    .takeLast(30)
                    .flatMap { it.recommendedProductIds }
                    .distinct()
                    .take(10) // Cap at 10 recommendations

                if (recommendedIds.isEmpty()) {
                    _state.update { it.copy(isLoadingRecommendations = false) }
                    return@launch
                }

                // Resolve IDs to Product objects
                val resolvedProducts = mutableListOf<Product>()
                for (rawId in recommendedIds) {
                    try {
                        val numericId = rawId.substringAfterLast("/").toLongOrNull() ?: continue
                        val productResult = productsRepository.getProductDetails(numericId).first {
                            it !is Result.Loading
                        }
                        if (productResult is Result.Success) {
                            resolvedProducts.add(productResult.data)
                        }
                    } catch (_: Exception) { /* skip failed product */ }
                }

                _state.update {
                    it.copy(
                        aiRecommendedProducts = resolvedProducts,
                        isLoadingRecommendations = false
                    )
                }
            } catch (_: Exception) {
                _state.update { it.copy(isLoadingRecommendations = false) }
            }
        }
    }

    fun sendIntent(intent: HomeContract.Intent) {
        when (intent) {
            is HomeContract.Intent.LoadData,
            is HomeContract.Intent.Retry -> loadAll()

            is HomeContract.Intent.ProductClicked -> {
                val productId = intent.product.id.substringAfterLast("/").toLong()
                emitEffect(HomeContract.Effect.NavigateToProduct(productId))
            }

            is HomeContract.Intent.AiRecommendedProductClicked -> {
                val productId = intent.product.id.substringAfterLast("/").toLong()
                emitEffect(HomeContract.Effect.NavigateToProduct(productId))
            }

            is HomeContract.Intent.NavigateToAiChat ->
                emitEffect(HomeContract.Effect.NavigateToAiChat)

            is HomeContract.Intent.ProductFavoriteClicked -> toggleFavorite(intent.product)

            is HomeContract.Intent.BrandClicked ->
                emitEffect(HomeContract.Effect.NavigateToProducts(intent.brandName))

            is HomeContract.Intent.AdClicked ->
                emitEffect(HomeContract.Effect.NavigateToProducts(intent.ad.subtitle))

            is HomeContract.Intent.ViewAllBrandsClicked ->
                emitEffect(HomeContract.Effect.NavigateToAllBrands())

            is HomeContract.Intent.ViewAllProductsClicked ->
                emitEffect(HomeContract.Effect.NavigateToAllProducts)

            is HomeContract.Intent.SearchBarClicked ->
                emitEffect(HomeContract.Effect.NavigateToSearch)

            is HomeContract.Intent.Logout -> logout()
        }
    }

    private fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            emitEffect(HomeContract.Effect.NavigateToSignIn)
        }
    }

    private fun toggleFavorite(product: com.iti.domain.models.Product) {
        val userId = authRepository.getUserId()
        if (userId == null || userId == "guest") {
            emitEffect(HomeContract.Effect.ShowAuthRequired)
            return
        }

        viewModelScope.launch {
            val productId = product.id
            val isFavorite = product.isFavorite
            try {
                favoriteOverrides.update { it + (productId to !isFavorite) }
                if (isFavorite) {
                    removeProductFromFavoritesUseCase(productId)
                } else {
                    addProductToFavoritesUseCase(product)
                }
                kotlinx.coroutines.delay(1000)
                favoriteOverrides.update { it - productId }
            } catch (_: Exception) {
                favoriteOverrides.update { it - productId }
            }
        }
    }

    private fun loadAll() {
        _state.update { it.copy(screenState = HomeContract.ScreenState.Loading) }
        viewModelScope.launch {
            combine(
                getProductsByNumberUseCase(),
                getBrandsUseCase(),
                getAdsUseCase(),
                getFavoriteProductsUseCase(),
                favoriteOverrides
            ) { productsResult, brandsResult, adsResult, favoritesResult, overrides ->
                val anyLoading = productsResult is Result.Loading
                        || brandsResult is Result.Loading
                        || adsResult is Result.Loading

                when {
                    anyLoading -> HomeContract.ScreenState.Loading

                    productsResult is Result.Failure -> HomeContract.ScreenState.Failure(
                        productsResult.exception.message
                            ?.let { UiText.Plain(it) }
                            ?: UiText.StringResource(R.string.error_loading_products)
                    )

                    brandsResult is Result.Failure -> HomeContract.ScreenState.Failure(
                        brandsResult.exception.message
                            ?.let { UiText.Plain(it) }
                            ?: UiText.StringResource(R.string.error_loading_brands)
                    )

                    adsResult is Result.Failure -> HomeContract.ScreenState.Failure(
                        adsResult.exception.message
                            ?.let { UiText.Plain(it) }
                            ?: UiText.StringResource(R.string.error_loading_ads)
                    )

                    else -> {
                        val products = (productsResult as Result.Success).data
                        val favoriteIds = if (favoritesResult is Result.Success) {
                            favoritesResult.data.map { it.id }.toSet()
                        } else {
                            emptySet()
                        }
                        val updatedProducts = products.map { product ->
                            val isFavoriteInDb = product.id in favoriteIds
                            val isFavorite = overrides[product.id] ?: isFavoriteInDb
                            product.copy(isFavorite = isFavorite)
                        }
                        HomeContract.ScreenState.Success(
                            HomeContract.HomeData(
                                products = updatedProducts,
                                brands = (brandsResult as Result.Success).data,
                                ads = (adsResult as Result.Success).data
                            )
                        )
                    }
                }
            }.collect { screenState ->
                _state.update { it.copy(screenState = screenState) }
            }
        }
    }

    private fun emitEffect(effect: HomeContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
