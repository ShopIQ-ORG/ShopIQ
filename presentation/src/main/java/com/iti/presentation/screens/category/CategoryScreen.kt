package com.iti.presentation.screens.category

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.iti.presentation.R
import com.iti.presentation.components.SearchBar
import com.iti.presentation.components.NoInternetScreen
import com.iti.presentation.components.NoResultsFeedback
import com.iti.presentation.components.ShopIQScaffold
import com.iti.presentation.screens.category.components.CategoryCard
import com.iti.presentation.screens.category.components.CategoryCardShimmer
import com.valentinilk.shimmer.shimmer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    viewModel: CategoryViewModel,
    onCategoryClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    bottomPadding: Dp = 0.dp,
    cartItemCount: Int = 0,
    onCartClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CategoryContract.Effect.NavigateToCategoryProducts -> {
                    onCategoryClick(effect.categoryName)
                }
            }
        }
    }

    val filteredCategories = remember(state.categories, state.searchQuery) {
        if (state.searchQuery.isEmpty()) state.categories
        else state.categories.filter {
            it.title.contains(state.searchQuery, ignoreCase = true)
        }
    }

    ShopIQScaffold(
        modifier = modifier,
        title = stringResource(R.string.categories_title),
        cartItemCount = cartItemCount,
        onCartClick = onCartClick
    ) { innerPadding, scrollBehavior ->
        if (state.errorMessage != null) {
            NoInternetScreen(
                onRetry = { viewModel.sendIntent(CategoryContract.Intent.LoadCategories) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(
                    top = innerPadding.calculateTopPadding() + 8.dp,
                    bottom = bottomPadding + 16.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .then(if (state.isLoading) Modifier.shimmer() else Modifier)
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Column(modifier = Modifier.padding(bottom = 8.dp)) {
                        SearchBar(
                            value = state.searchQuery,
                            placeholderText = stringResource(id = R.string.category_search_placeholder),
                            onValueChanged = {
                                viewModel.sendIntent(CategoryContract.Intent.SearchQueryChanged(it))
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(id = R.string.category_section_title),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }

                if (state.isLoading) {
                    items(6) {
                        CategoryCardShimmer()
                    }
                } else if (filteredCategories.isEmpty() && state.searchQuery.isNotEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        NoResultsFeedback(
                            query = state.searchQuery,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                } else {
                    items(
                        items = filteredCategories,
                        key = { it.id }
                    ) { category ->
                        CategoryCard(
                            category = category,
                            onClick = {
                                viewModel.sendIntent(CategoryContract.Intent.CategoryClicked(category.title))
                            }
                        )
                    }
                }
            }
        }
    }
}