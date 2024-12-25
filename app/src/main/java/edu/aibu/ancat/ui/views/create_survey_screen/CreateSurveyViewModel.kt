package edu.aibu.ancat.ui.views.create_survey_screen

import android.content.Context
import androidx.lifecycle.ViewModel
import edu.aibu.ancat.core.helper.JsonHelper
import edu.aibu.ancat.data.model.SurveyItem
import edu.aibu.ancat.data.model.mergeSurveyItemsByType
import edu.aibu.ancat.data.repository.JsonFilesRepository
import edu.aibu.ancat.domain.entity.JsonFilesInfoEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.aibu.ancat.core.helper.DocumentHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

sealed class DialogType {
    data object DescriptionType : DialogType()
    data object MultipleChoiceType : DialogType()
    data object RatingType : DialogType()
}

@HiltViewModel
class CreateSurveyViewModel @Inject constructor(
    private val jsonFilesRepository: JsonFilesRepository,
    private val documentHelper: DocumentHelper,
    private val jsonHelper: JsonHelper
) : ViewModel() {

    private val _dialogType = MutableStateFlow<DialogType?>(null)
    val dialogType: StateFlow<DialogType?> = _dialogType

    fun showDialog(dialogType: DialogType) {
        _dialogType.value = dialogType
    }

    fun hideDialog() {
        _dialogType.value = null
    }

    private val _surveyItems = MutableStateFlow<List<SurveyItem>>(emptyList())
    val surveyItems: StateFlow<List<SurveyItem>> get() = _surveyItems

    suspend fun createSurvey(context: Context) {
        documentHelper.createDocument(surveyItems.value)
    }

    fun addSurveyItem(surveyItem: SurveyItem) {
        _surveyItems.value += surveyItem
        _surveyItems.value = mergeSurveyItemsByType(_surveyItems)

    }

    fun saveJson(context: Context, jsonFilesInfoEntity: JsonFilesInfoEntity, surveyItem: List<SurveyItem>) {
        jsonHelper.openFileAndWriteNewContent(
            fileName = jsonFilesInfoEntity.fileName,
            newContent = Json.encodeToString(surveyItem),
            context = context
        )
    }

    suspend fun getJsonFilesInfoById(id: Int): JsonFilesInfoEntity {
        return jsonFilesRepository.findById(id)
    }

    fun loadSurveyItems(context: Context, jsonFilesInfo: JsonFilesInfoEntity) {
        val jsonString = jsonHelper.readJsonFile(
            context = context,
            fileName = jsonFilesInfo.fileName
        )
        val items = Json.decodeFromString<List<SurveyItem>>(jsonString)
        _surveyItems.value = items
    }
}