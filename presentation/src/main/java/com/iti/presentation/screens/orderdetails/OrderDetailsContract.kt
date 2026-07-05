package com.iti.presentation.screens.orderdetails

import com.iti.domain.models.order.Order

object OrderDetailsContract {

    data class State(val order: Order)

    sealed class Event {
        object ContactSupportClicked : Event()
    }

    sealed class Effect {
        object NavigateToSupport : Effect()
    }
}