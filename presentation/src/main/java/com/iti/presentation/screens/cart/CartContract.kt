package com.iti.presentation.screens.cart

import com.iti.domain.models.cart.Cart
import com.iti.presentation.util.UiText

object CartContract {

    data class State(
        val cart: Cart? = null,
        val isLoading: Boolean = false,
        val isRefreshing: Boolean = false,
        val error: UiText? = null,
        val accessRestricted: Boolean = false,
        val isPromoExpanded: Boolean = false,
        val promoInput: String = "",
        val isApplyingPromo: Boolean = false,
        val promoError: UiText? = null,
        val removingCouponCode: String? = null,
        val itemBeingRemoved: String? = null
    ) {
        val isEmpty: Boolean
            get() = cart?.items.isNullOrEmpty()

        val canCheckout: Boolean
            get() = !isLoading && !accessRestricted && !isEmpty && error == null
    }

    sealed class Event {
        data class IncreaseQuantity(val itemId: String) : Event()
        data class DecreaseQuantity(val itemId: String) : Event()
        data class RemoveItem(val itemId: String) : Event()
        object TogglePromoExpanded : Event()
        data class PromoInputChanged(val value: String) : Event()
        object ApplyPromoCode : Event()
        data class RemoveCoupon(val code: String) : Event()
        object ProceedToCheckout : Event()
        object Refresh : Event()
        object Retry : Event()
    }

    sealed class Effect {
        object NavigateToCheckout : Effect()
        data class ShowError(val message: UiText) : Effect()
        data class ShowSuccess(val message: UiText) : Effect()
    }
}