package com.iti.presentation.core

import android.content.Context
import androidx.annotation.StringRes
import com.iti.presentation.R

sealed class UiText {
    data class StringResource(
        @param:StringRes val resId: Int,
        val args: List<Any> = emptyList()
    ) : UiText()

    data class Plain(val value: String) : UiText()

    fun resolve(context: Context): String = when (this) {
        is StringResource -> context.getString(resId, *args.toTypedArray())
        is Plain          -> value
    }

    fun isFieldError(): Boolean = when (this) {
        is StringResource -> resId in fieldErrorResIds
        is Plain          -> false
    }
}

private val fieldErrorResIds = setOf(
    R.string.error_passwords_do_not_match,
    R.string.error_agree_to_terms,
    R.string.error_full_name_required,
    R.string.error_email_required,
    R.string.error_email_or_phone_required,
    R.string.error_phone_required,
    R.string.error_password_required,
    R.string.error_confirm_password_required,
)