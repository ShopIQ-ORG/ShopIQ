package com.iti.data.sources.remote.currency

import com.iti.domain.models.Currency

interface CurrencyRemoteDataSource {
    suspend fun fetchExchangeRates(baseCurrency: String = "USD"): List<Currency>
    suspend fun getExchangeRateHistory(
        currencyCode: String,
        startDate: String,
        endDate: String,
        baseCurrency: String = "USD"
    ): List<Pair<String, Double>>
}
