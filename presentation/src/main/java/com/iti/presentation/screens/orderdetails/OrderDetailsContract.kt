package com.iti.presentation.screens.orderdetails

import com.iti.domain.models.order.OrderDetails
import com.iti.presentation.util.UiText

object OrderDetailsContract {

    data class State(
        val orderDetails: OrderDetails? = null,
        val isLoading: Boolean = false,
        val error: UiText? = null,
    )

    sealed class Event {
        object Retry : Event()
        object ContactSupportClicked : Event()
    }

    sealed class Effect {
        data class ShowError(val message: UiText) : Effect()
        object NavigateToSupport : Effect()
    }
}