package edu.aibu.ancat.data.repository

import edu.aibu.ancat.data.sources.JsonFilesDao
import edu.aibu.ancat.domain.entity.JsonFilesInfoEntity
import javax.inject.Inject


class JsonFilesRepository @Inject constructor(private val jsonFilesDao: JsonFilesDao) {

    suspend fun getJsonFiles() = jsonFilesDao.getAll()
    suspend fun findById(id: Int) = jsonFilesDao.findById(id = id)
    suspend fun deleteAll() = jsonFilesDao.deleteAll()

    suspend fun insertJsonFile(jsonFilesInfoEntity: JsonFilesInfoEntity): Long =
        jsonFilesDao.insertJson(jsonFilesInfoEntity = jsonFilesInfoEntity)

    suspend fun deleteJsonFile(jsonFilesInfoEntity: JsonFilesInfoEntity) =
        jsonFilesDao.deleteJson(jsonFilesInfoEntity = jsonFilesInfoEntity)

}