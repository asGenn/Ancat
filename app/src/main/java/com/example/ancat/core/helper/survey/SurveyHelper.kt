package com.example.ancat.core.helper.survey

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.example.ancat.data.MultipleChoiceQuest
import com.example.ancat.data.jsonData
import com.example.ancat.domain.entity.JsonFilesInfoEntity
import org.json.JSONArray
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SurveyHelper @Inject constructor(
    private val documentHelper: DocumentHelper,
    private val questionsHelper: QuestionsHelper,
    private val pdfDocument: PdfDocument
) {
    private var pageNum = 1
    private var page = documentHelper.createPage(pdfDocument, pageNum)
    private var canvas: Canvas = page.canvas
    private var cursorPos = 30f

    private val paint = Paint().apply {
        textSize = 12f
        color = Color.BLACK
    }

    private val paintTitle = Paint().apply {
        textSize = 16f
        color = Color.BLACK
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    private fun callQuestSize(size: Int): Float = size * 20f

    private fun handlePageBreakIfNeeded(size: Int) {
        if (callQuestSize(size) + cursorPos > 792f) {
            pdfDocument.finishPage(page)
            page = documentHelper.createPage(pdfDocument, ++pageNum)
            canvas = page.canvas
            cursorPos = questionsHelper.surveyFrame(canvas, paint, 30f)
        }
    }

    private fun processTitleCommitFrame(title: String, commits: JSONArray): Float {
        val commitList = List<String>(commits.length()) {
            commits.getString(it)
        }
        val cursorPosition =
            questionsHelper.surveyTitleCommit(canvas, paint, paintTitle, title, commitList)
        return questionsHelper.surveyFrame(canvas, paint, cursorPosition)
    }

    private fun processCommitFrame(title: String, commits: JSONArray): Float {
        val commitList = List<String>(commits.length()) {
            commits.getString(it)
        }
        return questionsHelper.questionCommits(
            canvas,
            paint,
            paintTitle,
            title,
            commitList,
            cursorPos
        )
    }

    private fun processRatingQuestions(title: String, questions: JSONArray): Float {
        val questionList = List(questions.length()) { questions.getString(it) }
        handlePageBreakIfNeeded(questionList.size)
        return questionsHelper.ratingQuestion(canvas, paint, title, questionList, cursorPos)
    }

    private fun processMultipleChoiceQuestions(title: String, questions: JSONArray): Float {
        var optSize = 0
        val questionList = List(questions.length()) {
            val questionObj = questions.getJSONObject(it)
            val question = questionObj.getString("question")
            val options = List(questionObj.getJSONArray("options").length()) { index ->
                optSize++
                questionObj.getJSONArray("options").getString(index)
            }
            MultipleChoiceQuest(question, options)
        }
        handlePageBreakIfNeeded(optSize)
        return questionsHelper.multipleChoiceQuestion(canvas, paint, title, questionList, cursorPos)
    }

    fun createPdf(context: Context, jsonFilesInfoEntity: JsonFilesInfoEntity) {
        val jsonData = documentHelper.readJsonFromFilePath(jsonFilesInfoEntity.filePath)
        val jsonArray = JSONArray(jsonData)
        jsonFilesInfoEntity.filePath

        for (i in 0 until jsonArray.length()) {
            val questionObject = jsonArray.getJSONObject(i)
            val type = questionObject.getString("type")
            val title = questionObject.getString("title")
            val questions = questionObject.getJSONArray("questions")

            cursorPos = when (type) {
                // başlık ve açıklama
                "_" -> processTitleCommitFrame(title, questions)

                // açıklama
                "0" -> processCommitFrame(title, questions)

                // rating
                "1" -> processRatingQuestions(title, questions)

                // multiple choice
                "2" -> processMultipleChoiceQuestions(title, questions)
                else -> cursorPos
            }
        }

        pdfDocument.finishPage(page)
        documentHelper.savePdf(context, pdfDocument, "anket")
    }

}
