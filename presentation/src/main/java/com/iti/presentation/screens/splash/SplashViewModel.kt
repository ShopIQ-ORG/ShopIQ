package com.iti.presentation.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.models.Result
import com.iti.domain.usecases.auth.GetCurrentUserUseCase
import com.iti.domain.usecases.onboarding.IsOnboardingCompletedUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed interface SplashDestination {
    object OnBoarding : SplashDestination
    object SignIn : SplashDestination
    object Home : SplashDestination
}

class SplashViewModel(
    private val isOnboardingCompletedUseCase: IsOnboardingCompletedUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _destination = MutableStateFlow<SplashDestination?>(null)
    val destination: StateFlow<SplashDestination?> = _destination.asStateFlow()

    init {
        checkNavDestination()
    }

    private fun checkNavDestination() {
        viewModelScope.launch {
            val completed = isOnboardingCompletedUseCase().first()
            if (!completed) {
                _destination.value = SplashDestination.OnBoarding
            } else {
                when (getCurrentUserUseCase()) {
                    is Result.Success -> _destination.value = SplashDestination.Home
                    is Result.Failure -> _destination.value = SplashDestination.SignIn
                    else -> {}
                }
            }
        }
    }
}