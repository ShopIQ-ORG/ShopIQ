//
//  GetSavedAddressesUseCase.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.domain.usecases.address

import com.iti.domain.models.Address
import com.iti.domain.models.Result
import com.iti.domain.repositories.user.UserRepository
import kotlinx.coroutines.flow.Flow

class GetSavedAddressesUseCase(
    private val repository: UserRepository
) {
    operator fun invoke(): Flow<Result<List<Address>>> {
        return repository.getSavedAddresses()
    }
}
