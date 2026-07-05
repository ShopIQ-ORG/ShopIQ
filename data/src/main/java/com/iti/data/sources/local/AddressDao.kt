package com.iti.data.sources.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AddressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: AddressEntity)

    @Query("DELETE FROM addresses WHERE id = :id")
    suspend fun deleteAddressById(id: String)

    @Query("SELECT * FROM addresses")
    fun getAllAddresses(): Flow<List<AddressEntity>>

    @Query("SELECT * FROM addresses WHERE id = :id")
    suspend fun getAddressById(id: String): AddressEntity?

    @Query("UPDATE addresses SET isDefault = 0")
    suspend fun clearAllDefaults()

    @Query("UPDATE addresses SET isDefault = 0 WHERE id != :excludeId")
    suspend fun clearDefaultsExcept(excludeId: String)

    @Query("SELECT COUNT(*) FROM addresses")
    suspend fun getAddressCount(): Int

    @Query("SELECT * FROM addresses LIMIT 1")
    suspend fun getFirstAddress(): AddressEntity?
}
