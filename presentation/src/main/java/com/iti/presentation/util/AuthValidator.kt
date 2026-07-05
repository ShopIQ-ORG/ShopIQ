package com.iti.presentation.util

import com.iti.presentation.R

sealed interface ValidationResult {
    data object Success : ValidationResult
    data class Error(val message: UiText) : ValidationResult
}

object AuthField {
    const val FULL_NAME = "fullName"
    const val EMAIL = "email"
    const val PHONE = "phone"
    const val PASSWORD = "password"
    const val CONFIRM_PASSWORD = "confirmPassword"
    const val TERMS = "terms"
}

object AuthValidator {

    private val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]+$".toRegex()
    private val PHONE_EGYPT_REGEX = "^01[0125][0-9]{8}$".toRegex()
    private val PASSWORD_REGEX = "^.{6,}$".toRegex()
    private val FULL_NAME_REGEX = "^[a-zA-Z\\s]{3,}$".toRegex()

    fun validateEmail(email: String): ValidationResult {
        if (email.isBlank()) {
            return ValidationResult.Error(UiText.StringResource(R.string.error_email_required))
        }
        if (!EMAIL_REGEX.matches(email)) {
            return ValidationResult.Error(UiText.StringResource(R.string.error_invalid_email))
        }
        return ValidationResult.Success
    }

    fun validatePhone(phone: String): ValidationResult {
        if (phone.isBlank()) {
            return ValidationResult.Error(UiText.StringResource(R.string.error_phone_required))
        }
        if (!PHONE_EGYPT_REGEX.matches(phone)) {
            return ValidationResult.Error(UiText.StringResource(R.string.error_invalid_phone_egypt))
        }
        return ValidationResult.Success
    }

    fun validatePassword(password: String): ValidationResult {
        if (password.isBlank()) {
            return ValidationResult.Error(UiText.StringResource(R.string.error_password_required))
        }
        if (!PASSWORD_REGEX.matches(password)) {
            return ValidationResult.Error(UiText.StringResource(R.string.error_invalid_password))
        }
        return ValidationResult.Success
    }

    fun validateFullName(name: String): ValidationResult {
        if (name.isBlank()) {
            return ValidationResult.Error(UiText.StringResource(R.string.error_full_name_required))
        }
        if (!FULL_NAME_REGEX.matches(name.trim())) {
            return ValidationResult.Error(UiText.StringResource(R.string.error_invalid_full_name))
        }
        return ValidationResult.Success
    }

    fun validateSignIn(email: String, password: String): Map<String, UiText> {
        val errors = mutableMapOf<String, UiText>()
        (validateEmail(email) as? ValidationResult.Error)?.let { errors[AuthField.EMAIL] = it.message }
        (validatePassword(password) as? ValidationResult.Error)?.let { errors[AuthField.PASSWORD] = it.message }
        return errors
    }

    fun validateSignUp(
        fullName: String,
        email: String,
        phone: String,
        password: String,
        confirmPassword: String,
        agreeToTerms: Boolean
    ): Map<String, UiText> {
        val errors = mutableMapOf<String, UiText>()
        (validateFullName(fullName) as? ValidationResult.Error)?.let { errors[AuthField.FULL_NAME] = it.message }
        (validateEmail(email) as? ValidationResult.Error)?.let { errors[AuthField.EMAIL] = it.message }
        (validatePhone(phone) as? ValidationResult.Error)?.let { errors[AuthField.PHONE] = it.message }
        (validatePassword(password) as? ValidationResult.Error)?.let { errors[AuthField.PASSWORD] = it.message }
        if (errors[AuthField.PASSWORD] == null && password != confirmPassword) {
            errors[AuthField.CONFIRM_PASSWORD] = UiText.StringResource(R.string.error_passwords_do_not_match)
        }
        if (!agreeToTerms) {
            errors[AuthField.TERMS] = UiText.StringResource(R.string.error_agree_to_terms)
        }
        return errors
    }

    fun validateForgotPassword(email: String): Map<String, UiText> {
        val errors = mutableMapOf<String, UiText>()
        (validateEmail(email) as? ValidationResult.Error)?.let { errors[AuthField.EMAIL] = it.message }
        return errors
    }
}
