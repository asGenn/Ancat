package edu.aibu.ancat.ui.views.analysis_screen

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.aibu.ancat.core.helper.JsonHelper
import edu.aibu.ancat.data.repository.JsonFilesRepository
import javax.inject.Inject

@HiltViewModel
class AnalysisScreenViewModel @Inject constructor(
    private val jsonFilesRepository: JsonFilesRepository,
    private val jsonHelper: JsonHelper
): ViewModel() {

    suspend fun getAnalysisFiles(context: Context): List<Pair<String, String>> {
        val getJsonFiles = jsonFilesRepository.getJsonFiles()
        val data = getJsonFiles.mapNotNull {
            val resultFileName = "${it.fileName}_result"
            val exists = jsonHelper.checkFileExists(resultFileName, context)
            if (exists) {
                it.title to resultFileName
            } else {
                null
            }
        }
        return data
    }

    fun removeAnalysisFile(filePath: String, context: Context) {
        jsonHelper.removeJsonFile(filePath, context)

    }

}