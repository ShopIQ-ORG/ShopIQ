package com.iti.presentation.screens.checkout.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.models.Result
import com.iti.domain.repositories.payment.PaymobRepository
import com.iti.domain.usecases.address.GetSavedAddressesUseCase
import com.iti.domain.usecases.cart.GetCartUseCase
import com.iti.presentation.screens.checkout.PaymentMethodContract.PaymentMethodType
import com.iti.presentation.util.UiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CheckoutSummaryViewModel(
    private val getCartUseCase: GetCartUseCase,
    private val getSavedAddressesUseCase: GetSavedAddressesUseCase,
    private val paymobRepository: PaymobRepository,
    private val paymentMethod: PaymentMethodType
) : ViewModel() {

    private val _state = MutableStateFlow(CheckoutSummaryContract.State(paymentMethod = paymentMethod))
    val state: StateFlow<CheckoutSummaryContract.State> = _state.asStateFlow()

    private val _effect = Channel<CheckoutSummaryContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        onEvent(CheckoutSummaryContract.Event.LoadData)
    }

    fun onEvent(event: CheckoutSummaryContract.Event) {
        when (event) {
            CheckoutSummaryContract.Event.LoadData -> loadData()
            CheckoutSummaryContract.Event.PlaceOrderClicked -> handlePlaceOrder()
            CheckoutSummaryContract.Event.DismissPaymobBottomSheet -> _state.update { it.copy(showPaymobBottomSheet = false) }
            is CheckoutSummaryContract.Event.OnPaymentSuccess -> handlePaymentSuccess(event.response)
            is CheckoutSummaryContract.Event.OnPaymentFailure -> handlePaymentFailure(event.message)
            CheckoutSummaryContract.Event.OnPaymentPending -> handlePaymentPending()
        }
    }

    private fun loadData() {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            // Fetch Cart
            getCartUseCase().collect { result ->
                if (result is Result.Success) {
                    _state.update { it.copy(cart = result.data) }
                }
            }
        }
        viewModelScope.launch {
            // Fetch Addresses and find default
            getSavedAddressesUseCase().collect { result ->
                if (result is Result.Success) {
                    val defaultAddress = result.data.find { it.isDefault } ?: result.data.firstOrNull()
                    _state.update { it.copy(address = defaultAddress, isLoading = false) }
                } else if (result is Result.Failure) {
                    _state.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    private fun handlePlaceOrder() {
        if (_state.value.paymentMethod == PaymentMethodType.ONLINE) {
            _state.update { it.copy(showPaymobBottomSheet = true) }
        } else {
            // Handle COD placement
            viewModelScope.launch {
                _state.update { it.copy(isPlacingOrder = true) }
                // TODO: Call createOrderUseCase
                // For now, simulate success
                delay(1500)
                _state.update { it.copy(isPlacingOrder = false) }
                _effect.send(CheckoutSummaryContract.Effect.NavigateToOrderConfirmation)
            }
        }
    }

    private fun handlePaymentSuccess(response: Map<String, String?>) {
        _state.update { it.copy(showPaymobBottomSheet = false, paymentProcessing = true) }
        viewModelScope.launch {
            // TODO: Finalize order on Shopify
            delay(1500)
            _state.update { it.copy(paymentProcessing = false) }
            _effect.send(CheckoutSummaryContract.Effect.NavigateToOrderConfirmation)
        }
    }

    private fun handlePaymentFailure(message: String) {
        _state.update { it.copy(showPaymobBottomSheet = false) }
        viewModelScope.launch {
            _effect.send(CheckoutSummaryContract.Effect.ShowError(UiText.Plain(message)))
        }
    }

    private fun handlePaymentPending() {
        _state.update { it.copy(showPaymobBottomSheet = false) }
        // Maybe navigate to orders with pending status
    }
}
