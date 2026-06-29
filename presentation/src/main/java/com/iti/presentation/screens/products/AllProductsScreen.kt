package com.iti.presentation.screens.products

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
import com.iti.presentation.screens.products.components.AllProductsShimmer
import com.iti.presentation.screens.products.components.FilterBottomSheet
import com.iti.presentation.screens.products.components.SortBottomSheet
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
    val gridState = rememberLazyGridState()

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
                title = state.activeBrand ?: stringResource(R.string.all_products_title),
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

            // Get product count from state
            val productCount = when (val screenState = state.screenState) {
                is AllProductsContract.ScreenState.Success -> screenState.products.size
                else -> 0
            }

            // ── Filter, Sort, and Count Row ──────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Filter Button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(50.dp)
                        )
                        .clickable { viewModel.sendIntent(AllProductsContract.Intent.OpenFilterSheet) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = stringResource(R.string.cd_filter),
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.filter_button_label),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (filterCount > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "!",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }

                // Sort Button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(50.dp)
                        )
                        .clickable { viewModel.sendIntent(AllProductsContract.Intent.OpenSortSheet) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Sort,
                        contentDescription = stringResource(R.string.cd_sort),
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.sort_button_label),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Count text
                Text(
                    text = stringResource(R.string.products_count, productCount),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // ── Horizontal Subcategory Scrollable Chips Row ──────────────────
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // "All" chip
                item {
                    val isAllSelected = state.filterState.selectedCategory == null
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(
                                if (isAllSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isAllSelected) Color.Transparent else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(50.dp)
                            )
                            .clickable {
                                viewModel.sendIntent(AllProductsContract.Intent.SelectCategory(null))
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.all_subcategory),
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isAllSelected) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Subcategory chips
                items(state.availableCategories) { category ->
                    val isSelected = state.filterState.selectedCategory == category
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(50.dp)
                            )
                            .clickable {
                                viewModel.sendIntent(AllProductsContract.Intent.SelectCategory(category))
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

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

// ── Active Filters Summary Row ─────────────────────────────────────────────────
@Composable
private fun ActiveFiltersRow(
    state: AllProductsContract.State,
    onRemoveBrand: (String) -> Unit,
    onRemoveSort: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (state.sortOption != AllProductsContract.SortOption.BEST_SELLING) {
            ActiveChip(
                label = when (state.sortOption) {
                    AllProductsContract.SortOption.PRICE_ASC  -> "↑ Price"
                    AllProductsContract.SortOption.PRICE_DESC -> "↓ Price"
                    AllProductsContract.SortOption.BEST_SELLING -> ""
                },
                onRemove = onRemoveSort
            )
        }
    }
}

@Composable
private fun ActiveChip(label: String, onRemove: () -> Unit) {
    FilterChip(
        selected = true,
        onClick = onRemove,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium
            )
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.clear_filter),
                modifier = Modifier.size(14.dp)
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
            selectedTrailingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

// ── Legacy Brand Filter Banner ─────────────────────────────────────────────────
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
