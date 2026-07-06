package com.iti.presentation.util

import android.content.Context
import com.iti.domain.models.Currency
import com.iti.presentation.R

fun Currency.getLocalizedCode(context: Context): String {
    return when (this.code.uppercase()) {
        "USD" -> context.getString(R.string.currency_usd)
        "EGP" -> context.getString(R.string.currency_egp)
        "EUR" -> context.getString(R.string.currency_eur)
        "GBP" -> context.getString(R.string.currency_gbp)
        "AED" -> context.getString(R.string.currency_aed)
        "SAR" -> context.getString(R.string.currency_sar)
        else -> this.code
    }
}
