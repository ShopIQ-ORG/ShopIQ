package com.iti.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PersonOutline
import androidx.compose.ui.graphics.vector.ImageVector

import com.iti.presentation.R

enum class BottomNavItem(
    val labelResId: Int,
    val selectedIcon: ImageVector? = null,
    val unselectedIcon: ImageVector? = null,
    val iconResId: Int? = null
) {
    Home(
        labelResId = R.string.nav_home,
        selectedIcon = Icons.Rounded.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    Category(
        labelResId = R.string.nav_categories,
        selectedIcon = Icons.Rounded.GridView,
        unselectedIcon = Icons.Outlined.GridView
    ),
    AI(
        labelResId = R.string.nav_ai,
        iconResId = R.drawable.ic_ai
    ),
    Wishlist(
        labelResId = R.string.wishlist_title,
        selectedIcon = Icons.Rounded.Favorite,
        unselectedIcon = Icons.Rounded.FavoriteBorder
    ),
    Profile(
        labelResId = R.string.nav_profile,
        selectedIcon = Icons.Rounded.Person,
        unselectedIcon = Icons.Rounded.PersonOutline
    )
}