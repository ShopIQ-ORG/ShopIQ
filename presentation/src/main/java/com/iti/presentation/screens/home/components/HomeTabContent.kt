package com.iti.presentation.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.iti.presentation.R
import com.iti.presentation.components.AppTopBar
import com.iti.presentation.components.NoInternetScreen
import com.iti.presentation.components.ProductCard
import com.iti.presentation.components.SearchBar
import com.iti.presentation.screens.home.HomeContract

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTabContent(
    state: HomeContract.State,
    onIntent: (HomeContract.Intent) -> Unit,
    bottomPadding: Dp = 0.dp
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    var query by remember { mutableStateOf("") }

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
        when (val screenState = state.screenState) {
            is HomeContract.ScreenState.Loading -> {
                HomeShimmer(
                    topPadding = innerPadding.calculateTopPadding(),
                    bottomPadding = bottomPadding
                )
            }

            is HomeContract.ScreenState.Failure -> {
                NoInternetScreen(
                    onRetry = { onIntent(HomeContract.Intent.Retry) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }

            is HomeContract.ScreenState.Success -> {
                val data = screenState.data
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
                        SearchBar(
                            value = query,
                            onValueChanged = {},
                            enabled = false,
                            onClick = {
                                onIntent(HomeContract.Intent.SearchBarClicked)
                            },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    item { AdsSection(data.ads) }

                    item {
                        SectionHeader(
                            title = stringResource(R.string.top_brands),
                            onViewAllClick = { onIntent(HomeContract.Intent.ViewAllBrandsClicked) }
                        )
                        BrandsRow(
                            brands = data.brands,
                            onBrandClick = { brand ->
                                onIntent(HomeContract.Intent.BrandClicked(brand.name))
                            }
                        )
                    }

                    item {
                        SectionHeader(
                            title = stringResource(R.string.featured_products),
                            onViewAllClick = { onIntent(HomeContract.Intent.ViewAllProductsClicked) }
                        )
                    }

                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(data.products.take(6)) { product ->
                                ProductCard(
                                    product = product,
                                    onClick = { onIntent(HomeContract.Intent.ProductClicked(product)) },
                                    onFavoriteClick = { onIntent(HomeContract.Intent.ProductFavoriteClicked(product)) },
                                    modifier = Modifier.width(160.dp)
                                )
                            }
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