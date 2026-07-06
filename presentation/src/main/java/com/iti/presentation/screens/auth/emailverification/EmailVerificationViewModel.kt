package com.iti.presentation.screens.auth.emailverification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.domain.models.Result
import com.iti.domain.models.User
import com.iti.domain.usecases.auth.ReloadAndGetCurrentUserUseCase
import com.iti.domain.usecases.auth.SendEmailVerificationUseCase
import com.iti.domain.usecases.shopify.GetValidShopifyTokenUseCase
import com.iti.presentation.R
import com.iti.presentation.util.UiText
import com.iti.presentation.util.toUiMessage
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val RESEND_COOLDOWN_SECONDS = 30
private const val POLL_INTERVAL_MILLIS = 3000L

class EmailVerificationViewModel(
    email: String,
    private val sendEmailVerificationUseCase: SendEmailVerificationUseCase,
    private val reloadAndGetCurrentUserUseCase: ReloadAndGetCurrentUserUseCase,
    private val getValidShopifyTokenUseCase: GetValidShopifyTokenUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(EmailVerificationContract.State(email = email))
    val state = _state.asStateFlow()

    private val _effect = Channel<EmailVerificationContract.Effect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var pollingJob: Job? = null
    private var cooldownJob: Job? = null

    init {
        sendLink()
    }

    fun onEvent(event: EmailVerificationContract.Event) {
        when (event) {
            EmailVerificationContract.Event.SendVerificationLink -> sendLink()
            EmailVerificationContract.Event.CheckVerification -> checkVerificationManually()
            EmailVerificationContract.Event.BackToLogin -> {
                stopPolling()
                sendEffect(EmailVerificationContract.Effect.NavigateToSignIn)
            }
        }
    }

    fun startPolling() {
        if (pollingJob?.isActive == true) return
        pollingJob = viewModelScope.launch {
            performCheck()
            while (true) {
                delay(POLL_INTERVAL_MILLIS)
                performCheck()
            }
        }
    }

    fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }

    private fun checkVerificationManually() {
        if (_state.value.isCheckingVerification) return
        viewModelScope.launch {
            _state.update { it.copy(isCheckingVerification = true) }

            when (val result = performCheck()) {
                is Result.Success -> {
                    if (!result.data) {
                        sendEffect(
                            EmailVerificationContract.Effect.ShowError(
                                UiText.StringResource(R.string.email_not_verified_yet)
                            )
                        )
                    }
                }
                is Result.Failure -> handleFailure(result.exception)
                is Result.Loading -> Unit
            }

            _state.update { it.copy(isCheckingVerification = false) }
        }
    }

    private suspend fun performCheck(): Result<Boolean> {
        return when (val result = reloadAndGetCurrentUserUseCase()) {
            is Result.Success -> {
                val user = result.data
                val isVerified = user is User.AuthenticatedUser && user.isEmailVerified
                if (isVerified) {
                    stopPolling()
                    resolveAfterVerification()
                }
                Result.Success(isVerified)
            }
            is Result.Failure -> result
            is Result.Loading -> Result.Loading
        }
    }

    private suspend fun resolveAfterVerification() {
        when (getValidShopifyTokenUseCase()) {
            is Result.Success -> sendEffect(EmailVerificationContract.Effect.NavigateToHome)
            else -> sendEffect(
                EmailVerificationContract.Effect.ShowError(
                    UiText.StringResource(R.string.error_shopify_token_unavailable)
                )
            )
        }
    }

    private fun sendLink() {
        val current = _state.value
        if (current.isSendingLink || current.resendCooldownSeconds > 0) return

        viewModelScope.launch {
            _state.update { it.copy(isSendingLink = true, error = null) }

            when (val result = sendEmailVerificationUseCase()) {
                is Result.Success -> {
                    sendEffect(
                        EmailVerificationContract.Effect.ShowInfo(
                            UiText.StringResource(R.string.verification_link_sent)
                        )
                    )
                    startCooldown()
                }
                is Result.Failure -> handleFailure(result.exception)
                is Result.Loading -> Unit
            }

            _state.update { it.copy(isSendingLink = false) }
        }
    }

    private fun startCooldown(seconds: Int = RESEND_COOLDOWN_SECONDS) {
        cooldownJob?.cancel()
        cooldownJob = viewModelScope.launch {
            for (remaining in seconds downTo 1) {
                _state.update { it.copy(resendCooldownSeconds = remaining) }
                delay(1000)
            }
            _state.update { it.copy(resendCooldownSeconds = 0) }
        }
    }

    private fun handleFailure(exception: Throwable) {
        val message = exception.toUiMessage()
        _state.update { it.copy(error = message) }
        sendEffect(EmailVerificationContract.Effect.ShowError(message))
    }

    private fun sendEffect(effect: EmailVerificationContract.Effect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }

    override fun onCleared() {
        stopPolling()
        cooldownJob?.cancel()
        super.onCleared()
    }
}