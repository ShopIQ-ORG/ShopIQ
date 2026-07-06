//
//  CurrencyRepositoryImpl.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.iti.domain.models.Currency
import com.iti.domain.repositories.currency.CurrencyRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CurrencyRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
    private val client: HttpClient
) : CurrencyRepository {

    private object PreferencesKeys {
        val SELECTED_CURRENCY_CODE = stringPreferencesKey("selected_currency_code")
    }

    private val fallbackCurrencies = listOf(
        Currency("USD", "US Dollar", "$", 1.0),
        Currency("EGP", "Egyptian Pound", "EGP", 48.0),
        Currency("EUR", "Euro", "€", 0.92),
        Currency("GBP", "British Pound", "£", 0.79),
        Currency("INR", "Indian Rupee", "₹", 83.12),
        Currency("AED", "UAE Dirham", "د.إ", 3.67),
        Currency("SAR", "Saudi Riyal", "SAR", 3.75)
    )

    private val _currenciesState = MutableStateFlow(fallbackCurrencies)

    override fun getSelectedCurrency(): Flow<Currency> {
        return dataStore.data.map { preferences ->
            val code = preferences[PreferencesKeys.SELECTED_CURRENCY_CODE] ?: "EGP"
            getCurrencyByCode(code)
        }
    }

    override fun getPopularCurrencies(): Flow<List<Currency>> {
        return _currenciesState.map { list ->
            val popularCodes = listOf("USD", "EUR", "GBP", "INR", "AED", "SAR", "EGP")
            list.filter { it.code in popularCodes }
        }
    }

    override fun getExchangeRateHistory(currencyCode: String): Flow<List<Pair<String, Double>>> = flow {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val calendar = Calendar.getInstance()
        val endDate = sdf.format(calendar.time)
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val startDate = sdf.format(calendar.time)

        try {
            val url = "https://api.frankfurter.app/$startDate..$endDate?from=USD&to=$currencyCode"
            val response = client.get(url)
            val body = response.bodyAsText()
            val jsonObject = Gson().fromJson(body, JsonObject::class.java)
            val ratesObject = jsonObject.getAsJsonObject("rates")
            val history = mutableListOf<Pair<String, Double>>()

            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val outputFormat = SimpleDateFormat("EEE", Locale.US)

            for (dateKey in ratesObject.keySet()) {
                val rateVal = ratesObject.getAsJsonObject(dateKey).get(currencyCode).asDouble
                val date = inputFormat.parse(dateKey)
                val dayLabel = if (date != null) outputFormat.format(date) else dateKey
                history.add(dayLabel to rateVal)
            }
            emit(history)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun fetchExchangeRates() {
        try {
            val currenciesResponse = client.get("https://api.frankfurter.app/currencies")
            val currenciesJson = Gson().fromJson(currenciesResponse.bodyAsText(), JsonObject::class.java)

            val ratesResponse = client.get("https://api.frankfurter.app/latest?from=USD")
            val ratesJson = Gson().fromJson(ratesResponse.bodyAsText(), JsonObject::class.java)
            val ratesObject = ratesJson.getAsJsonObject("rates")

            val dynamicList = mutableListOf<Currency>()
            dynamicList.add(Currency("USD", "US Dollar", "$", 1.0))

            for (code in currenciesJson.keySet()) {
                if (code == "USD") continue
                val name = currenciesJson.get(code).asString
                val rate = if (ratesObject.has(code)) ratesObject.get(code).asDouble else continue
                val symbol = getSymbol(code)
                dynamicList.add(Currency(code, name, symbol, rate))
            }

            if (dynamicList.size > 1) {
                _currenciesState.value = dynamicList
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun changeSelectedCurrency(code: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_CURRENCY_CODE] = code
        }
    }

    private fun getCurrencyByCode(code: String): Currency {
        val list = _currenciesState.value
        return list.firstOrNull { it.code == code }
            ?: fallbackCurrencies.firstOrNull { it.code == code }
            ?: fallbackCurrencies[0]
    }

    private fun getSymbol(code: String): String {
        return try {
            java.util.Currency.getInstance(code).getSymbol(Locale.US)
        } catch (e: Exception) {
            code
        }
    }
}
