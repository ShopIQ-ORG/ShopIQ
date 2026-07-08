package com.iti.data.sources.remote.favorites

interface FavoriteRemoteDataSource {
    suspend fun getFavoriteIds(userId: String): List<String>
    suspend fun addFavorite(userId: String, productId: String)
    suspend fun removeFavorite(userId: String, productId: String)
}