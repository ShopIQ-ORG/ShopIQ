package com.iti.presentation.screens.auth.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.exceptions.AuthException
import com.iti.domain.models.Result
import com.iti.domain.models.auth.LoginCredentials
import com.iti.domain.usecases.auth.LoginAsGuestUseCase
import com.iti.domain.usecases.auth.LoginUseCase
import com.iti.domain.usecases.auth.LoginWithGoogleUseCase
import com.iti.presentation.screens.auth.signin.SignInContract
import com.iti.presentation.util.AuthValidator
import com.iti.presentation.util.UiText
import com.iti.presentation.util.toUiMessage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignInViewModel(
    private val loginUseCase: LoginUseCase,
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    private val loginAsGuestUseCase: LoginAsGuestUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(SignInContract.State())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SignInContract.Effect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: SignInContract.Event) {
        when (event) {
            is SignInContract.Event.EmailChanged -> {
                _state.update { it.copy(email = event.email, fieldErrors = it.fieldErrors - "email") }
            }

            is SignInContract.Event.PasswordChanged -> {
                _state.update { it.copy(password = event.password, fieldErrors = it.fieldErrors - "password") }
            }

            SignInContract.Event.Login -> login()

            is SignInContract.Event.LoginWithGoogle -> loginWithGoogle(event.idToken)

            SignInContract.Event.LoginAsGuest -> loginAsGuest()

            SignInContract.Event.ForgotPassword -> {
                sendEffect(SignInContract.Effect.NavigateToForgotPassword)
            }
        }
    }

    private fun login() {
        val errors = AuthValidator.validateSignIn(_state.value.email, _state.value.password)
        if (errors.isNotEmpty()) {
            _state.update { it.copy(fieldErrors = errors) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, fieldErrors = emptyMap()) }

            when (val result = loginUseCase(LoginCredentials(_state.value.email, _state.value.password))) {
                is Result.Success -> sendEffect(SignInContract.Effect.NavigateToHome)
                is Result.Failure -> handleFailure(result.exception)
                is Result.Loading -> Unit
            }

            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, fieldErrors = emptyMap()) }

            when (val result = loginWithGoogleUseCase(idToken)) {
                is Result.Success -> sendEffect(SignInContract.Effect.NavigateToHome)
                is Result.Failure -> handleFailure(result.exception)
                is Result.Loading -> Unit
            }

            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun loginAsGuest() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, fieldErrors = emptyMap()) }

            when (val result = loginAsGuestUseCase()) {
                is Result.Success -> sendEffect(SignInContract.Effect.NavigateToHome)
                is Result.Failure -> handleFailure(result.exception)
                is Result.Loading -> Unit
            }

            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun handleFailure(exception: Throwable) {
        if (exception is AuthException.EmailNotVerified) {
            sendEffect(SignInContract.Effect.NavigateToEmailVerification(exception.email))
            return
        }

        val message = exception.toUiMessage()
        sendEffect(SignInContract.Effect.ShowError(message))
    }

    private fun sendEffect(effect: SignInContract.Effect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}