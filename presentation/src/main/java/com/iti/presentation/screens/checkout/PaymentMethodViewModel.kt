package com.iti.presentation.screens.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.models.Result
import com.iti.domain.usecases.cart.GetCartUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PaymentMethodViewModel(
    private val getCartUseCase: GetCartUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PaymentMethodContract.State())
    val state: StateFlow<PaymentMethodContract.State> = _state.asStateFlow()

    private val _effect = Channel<PaymentMethodContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var cartTotal: Double = 0.0

    init {
        loadCartTotal()
    }

    private fun loadCartTotal() {
        viewModelScope.launch {
            getCartUseCase().collect { result ->
                if (result is Result.Success) {
                    cartTotal = result.data.total.amount.toDoubleOrNull() ?: 0.0
                }
            }
        }
    }

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
                    val selectedMethod = _state.value.selectedMethod
                    if (selectedMethod == PaymentMethodContract.PaymentMethodType.COD && cartTotal > COD_MAX_AMOUNT) {
                        _effect.send(
                            PaymentMethodContract.Effect.ShowCodLimitError(
                                "Cash on Delivery is not available for orders above \$${COD_MAX_AMOUNT.toInt()}.00. " +
                                "Your order total is \$${"%.2f".format(cartTotal)}. Please choose Online Payment."
                            )
                        )
                    } else {
                        _effect.send(PaymentMethodContract.Effect.NavigateToNextStep(selectedMethod))
                    }
                }
            }
        }
    }

    companion object {
        const val COD_MAX_AMOUNT = 500.0
    }
}