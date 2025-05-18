package edu.aibu.ancat.core.helper

import android.content.Context
import edu.aibu.ancat.data.model.Question
import edu.aibu.ancat.data.model.SurveyItem
import kotlinx.serialization.encodeToString
import java.io.File
import javax.inject.Singleton

@Singleton
class JsonHelper {

    // read json file from the app's internal storage
    // return the json file content as a string
    fun saveJsonToFile(jsonData: String, fileName: String, context: Context): String {
        val dir = File(context.filesDir, "JsonFiles")
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, "$fileName.json")
        file.writeText(jsonData)
        return file.absolutePath
    }

    // list all json files in the app's internal storage
    // return a list of json file names
//    fun listJsonFiles(context: Context): List<String> {
//        return context.filesDir.listFiles()?.filter { it.extension == "json" }?.map { it.name }
//            ?: emptyList()
//    }

    // remove json file from the app's internal storage
    // return true if the file is removed successfully
    fun removeJsonFile(fileName: String, context: Context): Boolean {
        try {
            val file = File(context.filesDir, "JsonFiles/$fileName.json")
            return file.delete()
        } catch (e: Exception) {
            println("Error: ${e.message}")
            return false
        }
    }

    // read json file from the app's internal storage
    // return the json file content as a string
    fun readJsonFile(fileName: String, context: Context): String {
        val file = File(context.filesDir, "JsonFiles/$fileName.json")
        return file.readText()
    }

    // open file from and write new content to it
    // before writing clear the file content
    fun openFileAndWriteNewContent(
        fileName: String,
        newContent: String,
        context: Context
    ): Boolean {
        try {
            val file = File(context.filesDir, "JsonFiles/$fileName.json")
            file.writeText(newContent)
            return true
        } catch (e: Exception) {
            println("Error: ${e.message}")
            return false
        }
    }
    fun checkFileExists(fileName: String, context: Context): Boolean {
        val file = File(context.filesDir, "JsonFiles/$fileName.json")
        return file.exists()
    }

    fun addMarksToRatingQuest(context: Context, data: Float, surveyIndex: Int, questionIndex: Int, fileName: String): Boolean {
        try {
            val jsonString = readJsonFile(fileName, context)
            val jsonFormat = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
            val surveyItems = jsonFormat.decodeFromString<List<SurveyItem>>(jsonString).toMutableList()
            val question = surveyItems[surveyIndex].questions[questionIndex]

            if (surveyIndex !in surveyItems.indices) return false
            if (questionIndex !in surveyItems[surveyIndex].questions.indices) return false
            if (question !is Question.RatingQuestion) return false

            question.mark = data //
            val updatedJsonString = jsonFormat.encodeToString(surveyItems)
            return openFileAndWriteNewContent(fileName, updatedJsonString, context)

        } catch (e: Exception) {
            println("Error: ${e.message}")
            return false
        }
    }

    fun addMarksToMultiChoiceQuest(context: Context, data: List<Float>, surveyIndex: Int, questionIndex: Int, fileName: String): Boolean {
        try {
            val jsonString = readJsonFile(fileName, context)
            val jsonFormat = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
            val surveyItems = jsonFormat.decodeFromString<List<SurveyItem>>(jsonString).toMutableList()
            val question = surveyItems[surveyIndex].questions[questionIndex]

            if (surveyIndex !in surveyItems.indices) return false
            if (questionIndex !in surveyItems[surveyIndex].questions.indices) return false
            if (question !is Question.MultipleChoiceQuestion) return false

            question.marks = data //
            val updatedJsonString = jsonFormat.encodeToString(surveyItems)
            return openFileAndWriteNewContent(fileName, updatedJsonString, context)

        } catch (e: Exception) {
            println("Error: ${e.message}")
            return false
        }
    }


}