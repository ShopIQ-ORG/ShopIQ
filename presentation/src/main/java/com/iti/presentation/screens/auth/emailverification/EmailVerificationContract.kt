package com.iti.presentation.screens.auth.emailverification

import com.iti.presentation.util.UiText

object EmailVerificationContract {

    data class State(
        val email: String = "",
        val isSendingLink: Boolean = false,
        val isCheckingVerification: Boolean = false,
        val resendCooldownSeconds: Int = 0,
        val error: UiText? = null
    )

    sealed interface Event {
        object SendVerificationLink : Event
        object CheckVerification : Event
        object BackToLogin : Event
    }

    sealed interface Effect {
        object NavigateToSignIn : Effect
        object NavigateToHome : Effect
        data class ShowInfo(val message: UiText) : Effect
        data class ShowError(val message: UiText) : Effect
    }
}