//
//  AllBrandsShimmer.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 01/07/2026.
//
package com.iti.presentation.screens.brands.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iti.presentation.ui.theme.ShopIQTheme
import com.valentinilk.shimmer.shimmer

@Composable
fun AllBrandsShimmer(modifier: Modifier = Modifier) {
    val shimmerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
    val shape = RoundedCornerShape(16.dp)

    Column(
        modifier = modifier
            .fillMaxSize()
            .shimmer()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Shimmer search bar placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(25.dp))
                .background(shimmerColor)
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Shimmer banner cards
        repeat(4) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(shape)
                    .background(shimmerColor)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AllBrandsShimmerPreview() {
    ShopIQTheme(darkTheme = false) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            AllBrandsShimmer()
        }
    }
}
