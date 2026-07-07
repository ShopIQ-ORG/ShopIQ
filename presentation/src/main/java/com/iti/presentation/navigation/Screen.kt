//
//  Screen.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.presentation.navigation

import androidx.navigation3.runtime.NavKey
import com.iti.domain.models.order.Order

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
    data object ManageAddresses : Screen()
    data object Checkout : Screen()

    data object PaymentMethod : Screen()
    data object CODPayment : Screen()
    data class OnlinePayment(val amountCents: Long) : Screen()
    data object OnlinePaymentSummary : Screen()
    data object OrderSuccess : Screen()

    data object Orders : Screen()
    data class OrderDetails(val order: Order) : Screen()


    data object AiHistory : Screen()

    data object AccountSettings : Screen()
    data object EditProfile : Screen()
    data object LocalizationCurrency : Screen()
    data object AddressManagement : Screen()
    data class AddEditAddress(val addressId: String? = null) : Screen()
    data class AddressValidation(
        val latitude: Double,
        val longitude: Double,
        val street: String = "",
        val city: String = "",
        val country: String = "",
        val postalCode: String = "",
        val label: String = "",
        val isDefault: Boolean = false,
        val recipientName: String = "",
        val phone: String = "",
        val addressId: String? = null
    ) : Screen()
    data object ForgotPassword : Screen()
    data class EmailVerification(val email: String) : Screen()
}