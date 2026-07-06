//
//  CheckoutViewModel.kt
//  ShopIQ
//
//  Created by Antigravity on 7/6/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.presentation.screens.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.models.Result
import com.iti.domain.usecases.cart.GetCartUseCase
import com.iti.domain.usecases.cart.ClearCartUseCase
import com.iti.domain.usecases.checkout.CreateDraftOrderUseCase
import com.iti.domain.usecases.checkout.CompleteDraftOrderUseCase
import com.iti.domain.usecases.auth.GetCurrentUserUseCase
import com.iti.presentation.util.toUiMessage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CheckoutViewModel(
    private val getCartUseCase: GetCartUseCase,
    private val createDraftOrderUseCase: CreateDraftOrderUseCase,
    private val completeDraftOrderUseCase: CompleteDraftOrderUseCase,
    private val clearCartUseCase: ClearCartUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CheckoutContract.State())
    val state: StateFlow<CheckoutContract.State> = _state.asStateFlow()

    private val _effect = Channel<CheckoutContract.Effect>()
    val effect = _effect.receiveAsFlow()

    init {
        loadCart()
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            when (val result = getCurrentUserUseCase()) {
                is Result.Success -> {
                    _state.update { it.copy(currentUser = result.data) }
                }
                else -> Unit
            }
        }
    }

    fun onEvent(event: CheckoutContract.Event) {
        when (event) {
            is CheckoutContract.Event.AddressSelected -> {
                _state.update { it.copy(selectedAddress = event.address, currentStep = 2) }
            }
            CheckoutContract.Event.PaymentConfirmed -> {
                createDraftOrder()
            }
            CheckoutContract.Event.PlaceOrder -> {
                completeDraftOrder()
            }
            CheckoutContract.Event.NavigateBack -> {
                navigateBack()
            }
        }
    }

    private fun loadCart() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            getCartUseCase().collect { result ->
                when (result) {
                    is Result.Success -> {
                        _state.update { it.copy(isLoading = false, cart = result.data) }
                    }
                    is Result.Failure -> {
                        _state.update { it.copy(isLoading = false, error = result.exception.toUiMessage()) }
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun createDraftOrder() {
        val cart = _state.value.cart ?: return
        val address = _state.value.selectedAddress ?: return
        
        val lineItems = cart.items.map { item ->
            Pair(item.variantId, item.quantity)
        }

        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            when (val result = createDraftOrderUseCase(lineItems, address)) {
                is Result.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            draftOrder = result.data,
                            currentStep = 3
                        )
                    }
                }
                is Result.Failure -> {
                    val errorMsg = result.exception.toUiMessage()
                    _state.update { it.copy(isLoading = false, error = errorMsg) }
                    _effect.send(CheckoutContract.Effect.ShowError(errorMsg))
                }
                else -> Unit
            }
        }
    }

    private fun completeDraftOrder() {
        val draftOrder = _state.value.draftOrder ?: return
        
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            when (val result = completeDraftOrderUseCase(draftOrder.id)) {
                is Result.Success -> {
                    _state.update { it.copy(draftOrder = result.data) }
                    clearCartAndFinish()
                }
                is Result.Failure -> {
                    val errorMsg = result.exception.toUiMessage()
                    _state.update { it.copy(isLoading = false, error = errorMsg) }
                    _effect.send(CheckoutContract.Effect.ShowError(errorMsg))
                }
                else -> Unit
            }
        }
    }

    private fun clearCartAndFinish() {
        viewModelScope.launch {
            when (clearCartUseCase()) {
                is Result.Success -> {
                    _state.update { it.copy(isLoading = false, currentStep = 4) }
                }
                is Result.Failure -> {
                    _state.update { it.copy(isLoading = false, currentStep = 4) }
                }
                else -> Unit
            }
        }
    }

    private fun navigateBack() {
        viewModelScope.launch {
            val current = _state.value.currentStep
            if (current > 1 && current < 4) {
                _state.update { it.copy(currentStep = current - 1) }
            } else if (current == 4) {
                _effect.send(CheckoutContract.Effect.NavigateToHome)
            } else {
                _effect.send(CheckoutContract.Effect.NavigateBack)
            }
        }
    }
}
