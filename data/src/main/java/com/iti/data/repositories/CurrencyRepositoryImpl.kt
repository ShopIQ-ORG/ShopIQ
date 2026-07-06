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
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.iti.domain.models.Currency
import com.iti.domain.repositories.currency.CurrencyRepository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

import com.iti.data.sources.remote.currency.CurrencyRemoteDataSource

class CurrencyRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
    private val remoteDataSource: CurrencyRemoteDataSource
) : CurrencyRepository {

    private object PreferencesKeys {
        val SELECTED_CURRENCY_CODE = stringPreferencesKey("selected_currency_code")
    }

    private val _currenciesState = MutableStateFlow<List<Currency>>(emptyList())

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
            val history = remoteDataSource.getExchangeRateHistory(currencyCode, startDate, endDate, "USD")
            emit(history)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun fetchExchangeRates() {
        try {
            val updatedList = remoteDataSource.fetchExchangeRates("USD")
            _currenciesState.value = updatedList
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
            ?: Currency("USD", "US Dollar", "$", 1.0)
    }

}
