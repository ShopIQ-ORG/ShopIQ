//
//  GetSelectedCurrencyUseCase.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.domain.usecases.currency

import com.iti.domain.models.Currency
import com.iti.domain.repositories.settings.SettingsRepository
import kotlinx.coroutines.flow.Flow

class GetSelectedCurrencyUseCase(
    private val repository: SettingsRepository
) {
    operator fun invoke(): Flow<Currency> = repository.getSelectedCurrency()
}
