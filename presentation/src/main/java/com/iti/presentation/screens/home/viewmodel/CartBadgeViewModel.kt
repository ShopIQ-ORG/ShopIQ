package com.iti.presentation.screens.home.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.usecases.cart.ObserveCartItemCountUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class CartBadgeViewModel(
    observeCartItemCountUseCase: ObserveCartItemCountUseCase
) : ViewModel() {

    val cartItemCount: StateFlow<Int> = observeCartItemCountUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = 0
        )
}