package com.iti.presentation.screens.products.displayallproducts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import com.iti.presentation.R
import com.iti.presentation.components.BackTopBar
import com.iti.presentation.components.NoInternetScreen
import com.iti.presentation.components.NoResultsFeedback
import com.iti.presentation.components.ProductCard
import com.iti.presentation.screens.products.displayallproducts.components.FilterBottomSheet
import com.iti.presentation.screens.products.displayallproducts.components.SortBottomSheet
import com.iti.presentation.screens.products.displayallproducts.components.ActiveFiltersRow
import com.iti.presentation.screens.products.displayallproducts.components.FilterBanner
import com.iti.presentation.screens.products.displayallproducts.components.ProductsHeaderControls
import com.iti.presentation.screens.products.displayallproducts.components.AllProductsShimmer
import androidx.compose.foundation.ExperimentalFoundationApi
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AllProductsScreen(
    brandName: String?,
    subCategoryName: String? = null,
    onNavigateBack: () -> Unit,
    onNavigateToProduct: (Long) -> Unit,
    onNavigateToAuth: () -> Unit,
    viewModel: AllProductsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val gridState = rememberLazyGridState()

    LaunchedEffect(brandName, subCategoryName) {
        viewModel.sendIntent(AllProductsContract.Intent.LoadData(brandName, subCategoryName))
    }

    LaunchedEffect(state.filterState, state.sortOption, state.activeBrand, state.searchQuery) {
        if (gridState.firstVisibleItemIndex > 0 || gridState.firstVisibleItemScrollOffset > 0) {
            gridState.scrollToItem(0)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is AllProductsContract.Effect.NavigateToProduct -> {
                    val idLong = effect.productId.substringAfterLast("/").toLong()
                    onNavigateToProduct(idLong)
                }

                AllProductsContract.Effect.ShowAuthRequired -> {
                    onNavigateToAuth()
                }
            }
        }
    }

    // Pagination trigger
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = gridState.layoutInfo.visibleItemsInfo.lastOrNull()
                ?: return@derivedStateOf false
            lastVisibleItem.index >= gridState.layoutInfo.totalItemsCount - 4
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value && state.hasNextPage && !state.isLoadingMore) {
            viewModel.sendIntent(AllProductsContract.Intent.LoadMore)
        }
    }

    // Active filter count for badge
    val filterCount = with(state.filterState) {
        (if (selectedCategory != null) 1 else 0) +
        (if (selectedSubCategory != null) 1 else 0) +
        selectedBrands.size
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            BackTopBar(
                title = state.activeBrand ?: state.activeSubCategory ?: stringResource(R.string.all_products_title),
                onBack = onNavigateBack,
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            // ── Legacy brand filter chip ─────────────────────────────────────
            AnimatedVisibility(visible = state.activeBrand != null) {
                FilterBanner(
                    brandName = state.activeBrand ?: "",
                    onClear = { viewModel.sendIntent(AllProductsContract.Intent.ClearFilter) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            // ── Legacy subcategory filter chip ─────────────────────────────────────
            AnimatedVisibility(visible = state.activeSubCategory != null) {
                FilterBanner(
                    brandName = state.activeSubCategory ?: "",
                    onClear = { viewModel.sendIntent(AllProductsContract.Intent.ClearSubCategoryFilter) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            // Get product count from state
            val productCount = when (val screenState = state.screenState) {
                is AllProductsContract.ScreenState.Success -> screenState.products.size
                else -> 0
            }

            // ── Products Header Controls (Filter, Sort, Category chips) ─────
            ProductsHeaderControls(
                state = state,
                filterCount = filterCount,
                productCount = productCount,
                onFilterClick = { viewModel.sendIntent(AllProductsContract.Intent.OpenFilterSheet) },
                onSortClick = { viewModel.sendIntent(AllProductsContract.Intent.OpenSortSheet) },
                onCategorySelected = { viewModel.sendIntent(AllProductsContract.Intent.SelectCategory(it)) }
            )

            // ── Main Grid Content ────────────────────────────────────────────
            when (val screenState = state.screenState) {
                is AllProductsContract.ScreenState.Loading -> {
                    AllProductsShimmer(topPadding = 8.dp)
                }

                is AllProductsContract.ScreenState.Failure -> {
                    NoInternetScreen(
                        onRetry = { viewModel.sendIntent(AllProductsContract.Intent.Retry) },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is AllProductsContract.ScreenState.Success -> {
                    val products = screenState.products
                    if (products.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            NoResultsFeedback(query = "")
                        }
                    } else {
                        LazyVerticalGrid(
                            state = gridState,
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(
                                top = 8.dp,
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
                            // ── Active filter summary chips row ──────────
                            if (filterCount > 0 || state.sortOption != AllProductsContract.SortOption.BEST_SELLING) {
                                item(span = { GridItemSpan(2) }) {
                                    ActiveFiltersRow(
                                        state = state,
                                        onRemoveBrand = {
                                            viewModel.sendIntent(AllProductsContract.Intent.OpenFilterSheet)
                                        },
                                        onRemoveSort = {
                                            viewModel.sendIntent(AllProductsContract.Intent.PendingSortChanged(AllProductsContract.SortOption.BEST_SELLING))
                                            viewModel.sendIntent(AllProductsContract.Intent.ApplySort)
                                        }
                                    )
                                }
                            }

                            items(products, key = { it.id }) { product ->
                                ProductCard(
                                    product = product,
                                    modifier = Modifier.animateItem(),
                                    onClick = {
                                        viewModel.sendIntent(AllProductsContract.Intent.ProductClicked(product))
                                    },
                                    onFavoriteClick = {
                                        viewModel.sendIntent(AllProductsContract.Intent.ProductFavoriteClicked(product))
                                    }
                                )
                            }

                            // ── Loading More Indicator ──────────────────
                            if (state.isLoadingMore) {
                                item(span = { GridItemSpan(2) }) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            color = MaterialTheme.colorScheme.primary,
                                            strokeWidth = 2.dp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ── Filter Bottom Sheet ──────────────────────────────────────────────
        if (state.isFilterSheetOpen) {
            FilterBottomSheet(
                filterState = state.pendingFilterState,
                availableCategories = state.availableCategories,
                availableSubCategories = state.availableSubCategories,
                availableBrands = state.availableBrands,
                onCategorySelected = { viewModel.sendIntent(AllProductsContract.Intent.PendingCategoryChanged(it)) },
                onSubCategorySelected = { viewModel.sendIntent(AllProductsContract.Intent.PendingSubCategoryChanged(it)) },
                onBrandToggled = { viewModel.sendIntent(AllProductsContract.Intent.PendingBrandToggled(it)) },
                onBrandSearchChanged = { viewModel.sendIntent(AllProductsContract.Intent.PendingBrandSearchChanged(it)) },
                onApply = { viewModel.sendIntent(AllProductsContract.Intent.ApplyFilters) },
                onReset = { viewModel.sendIntent(AllProductsContract.Intent.ResetFilters) },
                onDismiss = { viewModel.sendIntent(AllProductsContract.Intent.CloseFilterSheet) }
            )
        }

        // ── Sort Bottom Sheet ────────────────────────────────────────────────
        if (state.isSortSheetOpen) {
            SortBottomSheet(
                selectedOption = state.pendingSortOption,
                onOptionSelected = { viewModel.sendIntent(AllProductsContract.Intent.PendingSortChanged(it)) },
                onApply = { viewModel.sendIntent(AllProductsContract.Intent.ApplySort) },
                onDismiss = { viewModel.sendIntent(AllProductsContract.Intent.CloseSortSheet) }
            )
        }
    }
}
