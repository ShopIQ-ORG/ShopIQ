package com.iti.presentation.screens.auth.signin

import com.iti.presentation.util.UiText

data class SignInState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: UiText? = null
)

sealed class SignInIntent {
    data class EmailChanged(val email: String) : SignInIntent()
    data class PasswordChanged(val password: String) : SignInIntent()
    data class LoginWithGoogle(val idToken: String) : SignInIntent()
    data class LoginWithFacebook(val accessToken: String) : SignInIntent()
    object Login : SignInIntent()
    object LoginAsGuest : SignInIntent()
    object ForgotPassword : SignInIntent()
}

sealed class SignInEffect {
    object NavigateToHome : SignInEffect()
    object NavigateToForgotPassword : SignInEffect()
    data class ShowError(val message: UiText) : SignInEffect()
}