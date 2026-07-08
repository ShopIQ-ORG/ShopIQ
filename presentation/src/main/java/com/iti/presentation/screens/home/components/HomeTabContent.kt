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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.iti.presentation.R
import com.iti.presentation.components.NoInternetScreen
import com.iti.presentation.components.ProductCard
import com.iti.presentation.components.SearchBar
import com.iti.presentation.components.ShopIQScaffold
import com.iti.presentation.screens.home.HomeContract
import com.iti.presentation.util.hasDiscount

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTabContent(
    state: HomeContract.State,
    onIntent: (HomeContract.Intent) -> Unit,
    bottomPadding: Dp = 0.dp,
    cartItemCount: Int = 0,
    onCartClick: () -> Unit
) {
    var query by remember { mutableStateOf("") }

    ShopIQScaffold(
        cartItemCount = cartItemCount,
        onCartClick = onCartClick
    ) { innerPadding, scrollBehavior ->
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
                val stableShuffled = remember(data.products) { data.products.shuffled() }
                val discountProducts = remember(data.products) { data.products.filter { it.hasDiscount }.take(6) }

                var isRefreshing by remember { mutableStateOf(false) }
                LaunchedEffect(state.screenState) {
                    isRefreshing = false
                }

                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = {
                        isRefreshing = true
                        onIntent(HomeContract.Intent.LoadData)
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
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

                    item {
                        AdsSection(
                            ads = data.ads,
                            onAdClick = { ad -> onIntent(HomeContract.Intent.AdClicked(ad)) }
                        )
                    }

                    // Show Eslam card below Ads only if the user is a guest (not logged in)
                    val isGuest = state.currentUser == null || state.currentUser is com.iti.domain.models.User.GuestUser
                    if (isGuest) {
                        item {
                            TryEslamCard(onTryEslamClick = { onIntent(HomeContract.Intent.NavigateToAiChat) })
                        }
                    }

                    // 1. Deals of the Day Section (shown for both guest and authenticated users)
                    if (discountProducts.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = stringResource(R.string.deals_of_the_day),
                                onViewAllClick = { onIntent(HomeContract.Intent.ViewAllProductsClicked) }
                            )
                        }
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(discountProducts, key = { it.id }) { product ->
                                    val onClick = remember(product) { { onIntent(HomeContract.Intent.ProductClicked(product)) } }
                                    val onFavoriteClick = remember(product) { { onIntent(HomeContract.Intent.ProductFavoriteClicked(product)) } }
                                    ProductCard(
                                        product = product,
                                        onClick = onClick,
                                        onFavoriteClick = onFavoriteClick,
                                        modifier = Modifier.width(160.dp)
                                    )
                                }
                            }
                        }
                    }

                    // 2. AI Picks for You (SuggestionsSection) - shown only if authenticated and has history in Firestore
                    if (!isGuest && state.hasChatHistory) {
                        item {
                            val suggestionsProducts = if (state.aiRecommendedProducts.isNotEmpty()) {
                                state.aiRecommendedProducts
                            } else {
                                stableShuffled
                            }
                            SuggestionsSection(
                                products = suggestionsProducts,
                                isLoading = state.isLoadingRecommendations,
                                onProductClick = { onIntent(HomeContract.Intent.ProductClicked(it)) },
                                onFavoriteClick = { onIntent(HomeContract.Intent.ProductFavoriteClicked(it)) },
                                onNavigateToChat = { onIntent(HomeContract.Intent.NavigateToAiChat) }
                            )
                        }
                    }

                    // 3. New Arrivals & Summer Sale Banners
                    item {
                        HomeBanners(
                            onExploreClick = { onIntent(HomeContract.Intent.ViewAllProductsClicked) },
                            onShopNowClick = { onIntent(HomeContract.Intent.SubCategoryClicked("ACCESSORIES")) }
                        )
                    }

                    // 4. Free Delivery / Features Bar
                    item {
                        HomeFeaturesBar()
                    }

                    // 5. Top Brands
                    item {
                        SectionHeader(
                            title = stringResource(R.string.top_brands),
                            onViewAllClick = { onIntent(HomeContract.Intent.ViewAllBrandsClicked) }
                        )
                        BrandsRow(
                            brands = data.brands,
                            onBrandClick = { brand ->
                                val isArabic = com.iti.presentation.util.LocaleHelper.isArabic()
                                val displayTitle = if (isArabic) (brand.arTitle ?: brand.name) else brand.name
                                onIntent(HomeContract.Intent.BrandClicked(brand.name, displayTitle))
                            }
                        )
                    }

                    // 6. Featured Products
                    item {
                        SectionHeader(
                            title = stringResource(R.string.featured_products),
                            onViewAllClick = { onIntent(HomeContract.Intent.ViewAllProductsClicked) }
                        )
                    }

                    item {
                        val featuredProducts = data.bestSellers.ifEmpty { 
                            remember(data.products) { data.products.shuffled().take(6) } 
                        }
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(featuredProducts, key = { it.id }) { product ->
                                val onClick = remember(product) { { onIntent(HomeContract.Intent.ProductClicked(product)) } }
                                val onFavoriteClick = remember(product) { { onIntent(HomeContract.Intent.ProductFavoriteClicked(product)) } }
                                ProductCard(
                                    product = product,
                                    onClick = onClick,
                                    onFavoriteClick = onFavoriteClick,
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