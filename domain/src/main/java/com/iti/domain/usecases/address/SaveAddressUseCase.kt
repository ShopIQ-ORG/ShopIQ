//
//  SaveAddressUseCase.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.domain.usecases.address

import com.iti.domain.models.Address
import com.iti.domain.models.Result
import com.iti.domain.repositories.user.UserRepository

class SaveAddressUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(address: Address): Result<Unit> {
        return repository.saveAddress(address)
    }
}
