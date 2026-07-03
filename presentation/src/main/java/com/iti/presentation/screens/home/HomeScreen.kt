package com.iti.presentation.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.iti.presentation.ui.theme.LocalDarkTheme
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
import com.iti.presentation.screens.ai.AiChatScreen
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
        onCategoryClick = onCategoryClick
    )
}

@Composable
fun HomeScreenContent(
    state: HomeContract.State,
    onIntent: (HomeContract.Intent) -> Unit,
    onNavigateToProduct: (Long) -> Unit,
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
                    val isSelected = selectedIndex == index
                    if (item == BottomNavItem.AI) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(80.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val isDark = LocalDarkTheme.current
                            val bgColor = if (isSelected) {
                                if (isDark) Color(0xFF3B1E78) else Color(0xFFE8DDFF)
                            } else {
                                if (isDark) Color(0xFF242A31) else Color(0xFFF3F4F6)
                            }
                            val iconColor = if (isSelected) {
                                if (isDark) Color(0xFFD4BFFF) else Color(0xFF6F32E5)
                            } else {
                                if (isDark) Color(0xFF8D97A5) else Color(0xFF8E8E93)
                            }

                            Box(
                                modifier = Modifier.offset(y = (-8).dp),
                                contentAlignment = Alignment.Center
                            ) {
                                // Outer glow/halo
                                Box(
                                    modifier = Modifier
                                        .size(68.dp)
                                        .background(
                                            color = if (isSelected) Color(0xFF6F32E5).copy(alpha = 0.12f) else Color.Transparent,
                                            shape = CircleShape
                                        )
                                )
                                // Inner gradient circle
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .shadow(
                                            elevation = if (isSelected) 8.dp else 0.dp,
                                            shape = CircleShape,
                                            ambientColor = Color(0xFF6F32E5),
                                            spotColor = Color(0xFF6F32E5)
                                        )
                                        .background(
                                            brush = if (isSelected) {
                                                Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFF4F46E5)))
                                            } else {
                                                if (isDark) {
                                                    Brush.linearGradient(listOf(Color(0xFF2A3038), Color(0xFF1E242B)))
                                                } else {
                                                    Brush.linearGradient(listOf(Color(0xFFF3F4F6), Color(0xFFE5E7EB)))
                                                }
                                            },
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            selectedIndex = index
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = item.iconResId!!),
                                        contentDescription = item.label,
                                        tint = if (isSelected) Color.White else iconColor,
                                        modifier = Modifier.size(28.dp) // Large sparkle icon
                                    )
                                }
                            }

                            // Selected indicator triangle at the bottom of the bar
                            if (isSelected) {
                                Canvas(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 6.dp)
                                        .size(8.dp, 6.dp)
                                ) {
                                    val path = Path().apply {
                                        moveTo(size.width / 2f, 0f)
                                        lineTo(size.width, size.height)
                                        lineTo(0f, size.height)
                                        close()
                                    }
                                    drawPath(
                                        path = path,
                                        color = if (isDark) Color(0xFFD4BFFF) else Color(0xFF6F32E5)
                                    )
                                }
                            }
                        }
                    } else {
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                selectedIndex = index
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon!! else item.unselectedIcon!!,
                                    contentDescription = item.label
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    maxLines = 1,
                                    softWrap = false,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.sp
                                    )
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onBackground,
                                selectedTextColor = MaterialTheme.colorScheme.onBackground,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->

        val isDark = LocalDarkTheme.current

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

            BottomNavItem.AI -> {
                AiChatScreen(
                    onBackClick = { selectedIndex = 0 },
                    currentUser = state.currentUser,
                    onAuthClick = onLogout,
                    bottomPadding = padding.calculateBottomPadding()
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
                    onLogout = onLogout
                )
            }
        }
    }
}