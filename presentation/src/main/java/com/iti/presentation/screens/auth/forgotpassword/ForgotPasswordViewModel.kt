package com.iti.presentation.screens.auth.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.models.Result
import com.iti.domain.usecases.auth.SendPasswordResetEmailUseCase
import com.iti.presentation.R
import com.iti.presentation.util.AuthValidator
import com.iti.presentation.util.UiText
import com.iti.presentation.util.toUiMessage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val sendPasswordResetEmailUseCase: SendPasswordResetEmailUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ForgotPasswordContract.State())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ForgotPasswordContract.Effect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: ForgotPasswordContract.Event) {
        when (event) {
            is ForgotPasswordContract.Event.EmailChanged -> {
                _state.update { it.copy(email = event.email, fieldErrors = it.fieldErrors - "email") }
            }

            ForgotPasswordContract.Event.SendResetLink -> sendResetLink()

            ForgotPasswordContract.Event.BackToLogin -> {
                sendEffect(ForgotPasswordContract.Effect.NavigateBack)
            }
        }
    }

    private fun sendResetLink() {
        val email = _state.value.email.trim()

        val errors = AuthValidator.validateForgotPassword(email)
        if (errors.isNotEmpty()) {
            _state.update { it.copy(fieldErrors = errors) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, fieldErrors = emptyMap()) }

            when (val result = sendPasswordResetEmailUseCase(email)) {
                is Result.Success -> {
                    _state.update { it.copy(linkSent = true) }
                    sendEffect(
                        ForgotPasswordContract.Effect.ShowSuccess(
                            UiText.StringResource(R.string.reset_link_sent)
                        )
                    )
                }

                is Result.Failure -> {
                    val message = result.exception.toUiMessage()
                    sendEffect(ForgotPasswordContract.Effect.ShowError(message))
                }

                is Result.Loading -> Unit
            }

            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun sendEffect(effect: ForgotPasswordContract.Effect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}