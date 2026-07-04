package com.iti.presentation.navigation

import androidx.navigation3.runtime.NavKey

sealed class Screen : NavKey {
    data object Splash : Screen()
    data object OnBoarding : Screen()
    data object SignIn : Screen()
    data object SignUp : Screen()
    data object Home : Screen()
    data object AllBrands : Screen()
    data object Cart : Screen()
    data class AllProducts(val brandName: String? = null) : Screen()
    data class ProductDetails(val productId: Long) : Screen()
    data class CategoryDetails(val categoryId: String, val categoryTitle: String) : Screen()
    data object Search : Screen()

    data object PaymentMethod : Screen()
    data object CODPayment : Screen()
    data object OnlinePayment : Screen()

    data object Orders : Screen()
    data class OrderDetails(val orderId: String) : Screen()


}
