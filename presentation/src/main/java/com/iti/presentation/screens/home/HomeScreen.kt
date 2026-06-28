package com.iti.presentation.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.iti.presentation.components.BottomNavItem
import com.iti.presentation.components.CategoryTabContent
import com.iti.presentation.components.HomeTabContent
import com.iti.presentation.components.ProfileTabContent
import com.iti.presentation.components.WishlistTabContent

@Composable
fun HomeScreen(onNavigateToSplash: () -> Unit) {
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    val navItems = BottomNavItem.entries

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label
                            )
                        },
                        label = { Text(text = item.label) },
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (navItems[selectedIndex]) {
                BottomNavItem.Home -> HomeTabContent()
                BottomNavItem.Category -> CategoryTabContent()
                BottomNavItem.Wishlist -> WishlistTabContent()
                BottomNavItem.Profile -> ProfileTabContent(
                    onNavigateToSplash = onNavigateToSplash
                )
            }
        }
    }
}
