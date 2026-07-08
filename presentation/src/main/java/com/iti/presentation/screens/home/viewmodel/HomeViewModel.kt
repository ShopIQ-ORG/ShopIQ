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
import com.iti.domain.usecases.products.GetBestSellersUseCase
import com.iti.domain.usecases.products.GetProductDetailsUseCase
import com.iti.domain.usecases.products.SearchProductsUseCase
import com.iti.domain.usecases.products.GetProductTranslationsUseCase
import com.iti.domain.repositories.auth.AuthRepository
import com.iti.presentation.R
import com.iti.presentation.components.ShopIQSnackbarType
import com.iti.presentation.screens.home.HomeContract
import com.iti.presentation.util.UiText
import com.iti.presentation.util.Stopwords
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
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
    private val getBestSellersUseCase: GetBestSellersUseCase,
    private val getProductDetailsUseCase: GetProductDetailsUseCase,
    private val searchProductsUseCase: SearchProductsUseCase,
    private val getProductTranslationsUseCase: GetProductTranslationsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeContract.State())
    val state: StateFlow<HomeContract.State> = _state.asStateFlow()

    private val _effect = Channel<HomeContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    data class AiRecommendationsState(
        val recommendedProducts: List<Product> = emptyList(),
        val hasChatHistory: Boolean = false,
        val isLoaded: Boolean = false
    )

    private val aiRecommendationsFlow = MutableStateFlow<AiRecommendationsState?>(null)

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
                    observeChatHistory(user.uid)
                } else {
                    aiRecommendationsFlow.value = AiRecommendationsState(isLoaded = true)
                }
            } else {
                aiRecommendationsFlow.value = AiRecommendationsState(isLoaded = true)
            }
        }
    }

    private fun observeChatHistory(userId: String) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                getChatHistoryUseCase(userId).collect { historyResult ->
                    if (historyResult is Result.Success) {
                        val messages = historyResult.data

                        val hasHistory = messages.any { it.sender == "user" }

                        val sortedMessages = messages.sortedByDescending { it.timestamp }

                        val recommendedIds = sortedMessages
                            .filter { it.sender == "ai" && it.recommendedProductIds.isNotEmpty() }
                            .flatMap { it.recommendedProductIds }
                            .distinct()
                            .take(10)

                        val keywords = sortedMessages
                            .filter { it.sender == "user" }
                            .flatMap { msg ->
                                msg.text.lowercase()
                                    .replace(Regex("[.,?!()\\-\"\\/]"), " ")
                                    .split("\\s+".toRegex())
                                    .map { it.trim() }
                                    .filter { word -> word.length > 2 && word !in Stopwords.set }
                            }
                            .distinct()

                        val resolvedProducts = mutableListOf<Product>()

                        coroutineScope {

                            val recommendationJobs = if (recommendedIds.isNotEmpty()) {
                                recommendedIds.map { rawId ->
                                    async {
                                        try {
                                            val numericId = rawId.substringAfterLast("/").toLongOrNull() ?: return@async null
                                            val res = getProductDetailsUseCase(numericId)
                                                .first { it !is Result.Loading }
                                            if (res is Result.Success) res.data else null
                                        } catch (_: Exception) {
                                            null
                                        }
                                    }
                                }
                            } else emptyList()

                            val searchJobs = if (keywords.isNotEmpty()) {
                                keywords.takeLast(3).map { keyword ->
                                    async {
                                        try {
                                            val res = searchProductsUseCase(keyword)
                                                .first { it !is Result.Loading }
                                            if (res is Result.Success) res.data else emptyList()
                                        } catch (_: Exception) {
                                            emptyList()
                                        }
                                    }
                                }
                            } else emptyList()

                            val recommendedList = recommendationJobs.awaitAll().filterNotNull()
                            resolvedProducts.addAll(recommendedList)

                            val searchList = searchJobs.awaitAll().flatten()
                            resolvedProducts.addAll(searchList)
                        }

                        val finalProducts = resolvedProducts.distinctBy { it.id }.take(10)

                        val productsToEmit = if (java.util.Locale.getDefault().language == "ar") {
                            finalProducts.map { product ->
                                try {
                                    val transResult = getProductTranslationsUseCase(product.id)
                                        .first { it !is Result.Loading }
                                    if (transResult is Result.Success) {
                                        val map = transResult.data
                                        product.copy(
                                            arTitle = map["title"]?.takeIf { it.isNotBlank() },
                                            arDescription = (map["body_html"] ?: map["description"])?.takeIf { it.isNotBlank() }
                                        )
                                    } else product
                                } catch (_: Exception) {
                                    product
                                }
                            }
                        } else {
                            finalProducts
                        }

                        aiRecommendationsFlow.value = AiRecommendationsState(
                            recommendedProducts = productsToEmit,
                            hasChatHistory = hasHistory,
                            isLoaded = true
                        )
                    } else if (historyResult is Result.Failure) {
                        aiRecommendationsFlow.value = AiRecommendationsState(isLoaded = true)
                    }
                }
            } catch (_: Exception) {
                aiRecommendationsFlow.value = AiRecommendationsState(isLoaded = true)
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
                emitEffect(HomeContract.Effect.NavigateToProducts(brandName = intent.brandName))

            is HomeContract.Intent.SubCategoryClicked ->
                emitEffect(HomeContract.Effect.NavigateToProducts(subCategoryName = intent.subCategoryName))

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

    private fun toggleFavorite(product: Product) {
        if (authRepository.isGuest()) {
            emitEffect(HomeContract.Effect.ShowAuthRequired)
            return
        }

        viewModelScope.launch {
            val productId = product.id
            val isFavorite = product.isFavorite
            try {
                favoriteOverrides.update { it + (productId to !isFavorite) }
                _state.update { it.copy(isFavoriteLoading = true) }

                if (isFavorite) {
                    removeProductFromFavoritesUseCase(productId)
                    emitEffect(
                        HomeContract.Effect.ShowToast(
                            message = UiText.StringResource(R.string.removed_from_wishlist),
                            type = ShopIQSnackbarType.Success
                        )
                    )
                } else {
                    addProductToFavoritesUseCase(product)
                    emitEffect(
                        HomeContract.Effect.ShowToast(
                            message = UiText.StringResource(R.string.added_to_wishlist),
                            type = ShopIQSnackbarType.Success
                        )
                    )
                }

                _state.update { it.copy(isFavoriteLoading = false) }
                favoriteOverrides.update { it - productId }
            } catch (_: Exception) {
                favoriteOverrides.update { it - productId }
                _state.update { it.copy(isFavoriteLoading = false) }
                emitEffect(
                    HomeContract.Effect.ShowToast(
                        message = UiText.Plain("Failed to update Wishlist"),
                        type = ShopIQSnackbarType.Error
                    )
                )
            }
        }
    }

    private fun loadAll() {
        _state.update { it.copy(screenState = HomeContract.ScreenState.Loading) }
        viewModelScope.launch {
            val flow1 = combine(
                getProductsByNumberUseCase(),
                getBrandsUseCase(),
                getAdsUseCase()
            ) { p, b, a -> Triple(p, b, a) }

            val flow2 = combine(
                getFavoriteProductsUseCase(),
                getBestSellersUseCase(50),
                favoriteOverrides
            ) { f, s, o -> Triple(f, s, o) }

            val mainDataFlow = combine(flow1, flow2) { (productsResult, brandsResult, adsResult), (favoritesResult, bestSellersResult, overrides) ->
                val processedBestSellers = if (bestSellersResult is Result.Success) {
                    Result.Success(bestSellersResult.data.takeLast(10))
                } else {
                    bestSellersResult
                }
                HomeContract.MainDataHolder(
                    productsResult = productsResult,
                    brandsResult = brandsResult,
                    adsResult = adsResult,
                    favoritesResult = favoritesResult,
                    bestSellersResult = processedBestSellers,
                    overrides = overrides
                )
            }

            combine(mainDataFlow, aiRecommendationsFlow) { mainData, recommendationsState ->
                val anyLoading = mainData.productsResult is Result.Loading
                        || mainData.brandsResult is Result.Loading
                        || mainData.adsResult is Result.Loading
                        || recommendationsState == null

                val screenState = when {
                    anyLoading -> HomeContract.ScreenState.Loading

                    mainData.productsResult is Result.Failure -> HomeContract.ScreenState.Failure(
                        mainData.productsResult.exception.message
                            ?.let { UiText.Plain(it) }
                            ?: UiText.StringResource(R.string.error_loading_products)
                    )

                    mainData.brandsResult is Result.Failure -> HomeContract.ScreenState.Failure(
                        mainData.brandsResult.exception.message
                            ?.let { UiText.Plain(it) }
                            ?: UiText.StringResource(R.string.error_loading_brands)
                    )

                    mainData.adsResult is Result.Failure -> HomeContract.ScreenState.Failure(
                        mainData.adsResult.exception.message
                            ?.let { UiText.Plain(it) }
                            ?: UiText.StringResource(R.string.error_loading_ads)
                    )

                    else -> {
                        val products = (mainData.productsResult as Result.Success).data
                        val favoriteIds = if (mainData.favoritesResult is Result.Success) {
                            mainData.favoritesResult.data.map { it.id }.toSet()
                        } else {
                            emptySet()
                        }
                        val updatedProducts = products.map { product ->
                            val isFavoriteInDb = product.id in favoriteIds
                            val isFavorite = mainData.overrides[product.id] ?: isFavoriteInDb
                            product.copy(isFavorite = isFavorite)
                        }

                        val bestSellers = if (mainData.bestSellersResult is Result.Success) {
                            mainData.bestSellersResult.data
                        } else emptyList()

                        val updatedBestSellers = bestSellers.map { product ->
                            val isFavoriteInDb = product.id in favoriteIds
                            val isFavorite = mainData.overrides[product.id] ?: isFavoriteInDb
                            product.copy(isFavorite = isFavorite)
                        }

                        HomeContract.ScreenState.Success(
                            HomeContract.HomeData(
                                products = updatedProducts,
                                brands = (mainData.brandsResult as Result.Success).data,
                                ads = (mainData.adsResult as Result.Success).data,
                                bestSellers = updatedBestSellers
                            )
                        ).also {
                            // Batch-fetch Arabic translations if device locale is AR
                            if (java.util.Locale.getDefault().language == "ar") {
                                fetchTranslationsForProducts(updatedProducts + updatedBestSellers)
                            }
                        }
                    }
                }
                Pair(screenState, recommendationsState)
            }.collect { (screenState, recommendationsState) ->
                _state.update { currentState ->
                    currentState.copy(
                        screenState = screenState,
                        aiRecommendedProducts = recommendationsState?.recommendedProducts ?: emptyList(),
                        hasChatHistory = recommendationsState?.hasChatHistory ?: false
                    )
                }
            }
        }
    }

    private fun emitEffect(effect: HomeContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
    /**
     * Concurrently fetches Arabic translations for a batch of products and updates
     * the products list in the state so that [ProductCard] shows translated titles.
     */
    private fun fetchTranslationsForProducts(products: List<Product>) {
        viewModelScope.launch {
            val translatedMap = products.distinctBy { it.id }.map { product ->
                async {
                    try {
                        val result = getProductTranslationsUseCase(product.id)
                            .first { it !is Result.Loading }
                        if (result is Result.Success) {
                            val map = result.data
                            product.id to product.copy(
                                arTitle = map["title"]?.takeIf { it.isNotBlank() },
                                arDescription = (map["body_html"] ?: map["description"])?.takeIf { it.isNotBlank() }
                            )
                        } else product.id to product
                    } catch (_: Exception) {
                        product.id to product
                    }
                }
            }.awaitAll().toMap()

            _state.update { currentState ->
                val screenState = currentState.screenState
                if (screenState is HomeContract.ScreenState.Success) {
                    val data = screenState.data
                    screenState.copy(
                        data = data.copy(
                            products = data.products.map { translatedMap[it.id] ?: it },
                            bestSellers = data.bestSellers.map { translatedMap[it.id] ?: it }
                        )
                    ).let { newScreenState ->
                        currentState.copy(screenState = newScreenState)
                    }
                } else currentState
            }
        }
    }
}
