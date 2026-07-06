package com.iti.data.sources.local.currency

import com.iti.domain.models.Currency
import kotlinx.coroutines.flow.Flow

interface CurrencyLocalDataSource {
    fun getSelectedCurrency(): Flow<Currency?>
    suspend fun saveSelectedCurrency(currency: Currency)
}
