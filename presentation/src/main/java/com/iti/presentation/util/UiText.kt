package com.iti.presentation.util

import android.content.Context
import androidx.annotation.StringRes

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
}