package com.iti.data.sources.local

import androidx.room.Entity
import androidx.room.PrimaryKey

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