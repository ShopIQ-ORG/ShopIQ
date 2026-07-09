package com.iti.presentation.screens.checkout.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.repositories.payment.PaymobRepository
import com.iti.presentation.R
import com.iti.presentation.util.UiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PaymentViewModel(
    private val paymobRepository: PaymobRepository
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

            paymobRepository.createPaymentIntention(
                amountCents = amountCents,
                currency = currency,
                integrationId = integrationId
            ).onSuccess { result ->
                _paymentUiState.value = PaymentUiState.Success(
                    clientSecret = result.clientSecret,
                    publicKey = result.publicKey
                )
            }.onFailure { exception ->
                _paymentUiState.value = PaymentUiState.Error(
                    exception.message?.let(UiText::Plain)
                        ?: UiText.StringResource(R.string.payment_intention_creation_failed)
                )
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