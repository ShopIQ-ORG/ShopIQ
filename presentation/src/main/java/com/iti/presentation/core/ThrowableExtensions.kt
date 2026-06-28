package com.iti.presentation.core

import com.iti.domain.exceptions.AuthException
import com.iti.domain.exceptions.NetworkException
import com.iti.presentation.R

fun Throwable.toUiMessage(): UiText = when (this) {
    is AuthException.InvalidCredentials -> UiText.StringResource(R.string.error_invalid_credentials)
    is AuthException.UserNotFound       -> UiText.StringResource(R.string.error_user_not_found)
    is AuthException.EmailAlreadyInUse  -> UiText.StringResource(R.string.error_email_already_in_use)
    is AuthException.WeakPassword       -> UiText.StringResource(R.string.error_weak_password)
    is NetworkException.NoConnection    -> UiText.StringResource(R.string.error_network)
    else                                -> UiText.StringResource(R.string.error_unknown)
}