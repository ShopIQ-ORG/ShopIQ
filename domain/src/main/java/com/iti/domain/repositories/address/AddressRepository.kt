//
//  AddressRepository.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.domain.repositories.address

import com.iti.domain.models.Address
import com.iti.domain.models.LocationCoordinates
import com.iti.domain.models.PlaceSuggestion
import com.iti.domain.models.Result
import kotlinx.coroutines.flow.Flow

interface AddressRepository {
    fun getSavedAddresses(): Flow<Result<List<Address>>>
    suspend fun saveAddress(address: Address): Result<Unit>
    suspend fun deleteAddress(addressId: String): Result<Unit>
    suspend fun getPlaceSuggestions(query: String, apiKey: String): Result<List<PlaceSuggestion>>
    suspend fun searchLocationByName(query: String, apiKey: String): Result<LocationCoordinates?>
}
