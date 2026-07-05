package com.iti.data.repositories

import com.iti.data.mappers.toDomain
import com.iti.data.mappers.toEntity
import com.iti.data.sources.local.AddressDao
import com.iti.domain.models.Address
import com.iti.domain.models.Result
import com.iti.domain.repositories.address.AddressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class AddressRepositoryImpl(
    private val addressDao: AddressDao
) : AddressRepository {

    override fun getSavedAddresses(): Flow<Result<List<Address>>> {
        return addressDao.getAllAddresses()
            .map { entities ->
                Result.Success(entities.map { it.toDomain() }) as Result<List<Address>>
            }
            .catch { e ->
                emit(Result.Failure(Exception(e)))
            }
    }

    override suspend fun saveAddress(address: Address): Result<Unit> {
        return try {
            val count = addressDao.getAddressCount()
            val existing = addressDao.getAddressById(address.id)
            
            val shouldBeDefault = address.isDefault || count == 0 || (existing?.isDefault == true)
            val updatedAddress = address.copy(isDefault = shouldBeDefault)

            if (updatedAddress.isDefault) {
                addressDao.clearDefaultsExcept(updatedAddress.id)
            }

            addressDao.insertAddress(updatedAddress.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun deleteAddress(addressId: String): Result<Unit> {
        return try {
            val existing = addressDao.getAddressById(addressId)
            if (existing != null) {
                addressDao.deleteAddressById(addressId)
                if (existing.isDefault) {
                    val firstRemaining = addressDao.getFirstAddress()
                    if (firstRemaining != null) {
                        addressDao.insertAddress(firstRemaining.copy(isDefault = true))
                    }
                }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }
}
