package com.iti.presentation.screens.auth.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.models.Result
import com.iti.domain.models.auth.LoginCredentials
import com.iti.domain.usecases.auth.LoginAsGuestUseCase
import com.iti.domain.usecases.auth.LoginUseCase
import com.iti.domain.usecases.auth.LoginWithFacebookUseCase
import com.iti.domain.usecases.auth.LoginWithGoogleUseCase
import com.iti.presentation.core.UiText
import com.iti.presentation.core.toUiMessage
import com.iti.presentation.R
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignInViewModel(
    private val loginUseCase: LoginUseCase,
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    private val loginWithFacebookUseCase: LoginWithFacebookUseCase,
    private val loginAsGuestUseCase: LoginAsGuestUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SignInEffect>()
    val effect = _effect.asSharedFlow()

    fun onIntent(intent: SignInIntent) {
        when (intent) {
            is SignInIntent.EmailChanged -> _state.update { it.copy(email = intent.email) }
            is SignInIntent.PasswordChanged -> _state.update { it.copy(password = intent.password) }
            is SignInIntent.Login -> login()
            is SignInIntent.LoginWithGoogle -> loginWithGoogle(intent.idToken)
            is SignInIntent.LoginWithFacebook -> loginWithFacebook(intent.accessToken)
            is SignInIntent.LoginAsGuest -> loginAsGuest()
            is SignInIntent.ForgotPassword -> sendEffect(SignInEffect.NavigateToForgotPassword)
        }
    }

    private fun login() {
        val email = _state.value.email
        val password = _state.value.password
        if (email.isBlank()) {
            handleValidationError(UiText.StringResource(R.string.error_email_or_phone_required))
            return
        }
        if (password.isBlank()) {
            handleValidationError(UiText.StringResource(R.string.error_password_required))
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result =
                loginUseCase(LoginCredentials(email, password))) {
                is Result.Success -> sendEffect(SignInEffect.NavigateToHome)
                is Result.Failure -> handleFailure(result.exception)
                is Result.Loading -> Unit
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = loginWithGoogleUseCase(idToken)) {
                is Result.Success -> sendEffect(SignInEffect.NavigateToHome)
                is Result.Failure -> handleFailure(result.exception)
                is Result.Loading -> Unit
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun loginWithFacebook(accessToken: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = loginWithFacebookUseCase(accessToken)) {
                is Result.Success -> sendEffect(SignInEffect.NavigateToHome)
                is Result.Failure -> handleFailure(result.exception)
                is Result.Loading -> Unit
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun loginAsGuest() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = loginAsGuestUseCase()) {
                is Result.Success -> sendEffect(SignInEffect.NavigateToHome)
                is Result.Failure -> handleFailure(result.exception)
                is Result.Loading -> Unit
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun handleFailure(exception: Throwable) {
        val message = exception.toUiMessage()
        _state.update { it.copy(error = message) }
        sendEffect(SignInEffect.ShowError(message))
    }

    private fun handleValidationError(message: UiText) {
        _state.update { it.copy(error = message) }
        sendEffect(SignInEffect.ShowError(message))
    }

    private fun sendEffect(effect: SignInEffect) {
        viewModelScope.launch { _effect.emit(effect) }
    }
}