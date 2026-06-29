package com.iti.presentation.screens.products.productdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.models.Result
import com.iti.domain.usecases.products.GetProductDetailsUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductDetailsViewModel(
    private val getProductDetailsUseCase: GetProductDetailsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProductDetailsUiState())
    val state: StateFlow<ProductDetailsUiState> = _state.asStateFlow()

    private val _sideEffects = MutableSharedFlow<ProductDetailsSideEffect>()
    val sideEffects: SharedFlow<ProductDetailsSideEffect> = _sideEffects.asSharedFlow()

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
                        _state.update {
                            it.copy(
                                isLoading = false,
                                product = result.data,
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
        _state.update { it.copy(isWishlisted = !it.isWishlisted) }
        viewModelScope.launch {
            val message = if (_state.value.isWishlisted) "Added to Wishlist" else "Removed from Wishlist"
            _sideEffects.emit(ProductDetailsSideEffect.ShowToast(message))
        }
    }

    private fun addToCart() {
        viewModelScope.launch {
            _sideEffects.emit(ProductDetailsSideEffect.ShowToast("Added to Cart!"))
        }
    }
}
