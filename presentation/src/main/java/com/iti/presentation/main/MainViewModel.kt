package com.iti.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.usecase.IsOnboardingCompletedUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

import com.iti.presentation.util.Constants

class MainViewModel(
    private val isOnboardingCompletedUseCase: IsOnboardingCompletedUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<MainContract.State>(MainContract.State.Loading)
    val state: StateFlow<MainContract.State> = _state.asStateFlow()

    init {
        sendIntent(MainContract.Intent.CheckOnboarding)
    }

    fun sendIntent(intent: MainContract.Intent) {
        when (intent) {
            is MainContract.Intent.CheckOnboarding -> checkOnboardingStatus()
        }
    }

    private fun checkOnboardingStatus() {
        viewModelScope.launch {
            delay(Constants.SPLASH_DELAY_MS)
            isOnboardingCompletedUseCase().collectLatest { completed ->
                if (completed) {
                    _state.value = MainContract.State.ShowHome
                } else {
                    _state.value = MainContract.State.ShowOnboarding
                }
            }
        }
    }
}
