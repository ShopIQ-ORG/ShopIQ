//
//  CheckoutContract.kt
//  ShopIQ
//
//  Created by Antigravity on 7/6/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.presentation.screens.checkout

import com.iti.domain.models.Address
import com.iti.domain.models.cart.Cart
import com.iti.domain.models.checkout.DraftOrder
import com.iti.presentation.util.UiText

sealed interface CheckoutContract {
    data class State(
        val currentStep: Int = 1, // 1: Address, 2: PaymentMethod, 3: PaymentDetails, 4: PaymentSuccess, 5: OrderSummary, 6: Finished
        val selectedAddress: Address? = null,
        val paymentMethod: PaymentMethodContract.PaymentMethodType? = null,
        val cart: Cart? = null,
        val draftOrder: DraftOrder? = null,
        val currentUser: com.iti.domain.models.User? = null,
        val isLoading: Boolean = false,
        val error: UiText? = null
    )

    sealed interface Event {
        data class AddressSelected(val address: Address) : Event
        data class PaymentMethodSelected(val method: com.iti.presentation.screens.checkout.PaymentMethodContract.PaymentMethodType) : Event
        object PaymentMethodConfirmed : Event
        object PaymentConfirmed : Event
        object PaymentSuccessProceed : Event
        object PlaceOrder : Event
        object NavigateBack : Event
    }

    sealed interface Effect {
        object NavigateBack : Effect
        object NavigateToHome : Effect
        data class ShowError(val message: UiText) : Effect
    }
}
