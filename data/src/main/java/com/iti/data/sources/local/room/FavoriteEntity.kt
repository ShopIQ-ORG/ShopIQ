package com.iti.data.sources.local.room

import androidx.room.Entity

@Entity(
    tableName = "favorites",
    primaryKeys = ["productId", "userId"]
)
data class FavoriteEntity(
    val productId: String,
    val userId: String,
    val title: String,
    val price: String,
    val imageUrl: String
)