package com.iti.presentation.screens.orders

import com.iti.domain.models.order.Order
import com.iti.presentation.util.UiText

object OrdersContract {

    data class State(
        val orders: List<Order> = emptyList(),
        val isLoading: Boolean = false,
        val isRefreshing: Boolean = false,
        val error: UiText? = null,
    ) {
        val isEmpty: Boolean
            get() = orders.isEmpty()
    }

    sealed class Event {
        object Refresh : Event()
        object Retry : Event()
        data class OrderClicked(val orderId: String) : Event()
    }

    sealed class Effect {
        data class NavigateToOrderDetails(val orderId: String) : Effect()
        data class ShowError(val message: UiText) : Effect()
    }
}