package com.iti.presentation.screens.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.models.Result
import com.iti.domain.models.auth.RegistrationInfo
import com.iti.domain.usecases.auth.RegisterUseCase
import com.iti.presentation.R
import com.iti.presentation.core.UiText
import com.iti.presentation.core.toUiMessage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val registerUseCase: RegisterUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(SignUpState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SignUpEffect>()
    val effect = _effect.asSharedFlow()

    fun onIntent(intent: SignUpIntent) {
        when (intent) {
            is SignUpIntent.FullNameChanged -> _state.update { it.copy(fullName = intent.fullName) }
            is SignUpIntent.EmailChanged -> _state.update { it.copy(email = intent.email) }
            is SignUpIntent.PhoneChanged -> _state.update { it.copy(phone = intent.phone) }
            is SignUpIntent.PasswordChanged -> _state.update { it.copy(password = intent.password) }
            is SignUpIntent.ConfirmPasswordChanged -> _state.update { it.copy(confirmPassword = intent.confirmPassword) }
            is SignUpIntent.AgreeToTermsChanged -> _state.update { it.copy(agreeToTerms = intent.checked) }
            is SignUpIntent.Register -> register()
            is SignUpIntent.NavigateToTerms -> sendEffect(SignUpEffect.NavigateToTerms)
            is SignUpIntent.NavigateToPrivacyPolicy -> sendEffect(SignUpEffect.NavigateToPrivacyPolicy)
        }
    }

    private fun register() {
        val state = _state.value

        if (state.fullName.isBlank()) {
            handleValidationError(UiText.StringResource(R.string.error_full_name_required))
            return
        }
        if (state.email.isBlank()) {
            handleValidationError(UiText.StringResource(R.string.error_email_required))
            return
        }
        if (state.phone.isBlank()) {
            handleValidationError(UiText.StringResource(R.string.error_phone_required))
            return
        }
        if (state.password.isBlank()) {
            handleValidationError(UiText.StringResource(R.string.error_password_required))
            return
        }
        if (state.confirmPassword.isBlank()) {
            handleValidationError(UiText.StringResource(R.string.error_confirm_password_required))
            return
        }

        if (state.password != state.confirmPassword) {
            handleValidationError(UiText.StringResource(R.string.error_passwords_do_not_match))
            return
        }

        if (!state.agreeToTerms) {
            handleValidationError(UiText.StringResource(R.string.error_agree_to_terms))
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val info = RegistrationInfo(
                    fullName = state.fullName,
                    email    = state.email,
                    phone    = state.phone,
                    password = state.password,
                )
                when (val result = registerUseCase(info)) {
                    is Result.Success -> sendEffect(SignUpEffect.NavigateToHome)
                    is Result.Failure -> handleFailure(result.exception)
                    is Result.Loading -> Unit
                }
            } catch (e: Exception) {
                handleFailure(e)
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun handleValidationError(message: UiText) {
        _state.update { it.copy(error = message) }
        sendEffect(SignUpEffect.ShowError(message))
    }

    private fun handleFailure(exception: Throwable) {
        val message = exception.toUiMessage()
        _state.update { it.copy(error = message) }
        sendEffect(SignUpEffect.ShowError(message))
    }

    private fun sendEffect(effect: SignUpEffect) {
        viewModelScope.launch { _effect.emit(effect) }
    }
}