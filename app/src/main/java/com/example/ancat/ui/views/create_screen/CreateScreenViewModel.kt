package com.example.ancat.ui.views.create_screen

import androidx.lifecycle.ViewModel
import com.example.ancat.data.repository.JsonFilesRepository
import com.example.ancat.domain.entity.JsonFilesInfoEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class  CreateScreenViewModel @Inject constructor(
    private val jsonFilesRepository: JsonFilesRepository
) : ViewModel() {

    suspend fun saveJsonFile(fileName: String,filePath: String, title: String) {
        jsonFilesRepository.insertJsonFile(
            jsonFilesInfoEntity = JsonFilesInfoEntity(
                fileName = fileName,
                filePath = filePath,
                id = 0,
                title = title

            )
        )
    }
    suspend fun getJsonFiles() = jsonFilesRepository.getJsonFiles()
    suspend fun deleteAll() = jsonFilesRepository.deleteAll()
    suspend fun deleteJsonFile(jsonFilesInfoEntity: JsonFilesInfoEntity) = jsonFilesRepository.deleteJsonFile(jsonFilesInfoEntity)






}