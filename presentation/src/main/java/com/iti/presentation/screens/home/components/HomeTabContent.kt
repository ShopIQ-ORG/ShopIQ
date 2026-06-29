package com.iti.presentation.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.iti.domain.models.Ad
import com.iti.domain.models.Brand
import com.iti.domain.models.Product
import com.iti.domain.models.Result
import com.iti.presentation.R
import com.iti.presentation.components.AppTopBar
import com.iti.presentation.components.BrandsRow
import com.iti.presentation.components.CustomNetworkImage
import com.iti.presentation.components.ProductsStaticGrid
import com.iti.presentation.components.SearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTabContent(
    productsResult: Result<List<Product>>,
    brandsResult: Result<List<Brand>>,
    adsResult: Result<List<Ad>>,
    onNavigateToAllBrands: () -> Unit = {},
    onNavigateToAllProducts: (String?) -> Unit = {},
    bottomPadding: Dp = 0.dp
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(
                scrollBehavior = scrollBehavior,
                onMenuClick = {},
                onCartClick = {}
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding(),
                bottom = bottomPadding + 16.dp
            )
        ) {
            item {
                SearchBar(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            }

            item { AdsSection(adsResult) }

            item {
                SectionHeader(
                    title = stringResource(R.string.top_brands),
                    onViewAllClick = onNavigateToAllBrands
                )
                BrandsSection(brandsResult, onBrandClick = onNavigateToAllProducts)
            }

            item {
                SectionHeader(
                    title = stringResource(R.string.featured_products),
                    onViewAllClick = { onNavigateToAllProducts(null) }
                )
            }

            when (productsResult) {
                is Result.Success -> {
                    item {
                        ProductsStaticGrid(
                            products = productsResult.data,
                            onProductClick = {},
                            onFavoriteClick = {}
                        )
                    }
                }
                is Result.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
                is Result.Failure -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.error_loading_products),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdsSection(adsResult: Result<List<Ad>>) {
    if (adsResult is Result.Success && adsResult.data.isNotEmpty()) {
        val ads = adsResult.data.filter { it.imageUrl.isNotEmpty() }
        val pagerState = rememberPagerState(pageCount = { ads.size })

        LaunchedEffect(pagerState) {
            while (true) {
                kotlinx.coroutines.delay(4000)
                if (ads.isNotEmpty()) {
                    pagerState.animateScrollToPage((pagerState.currentPage + 1) % ads.size)
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) { page ->
                val ad = ads[page]
                Box(modifier = Modifier.fillMaxSize()) {
                    CustomNetworkImage(
                        imageUrl = ad.imageUrl,
                        contentDescription = ad.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f))
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = ad.title,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = ad.subtitle,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.surface
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.shop_now),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, onViewAllClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        TextButton(onClick = onViewAllClick, contentPadding = PaddingValues(0.dp)) {
            Text(
                text = stringResource(R.string.view_all),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
fun BrandsSection(brandsResult: Result<List<Brand>>, onBrandClick: (String) -> Unit) {
    when (brandsResult) {
        is Result.Success -> {
            if (brandsResult.data.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_brands_available),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                BrandsRow(
                    brands = brandsResult.data,
                    onBrandClick = { brand -> onBrandClick(brand.name) }
                )
            }
        }
        is Result.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        else -> {}
    }
}