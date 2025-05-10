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
import edu.aibu.ancat.data.model.Question
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

    suspend fun createSurvey(context: Context, jsonFilesInfoEntity: JsonFilesInfoEntity) {
        documentHelper.createDocument(context, surveyItems.value, jsonFilesInfoEntity.fileName)
    }

    fun addSurveyItem(surveyItem: SurveyItem) {
        _surveyItems.value += surveyItem
        _surveyItems.value = mergeSurveyItemsByType(_surveyItems)
    }

    fun saveJson(
        context: Context,
        jsonFilesInfoEntity: JsonFilesInfoEntity,
        surveyItem: List<SurveyItem>
    ) {
        jsonHelper.openFileAndWriteNewContent(
            fileName = jsonFilesInfoEntity.fileName,
            newContent = Json.encodeToString(surveyItem),
            context = context
        )
    }

    fun editSurveyDescription(descriptionIndex: Int, newDescription: String) {
        val updatedSurveyList = _surveyItems.value.toMutableList()
        val currentSurveyItem = _surveyItems.value[0]
        val surveyTitle = currentSurveyItem.questions[0] as Question.SurveyTitle
        val descriptions = surveyTitle.description.toMutableList()
        if (newDescription.isNotBlank()) {
            descriptions[descriptionIndex] = newDescription
        } else {
            descriptions.removeAt(descriptionIndex)
        }
        val updatedSurveyTitle = surveyTitle.copy(description = descriptions)
        val updatedQuestions = currentSurveyItem.questions.toMutableList()
        updatedQuestions[0] = updatedSurveyTitle
        val updatedSurveyItem = currentSurveyItem.copy(questions = updatedQuestions)
        updatedSurveyList[0] = updatedSurveyItem
        _surveyItems.value = updatedSurveyList
    }



    fun editDescriptionsType(range: Int, descriptionIndex: Int, newQuestion: String) {
        val newList = _surveyItems.value.toMutableList()
        if (newQuestion.isNotBlank())
            (newList[range].questions[descriptionIndex] as Question.SurveyDescription).description = newQuestion
        else {
            val updatedQuestions = newList[range].questions.toMutableList()  // Soruları mutable hale getiriyoruz
            updatedQuestions.removeAt(descriptionIndex)  // Soruyu siliyoruz
            newList[range] = newList[range].copy(questions = updatedQuestions)

            if (newList[range].questions.isEmpty()) {
                newList.removeAt(range)
            }
        }
        _surveyItems.value = newList
    }

    fun editMultiChoiceTypeQuest(range: Int, questionIndex: Int, newQuestion: String) {
        val newList = _surveyItems.value.toMutableList()
        if (newQuestion.isNotBlank())
            (newList[range].questions[questionIndex] as Question.MultipleChoiceQuestion).question = newQuestion
        else {
            val updatedQuestions = newList[range].questions.toMutableList()  // Soruları mutable hale getiriyoruz
            updatedQuestions.removeAt(questionIndex)  // Soruyu siliyoruz
            newList[range] = newList[range].copy(questions = updatedQuestions)

            if (newList[range].questions.isEmpty()) {
                newList.removeAt(range)
            }
        }
        _surveyItems.value = newList
    }

    fun editMultiChoiceTypeMarks(
        range: Int,
        questionIndex: Int,
        optionIndex: Int,
        newQuestion: String
    ) {
        val newList = _surveyItems.value.toMutableList()
        val optionList = (newList[range].questions[questionIndex] as Question.MultipleChoiceQuestion).options.toMutableList()
        if (newQuestion.isNotBlank()) {
            optionList[optionIndex] = newQuestion
            (newList[range].questions[questionIndex] as Question.MultipleChoiceQuestion).options = optionList
        } else {
            optionList.removeAt(optionIndex)
            (newList[range].questions[questionIndex] as Question.MultipleChoiceQuestion).options = optionList

            if (optionList.isEmpty()) {
                val updatedQuestions = newList[range].questions.toMutableList()  // Soruları mutable hale getiriyoruz
                updatedQuestions.removeAt(questionIndex)  // Soruyu siliyoruz
                newList[range] = newList[range].copy(questions = updatedQuestions)

                if (newList[range].questions.isEmpty()) {
                    newList.removeAt(range)
                }
            }
        }
        _surveyItems.value = newList
    }



    fun editRatingTypeQuest(range: Int, questionIndex: Int, newQuestion: String) {
        val newList = _surveyItems.value.toMutableList()
        if (newQuestion.isNotBlank())
            (newList[range].questions[questionIndex] as Question.RatingQuestion).question = newQuestion
        else {
            val updatedQuestions = newList[range].questions.toMutableList()  // Soruları mutable hale getiriyoruz
            updatedQuestions.removeAt(questionIndex)  // Soruyu siliyoruz
            newList[range] = newList[range].copy(questions = updatedQuestions)

            if (newList[range].questions.isEmpty()) {
                newList.removeAt(range)
            }
        }
        _surveyItems.value = newList
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