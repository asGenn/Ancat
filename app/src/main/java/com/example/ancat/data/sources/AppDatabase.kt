package com.example.ancat.data.sources

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ancat.domain.entity.JsonFilesInfoEntity

@Database(
    entities = [JsonFilesInfoEntity::class], version = 1,
    autoMigrations = [

    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun jsonFilesDao(): JsonFilesDao
}