//
//  SearchScreen.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.presentation.screens.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.iti.presentation.components.ErrorScreen
import com.iti.presentation.components.NoResultsFeedback
import com.iti.presentation.components.ProductsGrid
import com.iti.presentation.components.SearchBar
import com.iti.presentation.screens.search.components.SearchEmptyState
import com.iti.presentation.screens.search.components.SearchShimmerState
import com.iti.presentation.ui.theme.ShopIQTheme

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToProduct: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SearchContract.Effect.NavigateToProduct -> {
                    onNavigateToProduct(effect.productId)
                }
                SearchContract.Effect.NavigateBack -> {
                    onNavigateBack()
                }
            }
        }
    }

    SearchScreenContent(
        state = state,
        onIntent = { viewModel.sendIntent(it) },
        onNavigateBack = onNavigateBack,
        focusRequester = focusRequester,
        modifier = modifier
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchScreenContent(
    state: SearchContract.State,
    onIntent: (SearchContract.Intent) -> Unit,
    onNavigateBack: () -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                SearchBar(
                    value = state.query,
                    onValueChanged = { onIntent(SearchContract.Intent.QueryChanged(it)) },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            onIntent(SearchContract.Intent.SearchSubmitted(state.query))
                            focusManager.clearFocus()
                        }
                    ),
                    trailingIcon = {
                        if (state.query.isNotEmpty()) {
                            IconButton(
                                onClick = { onIntent(SearchContract.Intent.QueryChanged("")) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .navigationBarsPadding()
                .imePadding()
        ) {
            when (val screenState = state.screenState) {
                SearchContract.ScreenState.Empty -> {
                    SearchEmptyState(
                        recentSearches = state.recentSearches,
                        trendingSearches = state.trendingSearches,
                        popularProducts = state.popularProducts,
                        onSearchTermClicked = { term ->
                            onIntent(SearchContract.Intent.QueryChanged(term))
                            onIntent(SearchContract.Intent.SearchSubmitted(term))
                            focusManager.clearFocus()
                        },
                        onDeleteRecentSearch = { query ->
                            onIntent(SearchContract.Intent.DeleteRecentSearch(query))
                        },
                        onClearAllRecentSearches = {
                            onIntent(SearchContract.Intent.ClearAllRecentSearches)
                        },
                        onProductClicked = { product ->
                            onIntent(SearchContract.Intent.ProductClicked(product))
                        }
                    )
                }
                SearchContract.ScreenState.Loading -> {
                    SearchShimmerState()
                }
                is SearchContract.ScreenState.Suggestions -> {
                    if (screenState.products.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            NoResultsFeedback(query = state.query)
                        }
                    } else {
                        ProductsGrid(
                            products = screenState.products,
                            onProductClick = { product ->
                                onIntent(SearchContract.Intent.ProductClicked(product))
                            },
                            onFavoriteClick = {}
                        )
                    }
                }
                is SearchContract.ScreenState.Success -> {
                    if (screenState.products.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            NoResultsFeedback(query = state.query)
                        }
                    } else {
                        ProductsGrid(
                            products = screenState.products,
                            onProductClick = { product ->
                                onIntent(SearchContract.Intent.ProductClicked(product))
                            },
                            onFavoriteClick = {}
                        )
                    }
                }
                is SearchContract.ScreenState.Failure -> {
                    ErrorScreen(
                        message = screenState.message,
                        onRetry = { onIntent(SearchContract.Intent.SearchSubmitted(state.query)) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    val focusRequester = remember { FocusRequester() }
    ShopIQTheme {
        SearchScreenContent(
            state = SearchContract.State(
                query = "Shoes",
                recentSearches = listOf("Shirt", "Belt"),
                trendingSearches = listOf("Nike", "Adidas")
            ),
            onIntent = {},
            onNavigateBack = {},
            focusRequester = focusRequester
        )
    }
}
