package com.iti.presentation.screens.auth.signup

import com.iti.presentation.core.UiText

data class SignUpState(
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val agreeToTerms: Boolean = false,
    val isLoading: Boolean = false,
    val error: UiText? = null
)

sealed class SignUpIntent {
    data class FullNameChanged(val fullName: String) : SignUpIntent()
    data class EmailChanged(val email: String) : SignUpIntent()
    data class PhoneChanged(val phone: String) : SignUpIntent()
    data class PasswordChanged(val password: String) : SignUpIntent()
    data class ConfirmPasswordChanged(val confirmPassword: String) : SignUpIntent()
    data class AgreeToTermsChanged(val checked: Boolean) : SignUpIntent()
    object Register : SignUpIntent()
    object NavigateToTerms : SignUpIntent()
    object NavigateToPrivacyPolicy : SignUpIntent()
}

sealed class SignUpEffect {
    object NavigateToHome : SignUpEffect()
    object NavigateToTerms : SignUpEffect()
    object NavigateToPrivacyPolicy : SignUpEffect()
    data class ShowError(val message: UiText) : SignUpEffect()
}