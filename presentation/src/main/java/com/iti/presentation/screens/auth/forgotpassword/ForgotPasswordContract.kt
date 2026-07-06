package com.iti.presentation.screens.auth.forgotpassword
import com.iti.presentation.util.UiText

object ForgotPasswordContract {

    data class State(
        val email: String = "",
        val isLoading: Boolean = false,
        val linkSent: Boolean = false,
        val fieldErrors: Map<String, UiText> = emptyMap()
    )

    sealed class Event {
        data class EmailChanged(val email: String) : Event()
        object SendResetLink : Event()
        object BackToLogin : Event()
    }

    sealed class Effect {
        object NavigateBack : Effect()
        data class ShowError(val message: UiText) : Effect()
        data class ShowSuccess(val message: UiText) : Effect()
    }
}