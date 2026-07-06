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
        val currentStep: Int = 1, // 1: Address, 2: Payment, 3: Summary, 4: Finished
        val selectedAddress: Address? = null,
        val cart: Cart? = null,
        val draftOrder: DraftOrder? = null,
        val currentUser: com.iti.domain.models.User? = null,
        val isLoading: Boolean = false,
        val error: UiText? = null
    )

    sealed interface Event {
        data class AddressSelected(val address: Address) : Event
        object PaymentConfirmed : Event
        object PlaceOrder : Event
        object NavigateBack : Event
    }

    sealed interface Effect {
        object NavigateBack : Effect
        object NavigateToHome : Effect
        data class ShowError(val message: UiText) : Effect
    }
}
