//
//  BrandsContent.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 01/07/2026.
//
package com.iti.presentation.screens.brands.components
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iti.domain.models.Brand
import com.iti.presentation.R
import com.iti.presentation.components.SearchBar
import com.iti.presentation.ui.theme.ShopIQTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrandsContent(
    brands: List<Brand>,
    query: String,
    onQueryChanged: (String) -> Unit,
    onBrandClick: (String) -> Unit,
    scrollBehavior: androidx.compose.material3.TopAppBarScrollBehavior,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 8.dp,
            bottom = 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SearchBar(
                value = query,
                placeholderText = stringResource(R.string.brands_search_placeholder),
                onValueChanged = onQueryChanged,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        itemsIndexed(
            items = brands,
            key = { _, brand -> brand.id }
        ) { _, brand ->
            BrandBannerCard(
                brandName = brand.name,
                imageUrl = getBrandImageUrl(brand.name, brand.imageUrl),
                onClick = { onBrandClick(brand.name) },
                modifier = Modifier.animateItem()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun BrandsContentPreview() {
    val mockBrands = listOf(
        Brand("1", "Zara", ""),
        Brand("2", "H&M", ""),
        Brand("3", "Nike", ""),
        Brand("4", "Adidas", ""),
        Brand("5", "Puma", "")
    )
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    ShopIQTheme(darkTheme = false) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            BrandsContent(
                brands = mockBrands,
                query = "",
                onQueryChanged = {},
                onBrandClick = {},
                scrollBehavior = scrollBehavior
            )
        }
    }
}
