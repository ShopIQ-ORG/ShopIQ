//
//  SearchLocationByNameUseCase.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.domain.usecases.address

import com.iti.domain.models.LocationCoordinates
import com.iti.domain.models.Result
import com.iti.domain.repositories.user.UserRepository

class SearchLocationByNameUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(query: String, apiKey: String): Result<LocationCoordinates?> {
        return repository.searchLocationByName(query, apiKey)
    }
}
