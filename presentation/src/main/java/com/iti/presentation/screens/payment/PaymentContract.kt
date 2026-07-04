package com.iti.presentation.screens.payment

sealed interface PaymentUiState {
    object Loading : PaymentUiState
    data class Success(val checkoutUrl: String) : PaymentUiState
    data class Error(val message: String) : PaymentUiState
}

sealed interface PaymentIntent {
    object LoadCheckout : PaymentIntent
}

sealed interface PaymentUiEffect {
    object NavigateBack : PaymentUiEffect
    object PaymentSuccess : PaymentUiEffect
}
