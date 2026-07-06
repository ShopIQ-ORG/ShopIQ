//
//  GetExchangeRateHistoryUseCase.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.domain.usecases.currency

import com.iti.domain.repositories.currency.CurrencyRepository
import kotlinx.coroutines.flow.Flow

class GetExchangeRateHistoryUseCase(
    private val repository: CurrencyRepository
) {
    operator fun invoke(currencyCode: String): Flow<List<Pair<String, Double>>> =
        repository.getExchangeRateHistory(currencyCode)
}
