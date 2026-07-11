package com.iti.presentation.screens.checkout.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.usecases.payment.CreatePaymentIntentionUseCase
import com.iti.presentation.R
import com.iti.presentation.util.UiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PaymentViewModel(
    private val createPaymentIntentionUseCase: CreatePaymentIntentionUseCase
) : ViewModel() {

    private val _paymentUiState = MutableStateFlow<PaymentUiState>(PaymentUiState.Idle)
    val paymentUiState: StateFlow<PaymentUiState> = _paymentUiState.asStateFlow()

    companion object {
        val SUPPORTED_CURRENCIES = setOf("EGP")
    }

    fun startPaymentFlow(
        amountCents: Long,
        currency: String,
        integrationId: Int
    ) {
        if (currency.uppercase() !in SUPPORTED_CURRENCIES) {
            _paymentUiState.value = PaymentUiState.Error(
                UiText.StringResource(R.string.payment_currency_not_supported)
            )
            return
        }

        viewModelScope.launch {
            _paymentUiState.value = PaymentUiState.Loading

            when (val result = createPaymentIntentionUseCase(
                amountCents = amountCents,
                currency = currency,
                integrationId = integrationId
            )) {
                is com.iti.domain.models.Result.Success -> {
                    _paymentUiState.value = PaymentUiState.Success(
                        clientSecret = result.data.clientSecret,
                        publicKey = result.data.publicKey
                    )
                }
                is com.iti.domain.models.Result.Failure -> {
                    _paymentUiState.value = PaymentUiState.Error(
                        result.exception.message?.let(UiText::Plain)
                            ?: UiText.StringResource(R.string.payment_intention_creation_failed)
                    )
                }
                is com.iti.domain.models.Result.Loading -> {
                    // Handled above
                }
            }
        }
    }

    fun resetState() {
        _paymentUiState.value = PaymentUiState.Idle
    }
}

sealed interface PaymentUiState {

    data object Idle : PaymentUiState

    data object Loading : PaymentUiState

    data class Success(
        val clientSecret: String,
        val publicKey: String
    ) : PaymentUiState

    data class Error(
        val message: UiText
    ) : PaymentUiState
}