package com.iti.domain.repositories.settings

import com.iti.domain.models.Currency
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSelectedCurrency(): Flow<Currency>
    fun getPopularCurrencies(): Flow<List<Currency>>
    fun getExchangeRateHistory(currencyCode: String): Flow<List<Pair<String, Double>>>
    suspend fun fetchExchangeRates()
    suspend fun changeSelectedCurrency(code: String)
    
    fun isOnboardingCompleted(): Flow<Boolean>
    suspend fun setOnboardingCompleted()
}
