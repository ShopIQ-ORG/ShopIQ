package com.iti.data.sources.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(product: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE productId = :productId AND userId = :userId")
    suspend fun deleteFavorite(productId: String, userId: String)

    @Query("SELECT * FROM favorites WHERE userId = :userId")
    fun getAllFavorites(userId: String): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE productId = :productId AND userId = :userId)")
    suspend fun isFavorite(productId: String, userId: String): Boolean

    @Query("DELETE FROM favorites WHERE userId = :userId")
    suspend fun deleteFavoritesForUser(userId: String)
}