package com.iti.presentation.components

import androidx.compose.runtime.Composable
import com.iti.presentation.screens.wishlist.WishlistScreen

@Composable
fun WishlistTabContent(
    onProductClick: (String) -> Unit = {},
    onExploreClick: () -> Unit = {},
    onAuthClick: () -> Unit = {},
    cartItemCount: Int = 0,
    onCartClick: () -> Unit = {}
) {
    WishlistScreen(
        onExploreProductsClick = onExploreClick,
        onProductClick = onProductClick,
        onAuthClick = onAuthClick,
        cartItemCount = cartItemCount,
        onCartClick = onCartClick
    )
}
