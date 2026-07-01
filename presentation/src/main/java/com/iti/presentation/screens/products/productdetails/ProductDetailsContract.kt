package com.iti.presentation.screens.products.productdetails

import com.iti.domain.models.Product

data class ProductDetailsUiState(
    val isLoading: Boolean = false,
    val product: Product? = null,
    val error: String? = null,
    val selectedColor: String? = null,
    val selectedSize: String? = null,
    val selectedImageIndex: Int = 0,
    val isWishlisted: Boolean = false,
    val isAddingToCart: Boolean = false
)

sealed interface ProductDetailsIntent {
    data class LoadProductDetails(val productId: Long) : ProductDetailsIntent
    data class SelectColor(val color: String) : ProductDetailsIntent
    data class SelectSize(val size: String) : ProductDetailsIntent
    data class SelectImage(val index: Int) : ProductDetailsIntent
    object ToggleWishlist : ProductDetailsIntent
    object AddToCart : ProductDetailsIntent
}

sealed interface ProductDetailsSideEffect {
    data class ShowToast(val message: String) : ProductDetailsSideEffect
    object NavigateToAuth : ProductDetailsSideEffect
}
