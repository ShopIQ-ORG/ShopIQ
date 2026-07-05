package com.iti.presentation.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.models.Result
import com.iti.domain.models.User
import com.iti.domain.usecases.auth.GetCurrentUserUseCase
import com.iti.domain.usecases.onboarding.IsOnboardingCompletedUseCase
import com.iti.domain.usecases.shopify.GetValidShopifyTokenUseCase
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
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getValidShopifyTokenUseCase: GetValidShopifyTokenUseCase
) : ViewModel() {

    private val _destination = MutableStateFlow<SplashDestination?>(null)
    val destination: StateFlow<SplashDestination?> = _destination.asStateFlow()

    fun checkDestination() {
        _destination.value = null
        viewModelScope.launch {
            try {
                val isOnboardingDone = isOnboardingCompletedUseCase().first()
                if (!isOnboardingDone) {
                    _destination.value = SplashDestination.OnBoarding
                    return@launch
                }

                _destination.value = when (val userResult = getCurrentUserUseCase()) {
                    is Result.Success -> resolveDestinationFor(userResult.data)
                    else -> SplashDestination.SignIn
                }
            } catch (e: Exception) {
                _destination.value = SplashDestination.SignIn
            }
        }
    }

    private suspend fun resolveDestinationFor(user: User): SplashDestination {
        return when (user) {
            User.GuestUser -> SplashDestination.Home

            is User.AuthenticatedUser -> {
                if (!user.isEmailVerified) {
                    SplashDestination.SignIn
                } else when (getValidShopifyTokenUseCase()) {
                    is Result.Success -> SplashDestination.Home
                    else -> SplashDestination.SignIn
                }
            }
        }
    }
}