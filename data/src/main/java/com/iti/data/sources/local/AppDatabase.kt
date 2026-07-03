package com.iti.data.sources.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FavoriteEntity::class, AddressEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteDao(): FavoriteDao
    abstract fun addressDao(): AddressDao

    companion object {
        const val DATABASE_NAME = "shopiq_database"
    }
}