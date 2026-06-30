package com.iti.presentation.core

import com.iti.domain.exceptions.AppException
import com.iti.domain.exceptions.AuthException
import com.iti.domain.exceptions.CartException
import com.iti.domain.exceptions.NetworkException
import com.iti.presentation.R
import com.iti.presentation.core.UiText.*

fun Throwable.toUiMessage(): UiText = when (this) {
    is AppException -> when (this) {
        is AuthException.InvalidCredentials -> StringResource(R.string.error_invalid_credentials)
        is AuthException.UserNotFound -> StringResource(R.string.error_user_not_found)
        is AuthException.EmailAlreadyInUse -> StringResource(R.string.error_email_already_in_use)
        is AuthException.WeakPassword -> StringResource(R.string.error_weak_password)
        is NetworkException.NoConnection -> StringResource(R.string.error_network)

        is AppException.Unknown -> StringResource(R.string.error_unknown)
        is CartException.CartNotFound -> StringResource(R.string.cart_load_error)
        is CartException.InvalidDiscountCode -> StringResource(R.string.cart_promo_invalid_error)
        is CartException.InvalidQuantity -> StringResource(R.string.cart_quantity_update_error)
        is CartException.UserErrors -> StringResource(R.string.something_went_wrong)
        is NetworkException.ServerError -> StringResource(R.string.something_went_wrong)
    }

    else -> StringResource(R.string.error_unknown)
}