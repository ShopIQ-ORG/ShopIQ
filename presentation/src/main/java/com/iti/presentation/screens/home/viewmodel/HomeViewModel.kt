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
import com.iti.domain.repositories.products.ProductsRepository
import com.iti.domain.repositories.auth.AuthRepository
import com.iti.presentation.R
import com.iti.presentation.screens.home.HomeContract
import com.iti.presentation.util.UiText
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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

    data class AiRecommendationsState(
        val recommendedProducts: List<Product> = emptyList(),
        val hasChatHistory: Boolean = false,
        val isLoaded: Boolean = false
    )

    private val aiRecommendationsFlow = MutableStateFlow<AiRecommendationsState?>(null)

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
                    observeChatHistory(user.uid)
                } else {
                    // Guest user - recommendations ready immediately (empty)
                    aiRecommendationsFlow.value = AiRecommendationsState(isLoaded = true)
                }
            } else {
                // User loading failed - treat as guest immediately to unblock screen loading
                aiRecommendationsFlow.value = AiRecommendationsState(isLoaded = true)
            }
        }
    }

    private fun observeChatHistory(userId: String) {
        android.util.Log.d("SuggestionsDebug", "observeChatHistory started for userId=$userId")
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                getChatHistoryUseCase(userId).collect { historyResult ->
                    when (historyResult) {
                        is Result.Loading -> {
                            android.util.Log.d("SuggestionsDebug", "Chat history: Loading...")
                        }
                        is Result.Failure -> {
                            android.util.Log.e("SuggestionsDebug", "Chat history FAILED: ${historyResult.exception.message}", historyResult.exception)
                            aiRecommendationsFlow.value = AiRecommendationsState(isLoaded = true)
                        }
                        is Result.Success -> {
                            val messages = historyResult.data
                            android.util.Log.d("SuggestionsDebug", "Chat history loaded. Total messages: ${messages.size}")
                            
                            // Check if the user has any chat history (at least one user message exists)
                            val hasHistory = messages.any { it.sender == "user" }
                            android.util.Log.d("SuggestionsDebug", "hasChatHistory determined: $hasHistory")

                            // Sort messages so that the latest chats are processed first
                            val sortedMessages = messages.sortedByDescending { it.timestamp }

                            sortedMessages.forEachIndexed { i, msg ->
                                android.util.Log.d("SuggestionsDebug", "  msg[$i] sender=${msg.sender} | text='${msg.text.take(60)}' | recommendedIds=${msg.recommendedProductIds}")
                            }

                            // 1. Collect unique product IDs that Eslam AI explicitly recommended (latest first)
                            val recommendedIds = sortedMessages
                                .filter { it.sender == "ai" && it.recommendedProductIds.isNotEmpty() }
                                .flatMap { it.recommendedProductIds }
                                .distinct()
                                .take(10)

                            android.util.Log.d("SuggestionsDebug", "Recommended IDs extracted: $recommendedIds")

                            // 2. Extract meaningful search keywords from user messages only (latest first)
                            val stopwords = setOf(
                                // Arabic stopwords
                                "أريد", "عن", "من", "في", "أبحث", "اريد", "ابحث", "هل",
                                "عندكم", "عندك", "عاوز", "عايز", "محتاج", "موجود", "يا",
                                "ما", "ماذا", "فيه", "ده", "دي", "هو", "هي", "انا",
                                // English stopwords
                                "i", "want", "search", "looking", "for", "do", "you",
                                "have", "need", "please", "the", "a", "an", "is", "are",
                                "to", "in", "of", "and", "that", "can", "get", "show",
                                "me", "any", "some", "find", "with", "like"
                            )

                            val keywords = sortedMessages
                                .filter { it.sender == "user" }
                                .flatMap { msg ->
                                    msg.text.lowercase()
                                        .replace(Regex("[.,?!()\\-\"\\/]"), " ")
                                        .split("\\s+".toRegex())
                                        .map { it.trim() }
                                        .filter { word -> word.length > 2 && word !in stopwords }
                                }
                                .distinct()

                            android.util.Log.d("SuggestionsDebug", "Search keywords extracted: $keywords")

                            val resolvedProducts = mutableListOf<Product>()

                            kotlinx.coroutines.coroutineScope {
                                // Resolve explicit ID recommendations in parallel
                                val recommendationJobs = if (recommendedIds.isNotEmpty()) {
                                    recommendedIds.map { rawId ->
                                        async {
                                            try {
                                                val numericId = rawId.substringAfterLast("/").toLongOrNull()
                                                if (numericId == null) {
                                                    android.util.Log.e("SuggestionsDebug", "  Cannot parse numeric ID from: $rawId")
                                                    return@async null
                                                }
                                                android.util.Log.d("SuggestionsDebug", "  Fetching product by ID numericId=$numericId (from $rawId)")
                                                val res = productsRepository.getProductDetails(numericId)
                                                    .first { it !is Result.Loading }
                                                when (res) {
                                                    is Result.Success -> {
                                                        android.util.Log.d("SuggestionsDebug", "  SUCCESS product title=${res.data.title}")
                                                        res.data
                                                    }
                                                    is Result.Failure -> {
                                                        android.util.Log.e("SuggestionsDebug", "  FAILED to fetch id=$numericId: ${res.exception.message}")
                                                        null
                                                    }
                                                    else -> null
                                                }
                                            } catch (e: Exception) {
                                                android.util.Log.e("SuggestionsDebug", "  Exception fetching product $rawId: ${e.message}", e)
                                                null
                                            }
                                        }
                                    }
                                } else emptyList()

                                // Search by keywords in parallel
                                val searchJobs = if (keywords.isNotEmpty()) {
                                    keywords.takeLast(3).map { keyword ->
                                        async {
                                            try {
                                                android.util.Log.d("SuggestionsDebug", "  Searching by keyword='$keyword'")
                                                val res = productsRepository.searchProducts(keyword)
                                                    .first { it !is Result.Loading }
                                                when (res) {
                                                    is Result.Success -> {
                                                        android.util.Log.d("SuggestionsDebug", "  Search '$keyword' found ${res.data.size} products: ${res.data.map { it.title }}")
                                                        res.data
                                                    }
                                                    is Result.Failure -> {
                                                        android.util.Log.e("SuggestionsDebug", "  Search '$keyword' FAILED: ${res.exception.message}")
                                                        emptyList()
                                                    }
                                                    else -> emptyList()
                                                }
                                            } catch (e: Exception) {
                                                android.util.Log.e("SuggestionsDebug", "  Exception searching '$keyword': ${e.message}", e)
                                                emptyList()
                                            }
                                        }
                                    }
                                } else emptyList()

                                // Wait and merge results
                                val recommendedList = recommendationJobs.awaitAll().filterNotNull()
                                resolvedProducts.addAll(recommendedList)

                                val searchList = searchJobs.awaitAll().flatten()
                                resolvedProducts.addAll(searchList)
                            }

                            // Remove duplicates and limit to 10
                            val finalProducts = resolvedProducts.distinctBy { it.id }.take(10)
                            android.util.Log.d("SuggestionsDebug", "Final resolved suggestions (${finalProducts.size}): ${finalProducts.map { it.title }}")

                            aiRecommendationsFlow.value = AiRecommendationsState(
                                recommendedProducts = finalProducts,
                                hasChatHistory = hasHistory,
                                isLoaded = true
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("SuggestionsDebug", "observeChatHistory outer exception: ${e.message}", e)
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

    private data class MainDataHolder(
        val productsResult: Result<List<Product>>,
        val brandsResult: Result<List<com.iti.domain.models.Brand>>,
        val adsResult: Result<List<com.iti.domain.models.Ad>>,
        val favoritesResult: Result<List<Product>>,
        val overrides: Map<String, Boolean>
    )

    private fun loadAll() {
        _state.update { it.copy(screenState = HomeContract.ScreenState.Loading) }
        viewModelScope.launch {
            val mainDataFlow = combine(
                getProductsByNumberUseCase(),
                getBrandsUseCase(),
                getAdsUseCase(),
                getFavoriteProductsUseCase(),
                favoriteOverrides
            ) { productsResult, brandsResult, adsResult, favoritesResult, overrides ->
                MainDataHolder(productsResult, brandsResult, adsResult, favoritesResult, overrides)
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

                        HomeContract.ScreenState.Success(
                            HomeContract.HomeData(
                                products = updatedProducts,
                                brands = (mainData.brandsResult as Result.Success).data,
                                ads = (mainData.adsResult as Result.Success).data
                            )
                        )
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
}
