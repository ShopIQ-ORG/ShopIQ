package com.iti.presentation.navigation

sealed class Screen {
    data object Splash : Screen()
    data object OnBoarding : Screen()
    data object SignIn : Screen()
    data object SignUp : Screen()
    data object Home : Screen()
    data object AllBrands : Screen()

    data object Cart : Screen()

    data class AllProducts(val brandName: String? = null) : Screen()

    data class ProductDetails(val productId: Long) : Screen()
    data object Search : Screen()
}
