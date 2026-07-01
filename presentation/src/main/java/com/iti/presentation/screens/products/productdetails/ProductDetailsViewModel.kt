package com.iti.presentation.screens.products.productdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.models.Result
import com.iti.domain.models.User
import com.iti.domain.usecases.auth.GetCurrentUserUseCase
import com.iti.domain.usecases.products.AddProductToFavoritesUseCase
import com.iti.domain.usecases.products.GetProductDetailsUseCase
import com.iti.domain.usecases.products.GetFavoriteProductsUseCase
import com.iti.domain.usecases.products.RemoveProductFromFavoritesUseCase
import com.iti.domain.repositories.auth.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductDetailsViewModel(
    private val getProductDetailsUseCase: GetProductDetailsUseCase,
    private val addProductToFavoritesUseCase: AddProductToFavoritesUseCase,
    private val removeProductFromFavoritesUseCase: RemoveProductFromFavoritesUseCase,
    private val getFavoriteProductsUseCase: GetFavoriteProductsUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProductDetailsUiState())
    val state: StateFlow<ProductDetailsUiState> = _state.asStateFlow()

    private val _sideEffects = MutableSharedFlow<ProductDetailsSideEffect>()
    val sideEffects: SharedFlow<ProductDetailsSideEffect> = _sideEffects.asSharedFlow()

    private val favoriteOverride = MutableStateFlow<Boolean?>(null)

    fun handleIntent(intent: ProductDetailsIntent) {
        when (intent) {
            is ProductDetailsIntent.LoadProductDetails -> loadProductDetails(intent.productId)
            is ProductDetailsIntent.SelectColor -> selectColor(intent.color)
            is ProductDetailsIntent.SelectSize -> selectSize(intent.size)
            is ProductDetailsIntent.SelectImage -> selectImage(intent.index)
            is ProductDetailsIntent.ToggleWishlist -> toggleWishlist()
            is ProductDetailsIntent.AddToCart -> addToCart()
        }
    }

    private fun loadProductDetails(productId: Long) {
        viewModelScope.launch {
            getProductDetailsUseCase(productId).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _state.update { it.copy(isLoading = true, product = null, error = null) }
                    }
                    is Result.Success -> {
                        val product = result.data
                        
                        // Start observing favorites immediately if user is signed in
                        val userId = authRepository.getUserId()
                        if (userId != null && userId != "guest") {
                            observeFavoriteStatus(product.id)
                        }

                        _state.update {
                            it.copy(
                                isLoading = false,
                                product = product,
                                // Default selection logic
                                selectedColor = "Beige", // default as per mockup
                                selectedSize = "M",      // default as per mockup
                                selectedImageIndex = 0
                            )
                        }
                    }
                    is Result.Failure -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = result.exception.message ?: "Unknown error"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun observeFavoriteStatus(productId: String) {
        viewModelScope.launch {
            combine(
                getFavoriteProductsUseCase(),
                favoriteOverride
            ) { result: Result<List<com.iti.domain.models.Product>>, override: Boolean? ->
                if (result is Result.Success) {
                    val isFavoriteInDb = result.data.any { it.id == productId }
                    _state.update { it.copy(isWishlisted = override ?: isFavoriteInDb) }
                }
            }.collect()
        }
    }

    private fun selectColor(color: String) {
        _state.update { it.copy(selectedColor = color) }
    }

    private fun selectSize(size: String) {
        _state.update { it.copy(selectedSize = size) }
    }

    private fun selectImage(index: Int) {
        _state.update { it.copy(selectedImageIndex = index) }
    }

    private fun toggleWishlist() {
        // FAST check for guest status
        val userId = authRepository.getUserId()
        if (userId == null || userId == "guest") {
            viewModelScope.launch {
                _sideEffects.emit(ProductDetailsSideEffect.NavigateToAuth)
            }
            return
        }

        val currentProduct = _state.value.product ?: return
        val currentlyWishlisted = _state.value.isWishlisted
        
        viewModelScope.launch {
            try {
                val newStatus = !currentlyWishlisted
                // Optimistic update via override - happens IMMEDIATELY
                favoriteOverride.value = newStatus

                if (currentlyWishlisted) {
                    removeProductFromFavoritesUseCase(currentProduct.id)
                } else {
                    addProductToFavoritesUseCase(currentProduct)
                }
                
                val message = if (newStatus) "Added to Wishlist" else "Removed from Wishlist"
                _sideEffects.emit(ProductDetailsSideEffect.ShowToast(message))
                
                // Keep the override for a bit
                kotlinx.coroutines.delay(1000)
                favoriteOverride.value = null
            } catch (e: Exception) {
                // Revert optimistic update
                favoriteOverride.value = null
                _sideEffects.emit(ProductDetailsSideEffect.ShowToast("Error: ${e.message}"))
            }
        }
    }

    private fun addToCart() {
        viewModelScope.launch {
            _sideEffects.emit(ProductDetailsSideEffect.ShowToast("Added to Cart!"))
        }
    }
}
