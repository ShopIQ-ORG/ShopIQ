package com.iti.presentation.screens.payment

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.models.Result
import com.iti.domain.models.User
import com.iti.domain.models.cart.Cart
import com.iti.domain.usecases.auth.GetCurrentUserUseCase
import com.iti.domain.usecases.cart.GetCartUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class PaymentViewModel(
    private val getCartUseCase: GetCartUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<PaymentUiState>(PaymentUiState.Loading)
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<PaymentUiEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    init {
        handleIntent(PaymentIntent.LoadCheckout)
    }

    fun handleIntent(intent: PaymentIntent) {
        when (intent) {
            is PaymentIntent.LoadCheckout -> loadCheckout()
        }
    }

    private fun loadCheckout() {
        viewModelScope.launch {
            val userResult = getCurrentUserUseCase()
            val user = (userResult as? Result.Success)?.data

            getCartUseCase().collect { result ->
                when (result) {
                    is Result.Loading -> _uiState.value = PaymentUiState.Loading
                    is Result.Success -> {
                        val cart = result.data
                        val baseUrl = cart.checkoutUrl
                        if (baseUrl != null) {
                            val prefilledUrl = appendPrefillInfo(baseUrl, user)
                            _uiState.value = PaymentUiState.Success(prefilledUrl)
                        } else {
                            _uiState.value = PaymentUiState.Error("Checkout URL not found")
                        }
                    }
                    is Result.Failure -> {
                        _uiState.value = PaymentUiState.Error(result.exception.message ?: "Failed to load checkout")
                    }
                }
            }
        }
    }

    private fun appendPrefillInfo(baseUrl: String, user: User?): String {
        val uriBuilder = Uri.parse(baseUrl).buildUpon()

        // Prefill email from logged in user if available
        if (user is User.AuthenticatedUser) {
            uriBuilder.appendQueryParameter("checkout[email]", user.email)
        } else {
            // Static fallback for guest
            uriBuilder.appendQueryParameter("checkout[email]", "guest@shopiq.com")
        }

        // Static address info as requested
        uriBuilder.appendQueryParameter("checkout[shipping_address][first_name]", "Static")
        uriBuilder.appendQueryParameter("checkout[shipping_address][last_name]", "User")
        uriBuilder.appendQueryParameter("checkout[shipping_address][address1]", "123 ShopIQ St")
        uriBuilder.appendQueryParameter("checkout[shipping_address][city]", "Cairo")
        uriBuilder.appendQueryParameter("checkout[shipping_address][country]", "Egypt")

        return uriBuilder.build().toString()
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _uiEffect.send(PaymentUiEffect.NavigateBack)
        }
    }

    fun onPaymentSuccess() {
        viewModelScope.launch {
            _uiEffect.send(PaymentUiEffect.PaymentSuccess)
        }
    }
}
