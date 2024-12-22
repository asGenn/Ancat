package edu.aibu.ancat.ui.views.create_screen

import androidx.lifecycle.ViewModel
import edu.aibu.ancat.data.repository.JsonFilesRepository
import edu.aibu.ancat.domain.entity.JsonFilesInfoEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class  CreateScreenViewModel @Inject constructor(
    private val jsonFilesRepository: JsonFilesRepository
) : ViewModel() {

    suspend fun saveJsonFileToDB(fileName: String,filePath: String, title: String):Long {
        return jsonFilesRepository.insertJsonFile(
            jsonFilesInfoEntity = JsonFilesInfoEntity(
                fileName = fileName,
                filePath = filePath,
                id = 0,
                title = title

            )
        )
    }
    suspend fun getJsonFiles() = jsonFilesRepository.getJsonFiles()
//    suspend fun deleteAll() = jsonFilesRepository.deleteAll()
    suspend fun deleteJsonFile(jsonFilesInfoEntity: JsonFilesInfoEntity) = jsonFilesRepository.deleteJsonFile(jsonFilesInfoEntity)






}