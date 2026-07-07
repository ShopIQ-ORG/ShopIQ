package com.iti.presentation.screens.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.repositories.payment.PaymobRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PaymentViewModel(
    private val paymobRepository: PaymobRepository
) : ViewModel() {

    private val _paymentUiState = MutableStateFlow<PaymentUiState>(PaymentUiState.Idle)
    val paymentUiState: StateFlow<PaymentUiState> = _paymentUiState.asStateFlow()

    fun startPaymentFlow(amountCents: Long, currency: String, integrationId: Int) {
        viewModelScope.launch {
            _paymentUiState.value = PaymentUiState.Loading
            
            paymobRepository.createPaymentIntention(amountCents, currency, integrationId)
                .onSuccess { result ->
                    _paymentUiState.value = PaymentUiState.Success(
                        clientSecret = result.clientSecret,
                        publicKey = result.publicKey
                    )
                }
                .onFailure { exception ->
                    _paymentUiState.value = PaymentUiState.Error(exception.message ?: "Intention Creation Failed")
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
    data class Success(val clientSecret: String, val publicKey: String) : PaymentUiState
    data class Error(val message: String) : PaymentUiState
}
