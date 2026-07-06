package com.iti.presentation.screens.wishlist

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.iti.presentation.R
import com.iti.presentation.components.ShopIQScaffold
import com.iti.presentation.components.ErrorScreen
import com.iti.presentation.ui.theme.*
import com.iti.presentation.screens.wishlist.components.EmptyWishlistState
import com.iti.presentation.screens.wishlist.components.GuestWishlistState
import com.iti.presentation.screens.wishlist.components.FavoritesGrid
import com.iti.presentation.util.UiText
import org.koin.androidx.compose.koinViewModel

import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    onExploreProductsClick: () -> Unit,
    onProductClick: (String) -> Unit,
    onAuthClick: () -> Unit,
    cartItemCount: Int,
    onCartClick: () -> Unit,
    viewModel: WishlistViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is WishlistUiEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is WishlistUiEffect.NavigateToAuth -> onAuthClick()
            }
        }
    }

    WishlistContent(
        uiState = uiState,
        onExploreProductsClick = onExploreProductsClick,
        onProductClick = onProductClick,
        onAuthClick = onAuthClick,
        cartItemCount = cartItemCount,
        onCartClick = onCartClick,
        onRemoveFromFavorites = { productId ->
            viewModel.handleIntent(WishlistIntent.RemoveFromFavorites(productId))
        },
        onRetryClick = {
            viewModel.handleIntent(WishlistIntent.LoadFavorites)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistContent(
    uiState: WishlistUiState,
    onExploreProductsClick: () -> Unit,
    onProductClick: (String) -> Unit,
    onAuthClick: () -> Unit,
    cartItemCount: Int,
    onCartClick: () -> Unit,
    onRemoveFromFavorites: (String) -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Force Light Colors even in Dark Mode as requested
    val backgroundColor = BackgroundLight
    val textPrimaryColor = TextPrimaryLight
    val textSecondaryColor = TextSecondaryLight
    val buttonBgColor = ButtonPrimaryLight
    val buttonTextColor = ButtonPrimaryTextLight

    ShopIQScaffold(
        title = stringResource(id = R.string.wishlist_title),
        cartItemCount = cartItemCount,
        onCartClick = onCartClick,
        modifier = modifier
    ) { paddingValues, _ ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is WishlistUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = PrimaryLight
                    )
                }
                is WishlistUiState.Success -> {
                    if (state.products.isEmpty()) {
                        EmptyWishlistState(
                            textSecondaryColor = textSecondaryColor,
                            buttonBgColor = buttonBgColor,
                            buttonTextColor = buttonTextColor,
                            onExploreClick = onExploreProductsClick
                        )
                    } else {
                        FavoritesGrid(
                            products = state.products,
                            onProductClick = onProductClick,
                            onRemoveFromFavorites = onRemoveFromFavorites
                        )
                    }
                }
                is WishlistUiState.RequireAuth -> {
                    GuestWishlistState(
                        textSecondaryColor = textSecondaryColor,
                        buttonBgColor = buttonBgColor,
                        buttonTextColor = buttonTextColor,
                        onAuthClick = onAuthClick
                    )
                }
                is WishlistUiState.Error -> {
                    ErrorScreen(
                        message = UiText.Plain(state.message),
                        onRetry = onRetryClick
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WishlistContentSuccessPreview() {
    val dummyProducts = listOf(
        com.iti.domain.models.Product(
            id = "1",
            title = "Classic Leather Jacket",
            description = "High quality leather jacket",
            handle = "classic-leather-jacket",
            productType = "Apparel",
            vendor = "Brand A",
            tags = emptyList(),
            minPrice = com.iti.domain.models.Money("1200.0", "EGP"),
            maxPrice = com.iti.domain.models.Money("1200.0", "EGP"),
            images = emptyList(),
            variants = emptyList(),
            isFavorite = true
        ),
        com.iti.domain.models.Product(
            id = "2",
            title = "Modern Sneakers",
            description = "Comfortable sneakers for daily use",
            handle = "modern-sneakers",
            productType = "Footwear",
            vendor = "Brand B",
            tags = emptyList(),
            minPrice = com.iti.domain.models.Money("850.0", "EGP"),
            maxPrice = com.iti.domain.models.Money("850.0", "EGP"),
            images = emptyList(),
            variants = emptyList(),
            isFavorite = true
        )
    )
    ShopIQTheme {
        WishlistContent(
            uiState = WishlistUiState.Success(dummyProducts),
            onExploreProductsClick = {},
            onProductClick = {},
            onAuthClick = {},
            cartItemCount = 2,
            onCartClick = {},
            onRemoveFromFavorites = {},
            onRetryClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WishlistContentEmptyPreview() {
    ShopIQTheme {
        WishlistContent(
            uiState = WishlistUiState.Success(emptyList()),
            onExploreProductsClick = {},
            onProductClick = {},
            onAuthClick = {},
            cartItemCount = 0,
            onCartClick = {},
            onRemoveFromFavorites = {},
            onRetryClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WishlistContentGuestPreview() {
    ShopIQTheme {
        WishlistContent(
            uiState = WishlistUiState.RequireAuth,
            onExploreProductsClick = {},
            onProductClick = {},
            onAuthClick = {},
            cartItemCount = 0,
            onCartClick = {},
            onRemoveFromFavorites = {},
            onRetryClick = {}
        )
    }
}