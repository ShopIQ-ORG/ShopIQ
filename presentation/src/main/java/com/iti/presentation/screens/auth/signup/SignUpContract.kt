package com.iti.presentation.screens.auth.signup

import com.iti.presentation.util.UiText

object SignUpContract {

    data class State(
        val fullName: String = "",
        val email: String = "",
        val phone: String = "",
        val password: String = "",
        val confirmPassword: String = "",
        val agreeToTerms: Boolean = false,
        val isLoading: Boolean = false,
        val fieldErrors: Map<String, UiText> = emptyMap()
    )

    sealed class Event {
        data class FullNameChanged(val fullName: String) : Event()
        data class EmailChanged(val email: String) : Event()
        data class PhoneChanged(val phone: String) : Event()
        data class PasswordChanged(val password: String) : Event()
        data class ConfirmPasswordChanged(val confirmPassword: String) : Event()
        data class AgreeToTermsChanged(val checked: Boolean) : Event()

        object Register : Event()
        object NavigateToTerms : Event()
        object NavigateToPrivacyPolicy : Event()
    }

    sealed class Effect {
        object NavigateToHome : Effect()
        object NavigateToTerms : Effect()
        class NavigateToEmailVerification(val email: String): Effect()
        object NavigateToPrivacyPolicy : Effect()
        data class ShowError(val message: UiText) : Effect()
    }
}