//
//  GetPlaceSuggestionsUseCase.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.domain.usecases.address

import com.iti.domain.models.PlaceSuggestion
import com.iti.domain.models.Result
import com.iti.domain.repositories.address.AddressRepository

class GetPlaceSuggestionsUseCase(
    private val repository: AddressRepository
) {
    suspend operator fun invoke(query: String, apiKey: String): Result<List<PlaceSuggestion>> {
        return repository.getPlaceSuggestions(query, apiKey)
    }
}
