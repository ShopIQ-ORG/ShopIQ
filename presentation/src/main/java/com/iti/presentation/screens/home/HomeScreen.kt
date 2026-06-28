package com.iti.presentation.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iti.domain.models.Ad
import com.iti.domain.models.Brand
import com.iti.domain.models.Product
import com.iti.domain.models.Result
import com.iti.presentation.components.BottomNavItem
import com.iti.presentation.components.HomeTabContent
import com.iti.presentation.components.ProfileTabContent
import com.iti.presentation.components.WishlistTabContent
import com.iti.presentation.screens.category.CategoryScreen
import com.iti.presentation.ui.theme.ShopIQTheme
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

@OptIn(ExperimentalMaterial3Api::class)
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
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "ShopIQ",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Black,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = Color.Red,
                                    contentColor = Color.White
                                ) {
                                    Text("3")
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Cart"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                navItems.forEachIndexed { index, item ->
                    val isSelected = selectedIndex == index
                    NavigationBarItem(
                        icon = {
                            val iconImage = if (isSelected) item.selectedIcon else item.unselectedIcon
                            Icon(
                                imageVector = iconImage,
                                contentDescription = item.label
                            )
                        },
                        label = { Text(text = item.label, fontSize = 10.sp) },
                        selected = isSelected,
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent,
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        ),
                        onClick = { selectedIndex = index }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (navItems[selectedIndex]) {
                BottomNavItem.Home -> HomeTabContent(
                    productsResult = productsResult,
                    brandsResult = brandsResult,
                    adsResult = adsResult,
                    onNavigateToAllBrands = onNavigateToAllBrands,
                    onNavigateToAllProducts = onNavigateToAllProducts
                )
                BottomNavItem.Category -> CategoryScreen(viewModel = koinViewModel())
                BottomNavItem.Wishlist -> WishlistTabContent()
                BottomNavItem.Profile -> ProfileTabContent(
                    onNavigateToSplash = onNavigateToSplash
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    ShopIQTheme {
        HomeScreenContent(
            onNavigateToSplash = { }
        )
    }
}