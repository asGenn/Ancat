package com.example.ancat.ui.views.create_survey_screen

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.ancat.core.helper.JsonHelper
import com.example.ancat.core.helper.survey.SurveyHelper
import com.example.ancat.data.model.SurveyItem
import com.example.ancat.data.model.mergeSurveyItemsByType
import com.example.ancat.data.repository.JsonFilesRepository
import com.example.ancat.domain.entity.JsonFilesInfoEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    private val surveyHelper: SurveyHelper
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

    fun loadSurveyItems(context: Context, jsonFilesInfo: JsonFilesInfoEntity) {
        val jsonString = JsonHelper().readJsonFile(
            context = context,
            fileName = jsonFilesInfo.fileName
        )
        val items = Json.decodeFromString<List<SurveyItem>>(jsonString)
        _surveyItems.value = items
    }

    fun addSurveyItem(surveyItem: SurveyItem) {
        _surveyItems.value += surveyItem
        _surveyItems.value = mergeSurveyItemsByType(_surveyItems)
        surveyItems.value.forEach {
            Log.d("SurveyItem", it.toString())
        }

    }

    suspend fun getJsonFilesInfoById(id: Int): JsonFilesInfoEntity {
        return jsonFilesRepository.findById(id)
    }

    fun createSurvey(context: Context, jsonFilesInfoEntity: JsonFilesInfoEntity) {
        surveyHelper.createPdf(context = context, surveyItems.value)
    }
}