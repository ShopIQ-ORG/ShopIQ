package com.iti.presentation.util

import java.text.NumberFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Currency
import java.util.Locale
import com.iti.domain.models.order.Money

fun Money.toCurrency(): String = amount.toCurrency(currencyCode)


fun Double.toCurrency(currencyCode: String): String = try {
    NumberFormat.getCurrencyInstance(Locale.US).apply {
        currency = Currency.getInstance(currencyCode)
    }.format(this)
} catch (e: Exception) {
    "$this $currencyCode"
}

fun String.toDisplayDate(): String = try {
    val dt = OffsetDateTime.parse(this, DateTimeFormatter.ISO_DATE_TIME)
    dt.format(DateTimeFormatter.ofPattern("MMM d, yyyy • hh:mm a", Locale.US))
} catch (e: Exception) {
    this
}