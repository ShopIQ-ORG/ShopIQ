package com.iti.presentation.screens.auth.emailverification

import com.iti.presentation.util.UiText

object EmailVerificationContract {

    data class State(
        val email: String = "",
        val isLoading: Boolean = false,
        val error: UiText? = null
    ) {
        val canSendVerificationLink: Boolean
            get() = !isLoading
    }

    sealed class Event {
        object SendVerificationLink : Event()
        object BackToLogin : Event()
    }

    sealed class Effect {
        object NavigateToSignIn : Effect()
        data class ShowError(val message: UiText) : Effect()
        data class ShowInfo(val message: UiText) : Effect()
    }
}