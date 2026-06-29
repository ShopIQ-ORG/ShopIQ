package com.iti.presentation.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.iti.domain.models.Brand
import com.iti.presentation.components.BrandCard

@Composable
fun BrandsRow(
    brands: List<Brand>,
    onBrandClick: (Brand) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(brands) { brand ->
            BrandCard(brand = brand, onClick = { onBrandClick(brand) })
        }
    }
}