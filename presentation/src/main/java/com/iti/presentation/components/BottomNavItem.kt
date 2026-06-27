package com.iti.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

enum class BottomNavItem(
    val label: String,
    val icon: ImageVector
) {
    Home("Home", Icons.Filled.Home),
    Category("Category", Icons.Filled.Menu),
    Wishlist("Wishlist", Icons.Filled.FavoriteBorder),
    Profile("Profile", Icons.Filled.Person)
}
