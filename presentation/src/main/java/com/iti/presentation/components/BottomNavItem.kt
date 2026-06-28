package com.iti.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.ui.graphics.vector.ImageVector

enum class BottomNavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    Home("Home", Icons.Filled.Home, Icons.Outlined.Home),
    Category("Categories", Icons.Filled.GridView, Icons.Outlined.GridView),
    Wishlist("Wishlist", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder),
    Profile("Profile", Icons.Filled.Person, Icons.Outlined.Person)
}
