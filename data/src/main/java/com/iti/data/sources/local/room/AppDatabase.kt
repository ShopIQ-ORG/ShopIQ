package com.iti.data.sources.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.iti.data.sources.local.AddressDao
import com.iti.data.sources.local.AddressEntity

@Database(entities = [FavoriteEntity::class, AddressEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteDao(): FavoriteDao
    abstract fun addressDao(): AddressDao

    companion object {
        const val DATABASE_NAME = "shopiq_database"
    }
}