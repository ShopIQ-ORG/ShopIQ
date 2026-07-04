package com.iti.presentation.screens.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.iti.presentation.R
import com.iti.presentation.components.BottomNavItem
import com.iti.presentation.components.ProfileTabContent
import com.iti.presentation.components.WishlistTabContent
import com.iti.presentation.screens.category.CategoryScreen
import com.iti.presentation.screens.home.components.HomeTabContent
import com.iti.presentation.screens.home.viewmodel.CartBadgeViewModel
import com.iti.presentation.screens.home.viewmodel.HomeViewModel
import com.iti.presentation.util.NetworkMonitor
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun HomeScreen(
    onNavigateToAllBrands: () -> Unit,
    onNavigateToAllProducts: (String?) -> Unit,
    onCartClick: () -> Unit,
    onCategoryClick: (categoryId: String, categoryTitle: String) -> Unit,
    onNavigateToProduct: (Long) -> Unit,
    onLogout: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToOrders: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {

                is HomeContract.Effect.NavigateToAllBrands -> {
                    onNavigateToAllBrands()
                }

                HomeContract.Effect.NavigateToAllProducts -> {
                    onNavigateToAllProducts(null)
                }

                is HomeContract.Effect.NavigateToProduct -> {
                    onNavigateToProduct(effect.productId)
                }

                is HomeContract.Effect.NavigateToProducts -> {
                    onNavigateToAllProducts(effect.brandName)
                }

                HomeContract.Effect.NavigateToSearch -> {
                    onNavigateToSearch()
                }

                HomeContract.Effect.NavigateToSignIn -> {
                    onLogout()
                }
                HomeContract.Effect.ShowAuthRequired -> {
                    onLogout()
                }
            }
        }
    }

    HomeScreenContent(
        state = state,
        onNavigateToProduct = onNavigateToProduct,
        onIntent = viewModel::sendIntent,
        onLogout = { viewModel.sendIntent(HomeContract.Intent.Logout) },
        onCartClick = onCartClick,
        onCategoryClick = onCategoryClick,
        onNavigateToOrders = onNavigateToOrders
    )
}

@Composable
fun HomeScreenContent(
    state: HomeContract.State,
    onIntent: (HomeContract.Intent) -> Unit,
    onNavigateToProduct: (Long) -> Unit,
    onNavigateToOrders: () -> Unit,
    onCategoryClick: (categoryId: String, categoryTitle: String) -> Unit,
    onLogout: () -> Unit,
    onCartClick: () -> Unit
) {
    val networkMonitor: NetworkMonitor = koinInject()
    val isConnected by networkMonitor.isConnected.collectAsState(initial = networkMonitor.isCurrentlyConnected())
    val snackbarHostState = remember { SnackbarHostState() }
    var wasConnected by remember { mutableStateOf(isConnected) }
    val connectionLostMessage = stringResource(id = R.string.network_connection_lost)

    val cartBadgeViewModel: CartBadgeViewModel = koinViewModel()
    val cartItemCount by cartBadgeViewModel.cartItemCount.collectAsState()

    LaunchedEffect(isConnected) {
        if (!isConnected && wasConnected) {
            if (state.screenState is HomeContract.ScreenState.Success) {
                snackbarHostState.showSnackbar(
                    message = connectionLostMessage
                )
            }
        }
        wasConnected = isConnected
    }

    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }

    val navItems = BottomNavItem.entries

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.navigationBarsPadding(),
                containerColor = MaterialTheme.colorScheme.background,
                tonalElevation = 0.dp
            ) {

                navItems.forEachIndexed { index, item ->

                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = {
                            selectedIndex = index
                        },
                        icon = {
                            Icon(
                                imageVector = if (selectedIndex == index) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label
                            )
                        },
                        label = {
                            Text(item.label)
                        }
                    )
                }
            }
        }
    ) { padding ->

        when (navItems[selectedIndex]) {

            BottomNavItem.Home -> {
                HomeTabContent(
                    state = state,
                    onIntent = onIntent,
                    bottomPadding = padding.calculateBottomPadding(),
                    cartItemCount = cartItemCount,
                    onCartClick = onCartClick
                )
            }

            BottomNavItem.Category -> {
                CategoryScreen(
                    viewModel = koinViewModel(),
                    bottomPadding = padding.calculateBottomPadding(),
                    cartItemCount = cartItemCount,
                    onCartClick = onCartClick,
                    onCategoryClick = onCategoryClick,
                )
            }

            BottomNavItem.Wishlist -> {
                WishlistTabContent(
                    onProductClick = { productId ->
                        val idLong = productId.substringAfterLast("/").toLongOrNull() ?: 0L
                        onNavigateToProduct(idLong)
                    },
                    onExploreClick = { selectedIndex = 0 },
                    onAuthClick = onLogout
                )
            }

            BottomNavItem.Profile -> {
                ProfileTabContent(
                    user = state.currentUser,
                    onLogout = onLogout,
                    onNavigateToOrders = onNavigateToOrders
                )
            }
        }
    }
}