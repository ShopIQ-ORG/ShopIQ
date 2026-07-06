package com.iti.presentation.util

import com.iti.domain.models.Currency
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

    // Convert from USD to selected currency
    fun convertFromUsd(amountUsd: Double): Double {
        return amountUsd * _selectedCurrency.value.rateToUsd
    }

    // Generic conversion helper
    fun convert(amount: Double, fromCurrency: Currency, toCurrency: Currency): Double {
        val amountInUsd = amount / fromCurrency.rateToUsd
        return amountInUsd * toCurrency.rateToUsd
    }
}
