//
//  AllBrandsScreen.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 01/07/2026.
//
package com.iti.presentation.screens.brands

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.valentinilk.shimmer.shimmer
import com.iti.presentation.R
import com.iti.presentation.components.BackTopBar
import com.iti.presentation.components.CartIconWithBadge
import com.iti.presentation.components.NoInternetScreen
import com.iti.presentation.components.SearchBar
import com.iti.presentation.screens.home.viewmodel.CartBadgeViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.ui.tooling.preview.Preview
import com.iti.presentation.ui.theme.ShopIQTheme
import com.iti.domain.models.Brand

private val brandBannerImages = listOf(
    R.drawable.brand_banner_1,
    R.drawable.brand_banner_2,
    R.drawable.brand_banner_3,
    R.drawable.brand_banner_4,
    R.drawable.brand_banner_5
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllBrandsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAllProducts: (String) -> Unit,
    onCartClick: () -> Unit,
    viewModel: AllBrandsViewModel = koinViewModel(),
    cartBadgeViewModel: CartBadgeViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val cartItemCount by cartBadgeViewModel.cartItemCount.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is AllBrandsContract.Effect.NavigateToProducts ->
                    onNavigateToAllProducts(effect.brandName)
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            BackTopBar(
                title = stringResource(R.string.brands_title),
                onBack = onNavigateBack,
                scrollBehavior = scrollBehavior,
                actions = {
                    CartIconWithBadge(
                        count = cartItemCount,
                        onClick = onCartClick
                    )
                }
            )
        }
    ) { innerPadding ->
        when (val screenState = state.screenState) {
            is AllBrandsContract.ScreenState.Loading -> {
                AllBrandsShimmer(
                    modifier = Modifier.padding(innerPadding)
                )
            }

            is AllBrandsContract.ScreenState.Failure -> {
                NoInternetScreen(
                    onRetry = { viewModel.sendIntent(AllBrandsContract.Intent.Retry) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }

            is AllBrandsContract.ScreenState.Success -> {
                BrandsContent(
                    brands = state.filteredBrands,
                    query = state.query,
                    onQueryChanged = {
                        viewModel.sendIntent(AllBrandsContract.Intent.QueryChanged(it))
                    },
                    onBrandClick = { brandName ->
                        viewModel.sendIntent(AllBrandsContract.Intent.BrandClicked(brandName))
                    },
                    scrollBehavior = scrollBehavior,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BrandsContent(
    brands: List<com.iti.domain.models.Brand>,
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
        ) { index, brand ->
            BrandBannerCard(
                brandName = brand.name,
                imageRes = brandBannerImages[index % brandBannerImages.size],
                onClick = { onBrandClick(brand.name) },
                modifier = Modifier.animateItem()
            )
        }
    }
}

@Composable
private fun BrandBannerCard(
    brandName: String,
    imageRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(170.dp)
            .clip(shape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
    ) {
        // Background image
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = brandName,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Dark gradient overlay for text readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.55f),
                            Color.Transparent
                        ),
                        startX = 0f,
                        endX = 600f
                    )
                )
        )

        // Text overlay
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, bottom = 20.dp)
        ) {
            Text(
                text = brandName.uppercase(),
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
            Text(
                text = stringResource(R.string.brands_discover_collection),
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun AllBrandsShimmer(modifier: Modifier = Modifier) {
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