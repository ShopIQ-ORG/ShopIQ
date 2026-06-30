package com.iti.presentation.screens.products.displayallproducts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.models.Product
import com.iti.domain.models.Result
import com.iti.domain.models.User
import com.iti.domain.usecases.auth.GetCurrentUserUseCase
import com.iti.domain.usecases.products.AddProductToFavoritesUseCase
import com.iti.domain.usecases.products.GetFavoriteProductsUseCase
import com.iti.domain.usecases.products.GetProductsByNumberUseCase
import com.iti.domain.usecases.products.RemoveProductFromFavoritesUseCase
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

class AllProductsViewModel(
    private val getProductsByNumberUseCase: GetProductsByNumberUseCase,
    private val addProductToFavoritesUseCase: AddProductToFavoritesUseCase,
    private val removeProductFromFavoritesUseCase: RemoveProductFromFavoritesUseCase,
    private val getFavoriteProductsUseCase: GetFavoriteProductsUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AllProductsContract.State())
    val state: StateFlow<AllProductsContract.State> = _state.asStateFlow()

    private val _effect = Channel<AllProductsContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private val favoriteOverrides = MutableStateFlow<Map<String, Boolean>>(emptyMap())

    private val _activeBrand = MutableStateFlow<String?>(null)
    private var isDataLoaded = false

    fun sendIntent(intent: AllProductsContract.Intent) {
        when (intent) {
            is AllProductsContract.Intent.LoadData -> {
                if (!isDataLoaded) {
                    _activeBrand.value = intent.brandName
                    load()
                    isDataLoaded = true
                }
            }
            is AllProductsContract.Intent.Retry -> load()
            is AllProductsContract.Intent.ClearFilter -> _activeBrand.value = null
            is AllProductsContract.Intent.ProductClicked -> emitEffect(
                AllProductsContract.Effect.NavigateToProduct(intent.product.id)
            )
            is AllProductsContract.Intent.ProductFavoriteClicked -> toggleFavorite(intent.product)
        }
    }

    private fun toggleFavorite(product: Product) {
        viewModelScope.launch {
            val userResult = getCurrentUserUseCase()
            if (userResult is Result.Success && userResult.data is User.GuestUser) {
                emitEffect(AllProductsContract.Effect.ShowAuthRequired)
                return@launch
            }

            val productId = product.id
            val isFavorite = product.isFavorite

            try {
                // Optimistic update via override map
                favoriteOverrides.update { it + (productId to !isFavorite) }

                if (isFavorite) {
                    removeProductFromFavoritesUseCase(productId)
                } else {
                    addProductToFavoritesUseCase(product)
                }
                
                // Clear override after a short delay
                kotlinx.coroutines.delay(500)
                favoriteOverrides.update { it - productId }
            } catch (e: Exception) {
                // Revert optimistic update
                favoriteOverrides.update { it - productId }
            }
        }
    }

    private fun load() {
        viewModelScope.launch {
            combine(
                getProductsByNumberUseCase(),
                getFavoriteProductsUseCase(),
                _activeBrand,
                favoriteOverrides
            ) { productsResult, favoritesResult, brandName, overrides ->
                _state.update { it.copy(activeBrand = brandName) }
                
                when (productsResult) {
                    is Result.Loading -> AllProductsContract.ScreenState.Loading
                    is Result.Success -> {
                        val allProducts = productsResult.data
                        val favoriteIds = if (favoritesResult is Result.Success) {
                            favoritesResult.data.map { it.id }.toSet()
                        } else {
                            emptySet()
                        }
                        
                        val productsWithFavorites = allProducts.map { product ->
                            val isFavoriteInDb = product.id in favoriteIds
                            val isFavorite = overrides[product.id] ?: isFavoriteInDb
                            product.copy(isFavorite = isFavorite)
                        }
                        
                        val filtered = if (brandName != null) {
                            productsWithFavorites.filter { it.vendor.equals(brandName, ignoreCase = true) }
                        } else {
                            productsWithFavorites
                        }
                        AllProductsContract.ScreenState.Success(filtered)
                    }
                    is Result.Failure -> AllProductsContract.ScreenState.Failure(
                        productsResult.exception.message
                            ?.let { msg -> UiText.Plain(msg) }
                            ?: UiText.StringResource(R.string.error_loading_products)
                    )
                }
            }.collect { screenState ->
                _state.update { it.copy(screenState = screenState) }
            }
        }
    }

    private fun emitEffect(effect: AllProductsContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
