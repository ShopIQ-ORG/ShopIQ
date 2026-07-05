package com.iti.presentation.screens.orderdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.models.order.Order
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class OrderDetailsViewModel(
    order: Order
) : ViewModel() {

    private val _state = MutableStateFlow(OrderDetailsContract.State(order))
    val state: StateFlow<OrderDetailsContract.State> = _state.asStateFlow()

    private val _effect = Channel<OrderDetailsContract.Effect>()
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: OrderDetailsContract.Event) {
        when (event) {
            is OrderDetailsContract.Event.ContactSupportClicked -> emitEffect(
                OrderDetailsContract.Effect.NavigateToSupport
            )
        }
    }

    private fun emitEffect(effect: OrderDetailsContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}