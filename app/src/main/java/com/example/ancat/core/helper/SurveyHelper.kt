package com.example.ancat.core.helper

import android.content.Context
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.Page
import android.graphics.pdf.PdfDocument.PageInfo.Builder
import android.os.Environment
import android.widget.Toast
import com.example.ancat.core.renderer.SurveyRenderer
import com.example.ancat.data.model.Question
import com.example.ancat.data.model.SurveyItem
import com.example.ancat.utils.Constants.CELL_HEIGHT
import com.example.ancat.utils.Constants.PAGE_HEIGHT
import com.example.ancat.utils.Constants.PAGE_WIDTH
import com.example.ancat.utils.Constants.QUESTION_HEIGHT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SurveyHelper @Inject constructor(
    private val surveyRenderer: SurveyRenderer
) {

    private lateinit var pdfDocument: PdfDocument
    private lateinit var page: Page
    private lateinit var canvas: Canvas

    private var pageNumber = 0
    private var cursorPosition = 0f

    companion object {
        val PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)!!
    }

    private fun callQuestSize(size: Int): Boolean =
        (size * CELL_HEIGHT + cursorPosition) > QUESTION_HEIGHT

    private suspend fun handlePageBreakIfNeeded(questionLength: Int) {
        if (callQuestSize(questionLength)) {
            page = createPage()
            canvas = page.canvas
            cursorPosition = surveyRenderer.processSurveyFrame(canvas, 30f)
        }

    }

    private suspend fun createPage(): Page {
        return withContext(Dispatchers.IO) {
            if (pageNumber != 1)
                pdfDocument.finishPage(page)

            val pageInfo = Builder(PAGE_WIDTH, PAGE_HEIGHT, ++pageNumber).create()
            pdfDocument.startPage(pageInfo)
        }
    }

    private suspend fun createDocument(context: Context, pdfDocument: PdfDocument, documentName: String) {
        pdfDocument.finishPage(page)
        val filePath = File(PATH, "$documentName.pdf")

        try {
            withContext(Dispatchers.IO) {
                FileOutputStream(filePath).use { outputStream ->
                    pdfDocument.writeTo(outputStream)
                }
            }
            Toast.makeText(context, "PDF oluşturuldu: ${filePath.absolutePath}", Toast.LENGTH_SHORT)
                .show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            pdfDocument.close()
        }
    }

    suspend fun createSurvey(context: Context, surveyItem: List<SurveyItem>) {

        pageNumber = 1
        pdfDocument = PdfDocument()
        page = createPage()
        canvas = page.canvas

        for (data in surveyItem) {
            val type = data.type
            val title = data.title
            val questions = data.questions

            @Suppress("UNCHECKED_CAST")
            cursorPosition = when (type) {
                "_" -> {
                    surveyRenderer.processTitleCommitFrame(
                        canvas = canvas,
                        title = title,
                        commits = questions[0] as Question.SurveyTitle
                    )
                }

                "0" -> {
                    questions as List<Question.SurveyDescription>
                    handlePageBreakIfNeeded(questions.size)
                    surveyRenderer.processDescriptions(
                        canvas = canvas,
                        commits = questions,
                        currentCursor = cursorPosition,
                    )
                }

                "1" -> {
                    questions as List<Question.RatingQuestion>
                    handlePageBreakIfNeeded(questions.size)
                    surveyRenderer.processRatingQuestions(
                        canvas = canvas,
                        questions = questions,
                        currentCursor = cursorPosition,
                    )
                }

                "2" -> {
                    questions as List<Question.MultipleChoiceQuestion>
                    handlePageBreakIfNeeded(questions.sumOf { it.options.size })
                    surveyRenderer.processMultipleChoiceQuestions(
                        canvas = canvas,
                        questions = questions,
                        currentCursor = cursorPosition
                    )
                }

                else -> cursorPosition
            }
        }

        createDocument(context, pdfDocument, surveyItem[0].title)
    }

}
