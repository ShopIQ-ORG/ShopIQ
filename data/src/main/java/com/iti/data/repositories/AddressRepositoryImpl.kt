//
//  AddressRepositoryImpl.kt
//  ShopIQ
//
//  Created by Abdullh Gaber on 7/2/26.
//  Copyright © 2026 ITI. All rights reserved.
//

package com.iti.data.repositories

import com.iti.domain.models.Address
import com.iti.domain.models.Result
import com.iti.domain.repositories.address.AddressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class AddressRepositoryImpl : AddressRepository {

    private val _addresses = MutableStateFlow<List<Address>>(emptyList())

    override fun getSavedAddresses(): Flow<Result<List<Address>>> {
        return _addresses.map { Result.Success(it) }
    }

    override suspend fun saveAddress(address: Address): Result<Unit> {
        val currentList = _addresses.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == address.id }
        
        // Handle default status update
        val updatedAddress = if (address.isDefault || currentList.isEmpty()) {
            address.copy(isDefault = true)
        } else {
            address
        }

        if (index != -1) {
            currentList[index] = updatedAddress
        } else {
            currentList.add(updatedAddress)
        }

        // If this address is set as default, unset other defaults
        if (updatedAddress.isDefault) {
            for (i in currentList.indices) {
                if (currentList[i].id != updatedAddress.id) {
                    currentList[i] = currentList[i].copy(isDefault = false)
                }
            }
        }

        _addresses.value = currentList
        return Result.Success(Unit)
    }

    override suspend fun deleteAddress(addressId: String): Result<Unit> {
        val currentList = _addresses.value.toMutableList()
        val indexToDelete = currentList.indexOfFirst { it.id == addressId }
        if (indexToDelete != -1) {
            val wasDefault = currentList[indexToDelete].isDefault
            currentList.removeAt(indexToDelete)
            if (wasDefault && currentList.isNotEmpty()) {
                currentList[0] = currentList[0].copy(isDefault = true)
            }
            _addresses.value = currentList
        }
        return Result.Success(Unit)
    }
}
