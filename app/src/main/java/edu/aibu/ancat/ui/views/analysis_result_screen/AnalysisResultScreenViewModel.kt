package edu.aibu.ancat.ui.views.analysis_result_screen

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.aibu.ancat.core.helper.JsonHelper
import edu.aibu.ancat.data.model.SurveyAnalysisResult
import edu.aibu.ancat.data.model.SurveyItem
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class AnalysisResultScreenViewModel @Inject constructor(
    private val jsonHelper: JsonHelper
) : ViewModel() {

    fun getJsonResultData(path: String, context: Context): List<SurveyAnalysisResult> {
        val data = jsonHelper.readJsonFile(path, context)
        return Json.decodeFromString<List<SurveyAnalysisResult>>(data)
    }

    fun getJsonQuestionData(path: String, context: Context): List<SurveyItem> {
        val questPath = path.removeSuffix("_result")
        val data = jsonHelper.readJsonFile(questPath, context)
        return Json.decodeFromString<List<SurveyItem>>(data)
    }
}