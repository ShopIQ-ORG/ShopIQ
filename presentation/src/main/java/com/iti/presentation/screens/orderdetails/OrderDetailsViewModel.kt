package com.iti.presentation.screens.orderdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.models.Result
import com.iti.domain.usecases.orders.GetOrderDetailsUseCase
import com.iti.presentation.util.toUiMessage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OrderDetailsViewModel(
    private val orderId: String,
    private val getOrderDetailsUseCase: GetOrderDetailsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(OrderDetailsContract.State())
    val state: StateFlow<OrderDetailsContract.State> = _state.asStateFlow()

    private val _effect = Channel<OrderDetailsContract.Effect>()
    val effect = _effect.receiveAsFlow()

    init {
        loadOrderDetails()
    }

    fun onEvent(event: OrderDetailsContract.Event) {
        when (event) {
            is OrderDetailsContract.Event.Retry -> loadOrderDetails()
            is OrderDetailsContract.Event.ContactSupportClicked -> emitEffect(
                OrderDetailsContract.Effect.NavigateToSupport
            )
        }
    }

    private fun loadOrderDetails() {
        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            when (val result = getOrderDetailsUseCase(orderId)) {
                is Result.Success -> _state.update {
                    it.copy(isLoading = false, orderDetails = result.data)
                }

                is Result.Failure -> handleFailure(result.exception)
                else -> Unit
            }
        }
    }

    private fun emitEffect(effect: OrderDetailsContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }

    private suspend fun handleFailure(exception: Throwable) {
        _state.update { it.copy(isLoading = false) }
        _effect.send(OrderDetailsContract.Effect.ShowError(exception.toUiMessage()))
    }
}