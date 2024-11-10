package com.example.ancat.data.sources

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.ancat.domain.entity.JsonFilesInfoEntity



@Dao
interface JsonFilesDao {
    @Query("SELECT * FROM JsonFilesInfoEntity")
    suspend fun getAll(): List<JsonFilesInfoEntity>

    @Insert
    suspend fun insertJson(jsonFilesInfoEntity: JsonFilesInfoEntity)


}