package com.iti.data.sources.local.currency

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.iti.domain.models.Currency
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CurrencyLocalDataSourceImpl(
    private val dataStore: DataStore<Preferences>
) : CurrencyLocalDataSource {

    private val SELECTED_CURRENCY_CODE = stringPreferencesKey("selected_currency_code")
    private val SELECTED_CURRENCY_NAME = stringPreferencesKey("selected_currency_name")
    private val SELECTED_CURRENCY_SYMBOL = stringPreferencesKey("selected_currency_symbol")
    private val SELECTED_CURRENCY_RATE = stringPreferencesKey("selected_currency_rate")

    override fun getSelectedCurrency(): Flow<Currency?> {
        return dataStore.data.map { preferences ->
            val code = preferences[SELECTED_CURRENCY_CODE] ?: return@map null
            val name = preferences[SELECTED_CURRENCY_NAME] ?: return@map null
            val symbol = preferences[SELECTED_CURRENCY_SYMBOL] ?: return@map null
            val rateStr = preferences[SELECTED_CURRENCY_RATE] ?: return@map null
            val rate = rateStr.toDoubleOrNull() ?: 1.0
            Currency(code, name, symbol, rate)
        }
    }

    override suspend fun saveSelectedCurrency(currency: Currency) {
        dataStore.edit { preferences ->
            preferences[SELECTED_CURRENCY_CODE] = currency.code
            preferences[SELECTED_CURRENCY_NAME] = currency.name
            preferences[SELECTED_CURRENCY_SYMBOL] = currency.symbol
            preferences[SELECTED_CURRENCY_RATE] = currency.rateToUsd.toString()
        }
    }
}
