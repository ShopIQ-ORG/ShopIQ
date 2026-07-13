package com.iti.presentation.screens.checkout

import com.iti.domain.models.Address
import com.iti.domain.models.cart.Cart
import com.iti.domain.models.checkout.DraftOrder
import com.iti.presentation.util.UiText

sealed interface CheckoutContract {
    data class State(
        val currentStep: Int = 1,
        val selectedAddress: Address? = null,
        val paymentMethod: PaymentMethodType? = null,
        val cart: Cart? = null,
        val draftOrder: DraftOrder? = null,
        val currentUser: com.iti.domain.models.User? = null,
        val isLoading: Boolean = false,
        val error: UiText? = null
    )

    sealed interface Event {
        data class AddressSelected(val address: Address) : Event
        data class PaymentMethodSelected(val method: PaymentMethodType) : Event
        object PaymentMethodConfirmed : Event
        object PlaceOrder : Event
        object NavigateBack : Event
        data class GoToStep(val step: Int) : Event
    }

    sealed interface Effect {
        object NavigateBack : Effect
        object NavigateToHome : Effect
        data class ShowError(val message: UiText) : Effect
    }
}