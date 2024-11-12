package com.example.ancat.data.sources

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.ancat.domain.entity.JsonFilesInfoEntity



@Dao
interface JsonFilesDao {
    @Query("SELECT * FROM JsonFilesInfoEntity")
    suspend fun getAll(): List<JsonFilesInfoEntity>

    // get a specific row from the table
    @Query("SELECT * FROM JsonFilesInfoEntity WHERE id = :id")
    suspend fun findById(id: Int): JsonFilesInfoEntity

    @Insert
    suspend fun insertJson(jsonFilesInfoEntity: JsonFilesInfoEntity): Long

    // delete all data from the table
    @Query("DELETE FROM JsonFilesInfoEntity")
    suspend fun deleteAll()

    // delete a specific row from the table
    @Delete
    suspend fun deleteJson(jsonFilesInfoEntity: JsonFilesInfoEntity)

}