package com.iti.presentation.screens.wishlist

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.iti.domain.models.Product
import com.iti.presentation.components.ErrorScreen
import com.iti.presentation.components.ProductCard
import com.iti.presentation.core.UiText
import com.iti.presentation.ui.theme.*
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    onBackClick: () -> Unit,
    onExploreProductsClick: () -> Unit,
    onProductClick: (String) -> Unit,
    viewModel: WishlistViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val isDark = isSystemInDarkTheme()
    val backgroundColor = if (isDark) BackgroundDark else BackgroundLight
    val textPrimaryColor = if (isDark) TextPrimaryDark else TextPrimaryLight
    val textSecondaryColor = if (isDark) TextSecondaryDark else TextSecondaryLight
    val buttonBgColor = if (isDark) ButtonPrimaryDark else ButtonPrimaryLight
    val buttonTextColor = if (isDark) ButtonPrimaryTextDark else ButtonPrimaryTextLight

    LaunchedEffect(key1 = true) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is WishlistUiEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Wishlist",
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
                        color = if (isDark) PrimaryDark else PrimaryLight
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
                            onProductClick = onProductClick
                        )
                    }
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

@Composable
fun EmptyWishlistState(
    textSecondaryColor: androidx.compose.ui.graphics.Color,
    buttonBgColor: androidx.compose.ui.graphics.Color,
    buttonTextColor: androidx.compose.ui.graphics.Color,
    onExploreClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    color = ErrorLight.copy(alpha = 0.1f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = null,
                modifier = Modifier.size(50.dp),
                tint = ErrorLight
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "No favorites yet",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Save items you love by tapping the heart icon on any product.",
            style = MaterialTheme.typography.bodyMedium,
            color = textSecondaryColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onExploreClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = buttonBgColor)
        ) {
            Text(
                text = "Explore Products",
                style = MaterialTheme.typography.labelLarge,
                color = buttonTextColor
            )
        }
    }
}

@Composable
fun FavoritesGrid(
    products: List<Product>,
    onProductClick: (String) -> Unit,
    viewModel: WishlistViewModel = koinViewModel()
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(products, key = { it.id }) { product ->
            ProductCard(
                product = product,
                onClick = { onProductClick(product.id) },
                onFavoriteClick = { viewModel.handleIntent(WishlistIntent.RemoveFromFavorites(product.id)) }
            )
        }
    }
}