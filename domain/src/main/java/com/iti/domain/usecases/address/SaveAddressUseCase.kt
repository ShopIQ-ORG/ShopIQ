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
import com.iti.domain.repositories.address.AddressRepository

class SaveAddressUseCase(
    private val repository: AddressRepository
) {
    suspend operator fun invoke(address: Address): Result<Unit> {
        return repository.saveAddress(address)
    }
}
