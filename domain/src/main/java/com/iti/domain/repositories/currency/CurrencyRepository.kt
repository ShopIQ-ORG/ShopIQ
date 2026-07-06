//
//  CurrencyRepository.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.domain.repositories.currency

import com.iti.domain.models.Currency
import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {
    fun getSelectedCurrency(): Flow<Currency>
    fun getPopularCurrencies(): Flow<List<Currency>>
    fun getExchangeRateHistory(currencyCode: String): Flow<List<Pair<String, Double>>>
    suspend fun fetchExchangeRates()
    suspend fun changeSelectedCurrency(code: String)
}
