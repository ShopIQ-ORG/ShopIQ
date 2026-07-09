package com.iti.presentation.util

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.iti.domain.models.Currency
import com.iti.domain.models.order.Money
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

fun Double.toLocalizedCurrency(currencyCode: String, context: Context): String {
    val localizedCode = when (currencyCode.uppercase()) {
        "USD" -> context.getString(R.string.currency_usd)
        "EGP" -> context.getString(R.string.currency_egp)
        "EUR" -> context.getString(R.string.currency_eur)
        "GBP" -> context.getString(R.string.currency_gbp)
        "AED" -> context.getString(R.string.currency_aed)
        "SAR" -> context.getString(R.string.currency_sar)
        else -> currencyCode
    }
    return "${this.formatAsDecimal()} $localizedCode"
}

private fun Double.formatAsDecimal(): String {
    return if (this == this.toLong().toDouble()) {
        this.toLong().toString()
    } else {
        String.format("%.2f", this)
    }
}

@Composable
fun Money.localizedCurrency(): String {
    return CurrencyManager.convertFromUsdLocalized(
        amountUsd = amount,
        context = LocalContext.current
    )
}
