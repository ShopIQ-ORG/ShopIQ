//
//  SelectCurrencyUseCase.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.domain.usecases.currency

import com.iti.domain.repositories.settings.SettingsRepository

class SelectCurrencyUseCase(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(code: String) = repository.changeSelectedCurrency(code)
}
