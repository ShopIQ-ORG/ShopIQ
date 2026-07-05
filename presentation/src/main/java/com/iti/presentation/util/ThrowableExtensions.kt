package com.iti.presentation.util

import com.iti.domain.exceptions.AppException
import com.iti.domain.exceptions.AuthException
import com.iti.domain.exceptions.CartException
import com.iti.domain.exceptions.NetworkException
import com.iti.domain.exceptions.OrderException
import com.iti.presentation.R
import com.iti.presentation.util.UiText.*

fun Throwable.toUiMessage(): UiText = when (this) {
    is AppException -> when (this) {
        is AuthException.InvalidCredentials ->
            StringResource(R.string.error_invalid_credentials)

        is AuthException.UserNotFound ->
            StringResource(R.string.error_user_not_found)

        is AuthException.EmailAlreadyInUse ->
            StringResource(R.string.error_email_already_in_use)

        is AuthException.WeakPassword ->
            StringResource(R.string.error_weak_password)

        is NetworkException.NoConnection ->
            StringResource(R.string.error_network)

        is NetworkException.ServerError ->
            StringResource(R.string.something_went_wrong)

        is CartException.CartNotFound ->
            StringResource(R.string.cart_load_error)

        is CartException.InvalidDiscountCode ->
            StringResource(R.string.cart_promo_invalid_error)

        is CartException.InvalidQuantity ->
            StringResource(R.string.cart_quantity_update_error)

        is CartException.OperationRejected ->
            StringResource(R.string.cart_quantity_update_error)

        is CartException.GraphQLError ->
            StringResource(R.string.something_went_wrong)

        is CartException.UserErrors ->
            StringResource(R.string.something_went_wrong)

        is OrderException.GraphQLError ->
            StringResource(R.string.something_went_wrong)

        is OrderException.OrderNotFound ->
            StringResource(R.string.order_not_found_error)

        is OrderException.NoOrdersFound ->
            StringResource(R.string.no_orders_found)

        is OrderException.UnauthorizedAccess ->
            StringResource(R.string.login_required_title)

        is AppException.Unknown ->
            StringResource(R.string.error_unknown)

        is AuthException.UnauthorizedAccess -> StringResource(R.string.login_required_title)
    }

    else -> UiText.StringResource(R.string.error_unknown)
}