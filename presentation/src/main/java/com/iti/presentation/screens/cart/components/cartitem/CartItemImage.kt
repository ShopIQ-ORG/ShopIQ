package com.iti.presentation.screens.cart.components.cartitem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.iti.domain.models.cart.CartItem
import com.iti.presentation.components.CustomNetworkImage

@Composable
fun CartItemImage(
    item: CartItem,
    isBeingRemoved: Boolean,
    outOfStock: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(80.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .alpha(if (isBeingRemoved || outOfStock) 0.5f else 1f)
    ) {
        CustomNetworkImage(
            imageUrl = item.imageUrl,
            contentDescription = item.title,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop
        )
    }
}
