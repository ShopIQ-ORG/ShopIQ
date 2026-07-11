package com.iti.presentation.util

import android.content.Context
import com.iti.domain.models.Currency
import kotlin.math.roundToLong
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object CurrencyManager {
    private val _selectedCurrency = MutableStateFlow(Currency("EGP", "Egyptian Pound", "EGP", 48.0))
    val selectedCurrency: StateFlow<Currency> = _selectedCurrency.asStateFlow()

    private val _supportedCurrencies = MutableStateFlow(listOf(
        Currency("USD", "US Dollar", "$", 1.0),
        Currency("EGP", "Egyptian Pound", "EGP", 48.0),
        Currency("EUR", "Euro", "€", 0.92),
        Currency("GBP", "British Pound", "£", 0.78),
        Currency("AED", "UAE Dirham", "AED", 3.67),
        Currency("SAR", "Saudi Riyal", "SAR", 3.75)
    ))
    val supportedCurrencies: StateFlow<List<Currency>> = _supportedCurrencies.asStateFlow()

    fun updateSelectedCurrency(currency: Currency) {
        _selectedCurrency.value = currency
    }

    fun updateSupportedCurrencies(currencies: List<Currency>) {
        _supportedCurrencies.value = currencies
    }

    fun convertFromUsd(amountUsd: Double): Double {
        return amountUsd * _selectedCurrency.value.rateToUsd
    }

    fun convert(amount: Double, fromCurrency: Currency, toCurrency: Currency): Double {
        val amountInUsd = amount / fromCurrency.rateToUsd
        return amountInUsd * toCurrency.rateToUsd
    }

    fun convertFromUsdLocalized(amountUsd: Double, context: Context): String {
        val converted = convertFromUsd(amountUsd)
        return converted.toLocalizedCurrency(_selectedCurrency.value.code, context)
    }

    fun convertLocalized(
        amount: Double,
        fromCurrency: Currency,
        toCurrency: Currency,
        context: Context
    ): String {
        val converted = convert(amount, fromCurrency, toCurrency)
        return converted.toLocalizedCurrency(toCurrency.code, context)
    }

    fun convertCentsToEgp(amountCents: Long): Long {
        val selectedCurrency = _selectedCurrency.value

        if (selectedCurrency.code == "EGP") {
            return amountCents
        }

        val egpCurrency = _supportedCurrencies.value.firstOrNull { it.code == "EGP" }
            ?: Currency("EGP", "Egyptian Pound", "EGP", 48.0)

        val amount = amountCents / 100.0

        val converted = convert(
            amount = amount,
            fromCurrency = selectedCurrency,
            toCurrency = egpCurrency
        )

        return (converted * 100).roundToLong()
    }
}