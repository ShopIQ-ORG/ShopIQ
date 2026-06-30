package com.iti.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.iti.presentation.screens.wishlist.WishlistScreen

@Composable
fun WishlistTabContent(
    onProductClick: (String) -> Unit = {},
    onExploreClick: () -> Unit = {}
) {
    WishlistScreen(
        onBackClick = {},
        onExploreProductsClick = onExploreClick,
        onProductClick = onProductClick
    )
}
