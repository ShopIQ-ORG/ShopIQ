package com.iti.presentation.screens.checkout.summary

import com.iti.domain.models.Address
import com.iti.domain.models.cart.Cart
import com.iti.presentation.screens.checkout.PaymentMethodContract.PaymentMethodType
import com.iti.presentation.util.UiText

sealed interface CheckoutSummaryContract {
    data class State(
        val cart: Cart? = null,
        val address: Address? = null,
        val paymentMethod: PaymentMethodType = PaymentMethodType.COD,
        val isLoading: Boolean = false,
        val isPlacingOrder: Boolean = false,
        val error: UiText? = null,
        val showPaymobBottomSheet: Boolean = false,
        val paymentProcessing: Boolean = false,
        val showCodLimitError: Boolean = false
    )

    sealed interface Event {
        object LoadData : Event
        object PlaceOrderClicked : Event
        object DismissPaymobBottomSheet : Event
        object DismissCodLimitError : Event
        data class OnPaymentSuccess(val response: Map<String, String?>) : Event
        data class OnPaymentFailure(val message: String) : Event
        object OnPaymentPending : Event
    }

    sealed interface Effect {
        object NavigateBack : Effect
        object NavigateToOrderConfirmation : Effect
        data class ShowError(val message: UiText) : Effect
    }
}
