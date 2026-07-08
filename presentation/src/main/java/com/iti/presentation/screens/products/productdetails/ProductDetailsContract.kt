package com.iti.presentation.screens.products.productdetails

import com.iti.domain.models.Product
import com.iti.presentation.util.UiText

data class ProductDetailsUiState(
    val isLoading: Boolean = false,
    val product: Product? = null,
    val error: String? = null,
    val selectedColor: String? = null,
    val selectedSize: String? = null,
    val selectedImageIndex: Int = 0,
    val isWishlisted: Boolean = false,
    val isAddingToCart: Boolean = false,
    val showUnauthorizedDialog: Boolean = false,
    val isSubmittingReview: Boolean = false,
    val reviewError: String? = null,
    val currentUserName: String? = null,
    val selectedVariantId: String? = null,
    val translatedTitle: String? = null,
    val translatedDescription: String? = null
)

sealed interface ProductDetailsIntent {
    data class LoadProductDetails(val productId: Long) : ProductDetailsIntent
    data class SelectColor(val color: String) : ProductDetailsIntent
    data class SelectSize(val size: String) : ProductDetailsIntent
    data class SelectImage(val index: Int) : ProductDetailsIntent
    data class SelectVariant(val variantId: String) : ProductDetailsIntent
    object ToggleWishlist : ProductDetailsIntent
    object AddToCart : ProductDetailsIntent
    object DismissUnauthorizedDialog : ProductDetailsIntent
    data class SubmitReview(val rating: Int, val title: String, val body: String) : ProductDetailsIntent
    data class EditReview(val reviewId: String, val rating: Int, val title: String, val body: String) : ProductDetailsIntent
    data class DeleteReview(val reviewId: String) : ProductDetailsIntent
}


sealed interface ProductDetailsSideEffect {
    data class ShowToast(val message: UiText) : ProductDetailsSideEffect
    object NavigateToAuth : ProductDetailsSideEffect
}
