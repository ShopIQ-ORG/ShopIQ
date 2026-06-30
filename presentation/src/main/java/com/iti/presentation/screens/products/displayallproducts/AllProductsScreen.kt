package com.iti.presentation.screens.products.displayallproducts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.iti.presentation.R
import com.iti.presentation.components.BackTopBar
import com.iti.presentation.components.NoInternetScreen
import com.iti.presentation.components.ProductCard
import com.iti.presentation.screens.products.displayallproducts.components.AllProductsShimmer
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllProductsScreen(
    brandName: String? = null,
    onNavigateBack: () -> Unit,
    onNavigateToProduct: (Long) -> Unit,
    viewModel: AllProductsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    LaunchedEffect(brandName) {
        viewModel.sendIntent(AllProductsContract.Intent.LoadData(brandName))
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is AllProductsContract.Effect.NavigateToProduct -> {
                    val idLong = effect.productId.substringAfterLast("/").toLong()
                    onNavigateToProduct(idLong)
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            BackTopBar(
                title = state.activeBrand ?: stringResource(R.string.all_products_title),
                onBack = onNavigateBack,
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize()) {
            if (state.activeBrand != null) {
                FilterBanner(
                    brandName = state.activeBrand!!,
                    onClear = { viewModel.sendIntent(AllProductsContract.Intent.ClearFilter) },
                    modifier = Modifier.padding(
                        top = innerPadding.calculateTopPadding(),
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 4.dp
                    )
                )
            }

            when (val screenState = state.screenState) {
                is AllProductsContract.ScreenState.Loading -> {
                    AllProductsShimmer(
                        topPadding = if (state.activeBrand != null) 0.dp
                        else innerPadding.calculateTopPadding()
                    )
                }

                is AllProductsContract.ScreenState.Failure -> {
                    NoInternetScreen(
                        onRetry = { viewModel.sendIntent(AllProductsContract.Intent.Retry) },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }

                is AllProductsContract.ScreenState.Success -> {
                    if (screenState.products.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.no_products_found),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(
                                top = if (state.activeBrand != null) 8.dp
                                else innerPadding.calculateTopPadding() + 8.dp,
                                bottom = 24.dp,
                                start = 16.dp,
                                end = 16.dp
                            ),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .nestedScroll(scrollBehavior.nestedScrollConnection)
                        ) {
                            items(screenState.products, key = { it.id }) { product ->
                                ProductCard(
                                    product = product,
                                    onClick = {
                                        viewModel.sendIntent(
                                            AllProductsContract.Intent.ProductClicked(product)
                                        )
                                    },
                                    onFavoriteClick = {
                                        viewModel.sendIntent(
                                            AllProductsContract.Intent.ProductFavoriteClicked(product)
                                        )
                                    }
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
private fun FilterBanner(
    brandName: String,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterChip(
            selected = true,
            onClick = onClear,
            label = { Text(text = brandName, style = MaterialTheme.typography.labelMedium) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.clear_filter),
                    modifier = Modifier.size(16.dp)
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedTrailingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )
    }
}
