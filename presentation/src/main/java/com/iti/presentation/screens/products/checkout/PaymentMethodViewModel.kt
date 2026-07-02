package com.iti.presentation.screens.products.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PaymentMethodViewModel : ViewModel() {

    private val _state = MutableStateFlow(PaymentMethodContract.State())
    val state: StateFlow<PaymentMethodContract.State> = _state.asStateFlow()

    private val _effect = Channel<PaymentMethodContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun sendIntent(intent: PaymentMethodContract.Intent) {
        when (intent) {
            is PaymentMethodContract.Intent.SelectPaymentMethod -> {
                _state.update { it.copy(selectedMethod = intent.method) }
            }
            is PaymentMethodContract.Intent.BackClicked -> {
                viewModelScope.launch { _effect.send(PaymentMethodContract.Effect.NavigateBack) }
            }
            is PaymentMethodContract.Intent.ContinueClicked -> {
                viewModelScope.launch {
                    _effect.send(PaymentMethodContract.Effect.NavigateToNextStep(_state.value.selectedMethod))
                }
            }
        }
    }
}