package com.iti.presentation.screens.wishlist

import com.iti.domain.models.Product

sealed interface WishlistUiState {
    object Loading : WishlistUiState
    data class Success(val products: List<Product>) : WishlistUiState
    data class Error(val message: String) : WishlistUiState
}

sealed interface WishlistIntent {
    object LoadFavorites : WishlistIntent
    data class RemoveFromFavorites(val productId: String) : WishlistIntent
}

sealed interface WishlistUiEffect {
    data class ShowToast(val message: String) : WishlistUiEffect
}