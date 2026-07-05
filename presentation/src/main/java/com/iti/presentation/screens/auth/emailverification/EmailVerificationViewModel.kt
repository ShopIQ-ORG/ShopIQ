package com.iti.presentation.screens.auth.emailverification

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.models.Result
import com.iti.domain.usecases.auth.SendEmailVerificationUseCase
import com.iti.presentation.R
import com.iti.presentation.util.UiText
import com.iti.presentation.util.toUiMessage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EmailVerificationViewModel(
    private val sendEmailVerificationUseCase: SendEmailVerificationUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(
        EmailVerificationContract.State(
            email = savedStateHandle.get<String>("email").orEmpty()
        )
    )
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<EmailVerificationContract.Effect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: EmailVerificationContract.Event) {
        when (event) {
            EmailVerificationContract.Event.SendVerificationLink -> sendLink()

            EmailVerificationContract.Event.BackToLogin -> {
                sendEffect(EmailVerificationContract.Effect.NavigateToSignIn)
            }
        }
    }

    private fun sendLink() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }

            when (val result = sendEmailVerificationUseCase()) {
                is Result.Success -> {
                    sendEffect(
                        EmailVerificationContract.Effect.ShowInfo(
                            UiText.StringResource(R.string.verification_link_sent)
                        )
                    )
                }
                is Result.Failure -> handleFailure(result.exception)
                is Result.Loading -> Unit
            }

            _state.update {
                it.copy(isLoading = false)
            }
        }
    }

    private fun handleFailure(exception: Throwable) {
        val message = exception.toUiMessage()

        _state.update {
            it.copy(error = message)
        }

        sendEffect(
            EmailVerificationContract.Effect.ShowError(message)
        )
    }

    private fun sendEffect(effect: EmailVerificationContract.Effect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}