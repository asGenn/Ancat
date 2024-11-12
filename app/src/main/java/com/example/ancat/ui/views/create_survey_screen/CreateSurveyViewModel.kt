package com.example.ancat.ui.views.create_survey_screen

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.ancat.core.helper.survey.SurveyHelper
import com.example.ancat.data.repository.JsonFilesRepository
import com.example.ancat.domain.entity.JsonFilesInfoEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

sealed class DialogType {
    data object SurveyTitle : DialogType()
    data object DescriptionType : DialogType()
    data object MultipleChoiceType : DialogType()
    data object RatingType : DialogType()

}

@HiltViewModel
class CreateSurveyViewModel @Inject constructor(private val jsonFilesRepository: JsonFilesRepository,
    private val surveyHelper: SurveyHelper
) :
    ViewModel() {
    private val _dialogType = MutableStateFlow<DialogType?>(null)
    val dialogType: StateFlow<DialogType?> = _dialogType
    fun showDialog(dialogType: DialogType) {
        _dialogType.value = dialogType
    }

    fun hideDialog() {
        _dialogType.value = null
    }

    suspend fun getJsonFilesInfoById(id: Int): JsonFilesInfoEntity {
        return jsonFilesRepository.findById(id)
    }
    fun createSurvey(context: Context) {
        surveyHelper.createPdf(context = context)
    }
}