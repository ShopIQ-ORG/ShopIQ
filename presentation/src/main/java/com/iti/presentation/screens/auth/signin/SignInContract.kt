package com.iti.presentation.screens.auth.signin

import com.iti.presentation.util.UiText

object SignInContract {

    data class State(
        val email: String = "",
        val password: String = "",
        val isLoading: Boolean = false,
        val fieldErrors: Map<String, UiText> = emptyMap()
    )

    sealed class Event {
        data class EmailChanged(val email: String) : Event()
        data class PasswordChanged(val password: String) : Event()
        data class LoginWithGoogle(val idToken: String, val photoUrl: String? = null) : Event()
        object Login : Event()
        object LoginAsGuest : Event()
        object ForgotPassword : Event()
    }

    sealed class Effect {
        object NavigateToHome : Effect()
        object NavigateToForgotPassword : Effect()
        class NavigateToEmailVerification(val email: String): Effect()
        data class ShowError(val message: UiText) : Effect()
    }
}