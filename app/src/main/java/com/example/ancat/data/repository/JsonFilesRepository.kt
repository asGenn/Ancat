package com.example.ancat.data.repository

import com.example.ancat.data.sources.JsonFilesDao
import com.example.ancat.domain.entity.JsonFilesInfoEntity
import javax.inject.Inject


class JsonFilesRepository @Inject constructor(private val jsonFilesDao: JsonFilesDao) {

    suspend fun getJsonFiles() = jsonFilesDao.getAll()

    suspend fun insertJsonFile(jsonFilesInfoEntity: JsonFilesInfoEntity) = jsonFilesDao.insertJson(
        jsonFilesInfoEntity = jsonFilesInfoEntity
    )

    suspend fun deleteAll() = jsonFilesDao.deleteAll()

    suspend fun deleteJsonFile(jsonFilesInfoEntity: JsonFilesInfoEntity) =
        jsonFilesDao.deleteJson(jsonFilesInfoEntity = jsonFilesInfoEntity)
}