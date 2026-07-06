//
//  CurrencyManager.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.presentation.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class Currency(
    val code: String,
    val name: String,
    val symbol: String,
    val rateToUsd: Double // 1 USD = X Currency units
)

object CurrencyManager {
    val supportedCurrencies = listOf(
        Currency("USD", "US Dollar", "$", 1.0),
        Currency("EGP", "Egyptian Pound", "EGP", 48.0),
        Currency("EUR", "Euro", "€", 0.92),
        Currency("GBP", "British Pound", "£", 0.78),
        Currency("AED", "UAE Dirham", "AED", 3.67),
        Currency("SAR", "Saudi Riyal", "SAR", 3.75)
    )

    private val _selectedCurrency = MutableStateFlow(supportedCurrencies[1]) // EGP by default
    val selectedCurrency: StateFlow<Currency> = _selectedCurrency.asStateFlow()

    fun selectCurrency(code: String) {
        supportedCurrencies.firstOrNull { it.code == code }?.let {
            _selectedCurrency.value = it
        }
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
