package com.iti.presentation.screens.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.exceptions.NetworkException
import com.iti.domain.models.Result
import com.iti.domain.usecases.orders.GetOrdersUseCase
import com.iti.presentation.util.toUiMessage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OrdersViewModel(
    private val getOrdersUseCase: GetOrdersUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(OrdersContract.State())
    val state: StateFlow<OrdersContract.State> = _state.asStateFlow()

    private val _effect = Channel<OrdersContract.Effect>()
    val effect = _effect.receiveAsFlow()

    init {
        loadOrders()
    }

    fun onEvent(event: OrdersContract.Event) {
        when (event) {
            is OrdersContract.Event.Refresh -> refreshOrders()
            is OrdersContract.Event.Retry -> loadOrders()
            is OrdersContract.Event.OrderClicked -> emitEffect(
                OrdersContract.Effect.NavigateToOrderDetails(event.order)
            )
        }
    }

    private fun loadOrders() {
        _state.update { it.copy(isLoading = true, error = null, isNoInternet = false) }

        viewModelScope.launch {
            when (val result = getOrdersUseCase()) {
                is Result.Success -> _state.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        orders = result.data,
                    )
                }

                is Result.Failure -> handleFailure(result.exception)
                else -> Unit
            }
        }
    }

    private fun refreshOrders() {
        _state.update { it.copy(isRefreshing = true) }

        viewModelScope.launch {
            when (val result = getOrdersUseCase()) {
                is Result.Success -> _state.update {
                    it.copy(
                        isRefreshing = false,
                        isLoading = false,
                        orders = result.data,
                    )
                }

                is Result.Failure -> handleFailure(result.exception)
                else -> Unit
            }
        }
    }

    private fun emitEffect(effect: OrdersContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }

    private fun handleFailure(exception: Throwable) {
        val message = exception.toUiMessage()
        _state.update {
            it.copy(
                isLoading = false,
                isRefreshing = false,
                error = message,
                isNoInternet = exception is NetworkException.NoConnection
            )
        }
    }
}