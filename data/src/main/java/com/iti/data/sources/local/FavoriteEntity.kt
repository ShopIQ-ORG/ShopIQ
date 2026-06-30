package com.iti.data.sources.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val price: String,
    val imageUrl: String
)