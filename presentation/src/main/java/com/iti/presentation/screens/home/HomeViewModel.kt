package com.iti.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.models.Result
import com.iti.domain.models.User
import com.iti.domain.usecases.auth.GetCurrentUserUseCase
import com.iti.domain.usecases.products.AddProductToFavoritesUseCase
import com.iti.domain.usecases.products.GetAdsUseCase
import com.iti.domain.usecases.auth.LogoutUseCase
import com.iti.domain.usecases.products.GetBrandsUseCase
import com.iti.domain.usecases.products.GetFavoriteProductsUseCase
import com.iti.domain.usecases.products.GetProductsByNumberUseCase
import com.iti.domain.usecases.products.RemoveProductFromFavoritesUseCase
import com.iti.domain.repositories.auth.AuthRepository
import com.iti.presentation.R
import com.iti.presentation.core.UiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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
    private val authRepository: AuthRepository
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
                _state.update { it.copy(currentUser = result.data) }
            }
        }
    }

    fun sendIntent(intent: HomeContract.Intent) {
        when (intent) {
            is HomeContract.Intent.LoadData,
            is HomeContract.Intent.Retry -> loadAll()
            is HomeContract.Intent.ProductClicked -> {
                val productId = intent.product.id.substringAfterLast("/").toLong()

                emitEffect(
                    HomeContract.Effect.NavigateToProduct(productId)
                )
            }
            is HomeContract.Intent.ProductFavoriteClicked -> toggleFavorite(intent.product)
            is HomeContract.Intent.BrandClicked -> emitEffect(
                HomeContract.Effect.NavigateToProducts(intent.brandName)
            )
            is HomeContract.Intent.ViewAllBrandsClicked -> emitEffect(
                HomeContract.Effect.NavigateToAllBrands()
            )
            is HomeContract.Intent.ViewAllProductsClicked -> emitEffect(
                HomeContract.Effect.NavigateToAllProducts
            )
            is HomeContract.Intent.Logout -> logout()
        }
    }

    private fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            emitEffect(HomeContract.Effect.NavigateToSplash)
        }
    }

    private fun toggleFavorite(product: com.iti.domain.models.Product) {
        // FAST check for guest status using cached UID
        val userId = authRepository.getUserId()
        if (userId == null || userId == "guest") {
            emitEffect(HomeContract.Effect.ShowAuthRequired)
            return
        }

        viewModelScope.launch {
            val productId = product.id
            val isFavorite = product.isFavorite
            
            try {
                // Optimistic update via override map - happens IMMEDIATELY
                favoriteOverrides.update { it + (productId to !isFavorite) }

                if (isFavorite) {
                    removeProductFromFavoritesUseCase(productId)
                } else {
                    addProductToFavoritesUseCase(product)
                }
                
                // Keep the override for a bit to ensure the flow collection has caught up
                kotlinx.coroutines.delay(1000)
                favoriteOverrides.update { it - productId }
            } catch (e: Exception) {
                // Revert optimistic update
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
