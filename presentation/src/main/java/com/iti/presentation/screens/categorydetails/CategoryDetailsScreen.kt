package com.iti.presentation.screens.categorydetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.iti.domain.models.Product
import com.iti.presentation.components.BackTopBar
import com.iti.presentation.components.NoInternetScreen
import com.iti.presentation.components.NoResultsScreen
import com.iti.presentation.components.ProductsGrid
import com.iti.presentation.components.UnauthorizedDialog
import com.iti.presentation.screens.products.displayallproducts.components.AllProductsShimmer
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf

@Composable
fun CategoryDetailsScreen(
    categoryId: String,
    categoryTitle: String,
    viewModel: CategoryDetailsViewModel = koinViewModel(),
    onBackClick: () -> Unit,
    onNavigateToProduct: (Long) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToAuth: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showAuthDialog by remember { mutableStateOf(false) }

    LaunchedEffect(categoryId) {
        viewModel.sendIntent(CategoryDetailsContract.Intent.LoadProducts(categoryId))
    }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CategoryDetailsContract.Effect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(effect.message.resolve(context))
                }
                CategoryDetailsContract.Effect.ShowAuthRequired -> {
                    showAuthDialog = true
                }
            }
        }
    }

    CategoryDetailsContent(
        state = state,
        categoryTitle = categoryTitle,
        snackbarHostState = snackbarHostState,
        onBackClick = onBackClick,
        onNavigateToProduct = onNavigateToProduct,
        onNavigateToSearch = onNavigateToSearch,
        onRetryClick = {
            viewModel.sendIntent(CategoryDetailsContract.Intent.LoadProducts(categoryId))
        },
        onFavoriteClick = { product ->
            viewModel.sendIntent(CategoryDetailsContract.Intent.ProductFavoriteClicked(product))
        }
    )

    if (showAuthDialog) {
        UnauthorizedDialog(
            onDismiss = { showAuthDialog = false },
            onLogin = {
                showAuthDialog = false
                onNavigateToAuth()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailsContent(
    state: CategoryDetailsContract.State,
    categoryTitle: String,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onNavigateToProduct: (Long) -> Unit,
    onNavigateToSearch: () -> Unit,
    onRetryClick: () -> Unit,
    onFavoriteClick: (Product) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            BackTopBar(
                title = categoryTitle,
                onBack = onBackClick,
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                state.isLoading -> {
                    AllProductsShimmer(
                        topPadding = 8.dp
                    )
                }

                state.errorMessage != null -> {
                    NoInternetScreen(
                        onRetry = onRetryClick,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                state.products.isEmpty() -> {
                    NoResultsScreen(
                        query = "",
                        onTryAnotherSearch = onNavigateToSearch,
                        onBrowseCategories = onBackClick,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                else -> {
                    ProductsGrid(
                        products = state.products,
                        onProductClick = { product ->
                            val idStr = product.id.substringAfterLast("/").toLong()
                            onNavigateToProduct(idStr)
                        },
                        onFavoriteClick = onFavoriteClick,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}