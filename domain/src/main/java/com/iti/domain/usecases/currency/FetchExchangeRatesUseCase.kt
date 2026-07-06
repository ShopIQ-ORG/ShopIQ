//
//  FetchExchangeRatesUseCase.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.domain.usecases.currency

import com.iti.domain.repositories.currency.CurrencyRepository

class FetchExchangeRatesUseCase(
    private val repository: CurrencyRepository
) {
    suspend operator fun invoke() = repository.fetchExchangeRates()
}
