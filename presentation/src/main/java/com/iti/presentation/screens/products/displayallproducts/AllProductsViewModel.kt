package com.iti.presentation.screens.products.displayallproducts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.models.Product
import com.iti.domain.models.Result
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
    private val getFavoriteProductsUseCase: GetFavoriteProductsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AllProductsContract.State())
    val state: StateFlow<AllProductsContract.State> = _state.asStateFlow()

    private val _effect = Channel<AllProductsContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

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
            try {
                val isCurrentlyFavorite = product.isFavorite
                // Optimistic update
                updateProductFavoriteStatus(product.id, !isCurrentlyFavorite)

                if (isCurrentlyFavorite) {
                    removeProductFromFavoritesUseCase(product.id)
                } else {
                    addProductToFavoritesUseCase(product)
                }
            } catch (e: Exception) {
                // Revert optimistic update on failure
                updateProductFavoriteStatus(product.id, product.isFavorite)
            }
        }
    }

    private fun updateProductFavoriteStatus(productId: String, isFavorite: Boolean) {
        _state.update { currentState ->
            if (currentState.screenState is AllProductsContract.ScreenState.Success) {
                val updatedProducts = currentState.screenState.products.map {
                    if (it.id == productId) it.copy(isFavorite = isFavorite) else it
                }
                currentState.copy(
                    screenState = AllProductsContract.ScreenState.Success(updatedProducts)
                )
            } else {
                currentState
            }
        }
    }

    private fun load() {
        viewModelScope.launch {
            combine(
                getProductsByNumberUseCase(),
                getFavoriteProductsUseCase(),
                _activeBrand
            ) { productsResult, favoritesResult, brandName ->
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
                            product.copy(isFavorite = product.id in favoriteIds)
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
