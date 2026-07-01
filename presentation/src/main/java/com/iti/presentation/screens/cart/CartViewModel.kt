package com.iti.presentation.screens.cart

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.exceptions.AuthException
import com.iti.domain.models.Result
import com.iti.domain.models.cart.Cart
import com.iti.domain.models.cart.recalculatedAfterQuantityChange
import com.iti.domain.usecases.cart.ApplyDiscountCodesUseCase
import com.iti.domain.usecases.cart.GetCartUseCase
import com.iti.domain.usecases.cart.RemoveCartItemUseCase
import com.iti.domain.usecases.cart.UpdateCartItemQuantityUseCase
import com.iti.presentation.R
import com.iti.presentation.util.UiText
import com.iti.presentation.util.toUiMessage
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.microseconds
import kotlin.time.Duration.Companion.milliseconds

class CartViewModel(
    private val getCartUseCase: GetCartUseCase,
    private val updateCartItemQuantityUseCase: UpdateCartItemQuantityUseCase,
    private val removeItemUseCase: RemoveCartItemUseCase,
    private val applyPromoCodeUseCase: ApplyDiscountCodesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CartContract.State())
    val state: StateFlow<CartContract.State> = _state.asStateFlow()

    private val _effect = Channel<CartContract.Effect>()
    val effect = _effect.receiveAsFlow()

    private val quantityUpdateJobs = mutableMapOf<String, Job>()

    private val quantityRollbackSnapshots = mutableMapOf<String, Cart>()

    init {
        loadCart()
    }

    fun onEvent(event: CartContract.Event) {
        when (event) {
            is CartContract.Event.IncreaseQuantity -> increaseQuantity(event.itemId)
            is CartContract.Event.DecreaseQuantity -> decreaseQuantity(event.itemId)
            is CartContract.Event.RemoveItem -> removeItem(event.itemId)
            is CartContract.Event.TogglePromoExpanded -> _state.update { it.copy(isPromoExpanded = !it.isPromoExpanded) }
            is CartContract.Event.PromoInputChanged -> _state.update {
                it.copy(promoInput = event.value, promoError = null)
            }

            is CartContract.Event.ApplyPromoCode -> applyPromo()
            is CartContract.Event.ProceedToCheckout -> viewModelScope.launch {
                _effect.send(CartContract.Effect.NavigateToCheckout)
            }

            is CartContract.Event.Refresh -> refreshCart()
            is CartContract.Event.Retry -> loadCart()
        }
    }

    private fun loadCart() {
        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            getCartUseCase().collect { result ->
                when (result) {
                    is Result.Success -> _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            cart = result.data,
                            accessRestricted = false
                        )
                    }

                    is Result.Failure -> handleFailure(result.exception) { message ->
                        _state.update { it.copy(error = message) }
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun refreshCart() {
        _state.update { it.copy(isRefreshing = true) }

        viewModelScope.launch {
            getCartUseCase().collect { result ->
                when (result) {
                    is Result.Success -> _state.update {
                        it.copy(
                            isRefreshing = false,
                            isLoading = false,
                            cart = result.data,
                            accessRestricted = false
                        )
                    }

                    is Result.Failure -> handleFailure(result.exception)
                    else -> Unit
                }
            }
        }
    }

    private fun increaseQuantity(itemId: String) {
        if (isItemBusy(itemId)) return

        val currentItem = _state.value.cart?.items?.find { it.id == itemId } ?: return
        val newQuantity = currentItem.quantity + 1

        captureRollbackSnapshotIfNeeded(itemId)
        updateQuantityOptimistically(itemId, newQuantity)
        debounceQuantityUpdate(itemId, newQuantity)
    }

    private fun decreaseQuantity(itemId: String) {
        if (isItemBusy(itemId)) return

        val currentItem = _state.value.cart?.items?.find { it.id == itemId } ?: return

        if (currentItem.quantity <= 1) return

        val newQuantity = currentItem.quantity - 1

        captureRollbackSnapshotIfNeeded(itemId)
        updateQuantityOptimistically(itemId, newQuantity)
        debounceQuantityUpdate(itemId, newQuantity)
    }

    private fun isItemBusy(itemId: String): Boolean = _state.value.itemBeingRemoved == itemId

    private fun captureRollbackSnapshotIfNeeded(itemId: String) {
        if (quantityUpdateJobs[itemId]?.isActive != true) {
            _state.value.cart?.let { quantityRollbackSnapshots[itemId] = it }
        }
    }

    private fun debounceQuantityUpdate(itemId: String, newQuantity: Int) {
        quantityUpdateJobs[itemId]?.cancel()

        quantityUpdateJobs[itemId] = viewModelScope.launch {
            delay(500L.milliseconds)
            val result = withContext(NonCancellable) {
                updateCartItemQuantityUseCase(itemId, newQuantity)
            }
            when (result) {
                is Result.Success -> {
                    quantityRollbackSnapshots.remove(itemId)
                }

                is Result.Failure -> {
                    rollbackQuantityChange(itemId)
                    handleFailure(result.exception)
                }

                else -> Unit
            }
        }
    }

    private fun rollbackQuantityChange(itemId: String) {
        val snapshot = quantityRollbackSnapshots.remove(itemId) ?: return
        _state.update { it.copy(cart = snapshot) }
    }

    private fun updateQuantityOptimistically(itemId: String, quantity: Int) {
        _state.update { state ->
            val cart = state.cart ?: return@update state
            val updatedItems = cart.items.map { item ->
                if (item.id == itemId) item.copy(quantity = quantity) else item
            }
            state.copy(cart = cart.copy(items = updatedItems).recalculatedAfterQuantityChange())
        }
    }

    private fun removeItem(itemId: String) {
        if (_state.value.itemBeingRemoved != null) return
        quantityUpdateJobs[itemId]?.cancel()
        quantityUpdateJobs.remove(itemId)
        quantityRollbackSnapshots.remove(itemId)

        _state.update { it.copy(itemBeingRemoved = itemId) }


        viewModelScope.launch {

            val result = withContext(NonCancellable) {
                removeItemUseCase(itemId)
            }

            when (result) {
                is Result.Success -> {
                    _state.update { state ->
                        val cart = state.cart ?: return@update state.copy(itemBeingRemoved = null)
                        val updatedItems = cart.items.filterNot { it.id == itemId }
                        state.copy(
                            itemBeingRemoved = null,
                            cart = cart.copy(items = updatedItems).recalculatedAfterQuantityChange()
                        )
                    }
                }

                is Result.Failure -> {
                    _state.update { it.copy(itemBeingRemoved = null) }
                    handleFailure(result.exception)
                }

                else -> Unit
            }

        }
    }

    private fun applyPromo() {
        val code = _state.value.promoInput.trim()
        if (code.isEmpty()) {
            _state.update { it.copy(promoError = UiText.StringResource(R.string.cart_promo_empty_error)) }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isApplyingPromo = true, promoError = null) }
            when (val result = applyPromoCodeUseCase(arrayListOf(code))) {
                is Result.Success -> _state.update {
                    it.copy(isApplyingPromo = false, cart = result.data)
                }

                is Result.Failure -> {
                    _state.update { it.copy(isApplyingPromo = false) }
                    handleFailure(result.exception) { message ->
                        _state.update { it.copy(promoError = message) }
                    }
                }

                else -> Unit
            }
        }
    }

    private suspend fun handleFailure(
        exception: Throwable,
        onError: (UiText) -> Unit = {}
    ) {
        _state.update { it.copy(isLoading = false, isRefreshing = false) }

        if (exception is AuthException.UnauthorizedAccess) {
            _state.update { it.copy(accessRestricted = true) }
        } else {
            val message = exception.toUiMessage()
            onError(message)
            _effect.send(CartContract.Effect.ShowError(message))
        }
    }
}