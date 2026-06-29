package com.iti.presentation.screens.products.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer


@Composable
fun AllProductsShimmer(topPadding: androidx.compose.ui.unit.Dp) {
    val shimmerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(
            top = topPadding + 8.dp,
            bottom = 24.dp,
            start = 16.dp,
            end = 16.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .shimmer()
    ) {
        items(6) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.75f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(shimmerColor)
                )
                Box(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(0.8f)
                        .size(12.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(shimmerColor)
                )
                Box(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .fillMaxWidth(0.5f)
                        .size(12.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(shimmerColor)
                )
            }
        }
    }
}