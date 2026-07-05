package com.iti.presentation.screens.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.models.Result
import com.iti.domain.models.User
import com.iti.domain.models.auth.RegistrationInfo
import com.iti.domain.usecases.auth.RegisterUseCase
import com.iti.presentation.screens.auth.signup.SignUpContract
import com.iti.presentation.util.AuthValidator
import com.iti.presentation.util.toUiMessage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val registerUseCase: RegisterUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(SignUpContract.State())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SignUpContract.Effect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: SignUpContract.Event) {
        when (event) {
            is SignUpContract.Event.FullNameChanged -> {
                _state.update { it.copy(fullName = event.fullName, fieldErrors = it.fieldErrors - "fullName") }
            }

            is SignUpContract.Event.EmailChanged -> {
                _state.update { it.copy(email = event.email, fieldErrors = it.fieldErrors - "email") }
            }

            is SignUpContract.Event.PhoneChanged -> {
                _state.update { it.copy(phone = event.phone, fieldErrors = it.fieldErrors - "phone") }
            }

            is SignUpContract.Event.PasswordChanged -> {
                _state.update { it.copy(password = event.password, fieldErrors = it.fieldErrors - "password") }
            }

            is SignUpContract.Event.ConfirmPasswordChanged -> {
                _state.update { it.copy(confirmPassword = event.confirmPassword, fieldErrors = it.fieldErrors - "confirmPassword") }
            }

            is SignUpContract.Event.AgreeToTermsChanged -> {
                _state.update { it.copy(agreeToTerms = event.checked, fieldErrors = it.fieldErrors - "terms") }
            }

            SignUpContract.Event.Register -> register()

            SignUpContract.Event.NavigateToTerms -> {
                sendEffect(SignUpContract.Effect.NavigateToTerms)
            }

            SignUpContract.Event.NavigateToPrivacyPolicy -> {
                sendEffect(SignUpContract.Effect.NavigateToPrivacyPolicy)
            }
        }
    }

    private fun register() {
        val state = _state.value

        val errors = AuthValidator.validateSignUp(
            fullName = state.fullName,
            email = state.email,
            phone = state.phone,
            password = state.password,
            confirmPassword = state.confirmPassword,
            agreeToTerms = state.agreeToTerms
        )
        if (errors.isNotEmpty()) {
            _state.update { it.copy(fieldErrors = errors) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, fieldErrors = emptyMap()) }

            try {
                val info = RegistrationInfo(
                    fullName = state.fullName,
                    email = state.email,
                    phone = state.phone,
                    password = state.password
                )

                when (val result = registerUseCase(info)) {
                    is Result.Success -> {
                        val user = result.data
                        if (user is User.AuthenticatedUser && !user.isEmailVerified) {
                            sendEffect(SignUpContract.Effect.NavigateToEmailVerification(user.email))
                        } else {
                            sendEffect(SignUpContract.Effect.NavigateToHome)
                        }
                    }

                    is Result.Failure -> {
                        val message = result.exception.toUiMessage()
                        sendEffect(SignUpContract.Effect.ShowError(message))
                    }
                    is Result.Loading -> Unit
                }
            } catch (e: Exception) {
                val message = e.toUiMessage()
                sendEffect(SignUpContract.Effect.ShowError(message))
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun sendEffect(effect: SignUpContract.Effect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}