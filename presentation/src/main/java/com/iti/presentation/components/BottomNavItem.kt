package com.iti.presentation.components

import com.iti.presentation.R

enum class BottomNavItem(
    val label: String,
    val selectedIcon: Int,
    val unselectedIcon: Int
) {
    Home("Home", R.drawable.home_fill, R.drawable.home_not_fill),
    Category("Categories", R.drawable.category_fill, R.drawable.category_not_fill),
    Wishlist("Wishlist", R.drawable.heart_fill, R.drawable.heart_not_fill),
    Profile("Profile", R.drawable.user_fill, R.drawable.user_not_fill)
}
