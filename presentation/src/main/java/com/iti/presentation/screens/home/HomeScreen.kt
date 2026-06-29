package com.iti.presentation.screens.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.iti.domain.models.Ad
import com.iti.domain.models.Brand
import com.iti.domain.models.Product
import com.iti.domain.models.Result
import com.iti.presentation.components.BottomNavItem
import com.iti.presentation.components.ProfileTabContent
import com.iti.presentation.components.WishlistTabContent
import com.iti.presentation.screens.category.CategoryScreen
import com.iti.presentation.screens.home.components.HomeTabContent
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    onNavigateToSplash: () -> Unit,
    onNavigateToAllBrands: () -> Unit,
    onNavigateToAllProducts: (String?) -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val productsResult by viewModel.products.collectAsState()
    val brandsResult by viewModel.brands.collectAsState()
    val adsResult by viewModel.ads.collectAsState()

    HomeScreenContent(
        onNavigateToSplash = onNavigateToSplash,
        onNavigateToAllBrands = onNavigateToAllBrands,
        onNavigateToAllProducts = onNavigateToAllProducts,
        productsResult = productsResult,
        brandsResult = brandsResult,
        adsResult = adsResult
    )
}

@Composable
fun HomeScreenContent(
    onNavigateToSplash: () -> Unit,
    onNavigateToAllBrands: () -> Unit = {},
    onNavigateToAllProducts: (String?) -> Unit = {},
    productsResult: Result<List<Product>> = Result.Loading,
    brandsResult: Result<List<Brand>> = Result.Loading,
    adsResult: Result<List<Ad>> = Result.Loading
) {
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    val navItems = BottomNavItem.entries

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(
                modifier = Modifier.navigationBarsPadding(),
                containerColor = MaterialTheme.colorScheme.background,
                tonalElevation = 0.dp
            ) {
                navItems.forEachIndexed { index, item ->
                    val isSelected = selectedIndex == index
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label
                            )
                        },
                        label = {
                            Text(text = item.label, style = MaterialTheme.typography.labelSmall)
                        },
                        selected = isSelected,
                        onClick = { selectedIndex = index }
                    )
                }
            }
        }
    ) { outerPadding ->
        when (navItems[selectedIndex]) {
            BottomNavItem.Home -> HomeTabContent(
                productsResult = productsResult,
                brandsResult = brandsResult,
                adsResult = adsResult,
                onNavigateToAllBrands = onNavigateToAllBrands,
                onNavigateToAllProducts = onNavigateToAllProducts,
                bottomPadding = outerPadding.calculateBottomPadding()
            )
            BottomNavItem.Category -> CategoryScreen(
                viewModel = koinViewModel(),
                bottomPadding = outerPadding.calculateBottomPadding()
            )
            BottomNavItem.Wishlist -> WishlistTabContent()
            BottomNavItem.Profile -> ProfileTabContent(onNavigateToSplash = onNavigateToSplash)
        }
    }
}