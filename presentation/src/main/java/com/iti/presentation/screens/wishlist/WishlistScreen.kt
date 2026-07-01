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
import com.iti.presentation.components.ErrorScreen
import com.iti.presentation.ui.theme.*
import com.iti.presentation.screens.wishlist.components.EmptyWishlistState
import com.iti.presentation.screens.wishlist.components.GuestWishlistState
import com.iti.presentation.screens.wishlist.components.FavoritesGrid
import com.iti.presentation.util.UiText
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    onBackClick: () -> Unit,
    onExploreProductsClick: () -> Unit,
    onProductClick: (String) -> Unit,
    onAuthClick: () -> Unit,
    viewModel: WishlistViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // Force Light Colors even in Dark Mode as requested
    val backgroundColor = BackgroundLight
    val textPrimaryColor = TextPrimaryLight
    val textSecondaryColor = TextSecondaryLight
    val buttonBgColor = ButtonPrimaryLight
    val buttonTextColor = ButtonPrimaryTextLight

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.wishlist_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = textPrimaryColor
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = textPrimaryColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        },
        containerColor = backgroundColor
    ) { paddingValues ->
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
                            onRemoveFromFavorites = { productId ->
                                viewModel.handleIntent(WishlistIntent.RemoveFromFavorites(productId))
                            }
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
                        onRetry = { viewModel.handleIntent(WishlistIntent.LoadFavorites) }
                    )
                }
            }
        }
    }
}