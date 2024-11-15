package com.example.ancat.core.helper.survey

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.Page
import com.example.ancat.data.model.Question
import com.example.ancat.data.model.SurveyItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SurveyHelper @Inject constructor(
    private val documentHelper: DocumentHelper,
    private val questionsHelper: QuestionsHelper
) {

    private lateinit var pdfDocument: PdfDocument
    private lateinit var page: Page
    private lateinit var canvas: Canvas

    private var pageNum = 1

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

    private fun processTitleCommitFrame(title: String, commits: Question.SurveyTitle): Float {
        val cursorPosition =
            questionsHelper.surveyTitleCommit(canvas, paint, paintTitle, title, commits)
        return questionsHelper.surveyFrame(canvas, paint, cursorPosition)
    }


    private fun processDescriptions(
        title: String,
        commits: List<Question.SurveyDescription>
    ): Float {

        return questionsHelper.questionCommits(
            canvas, paint, paintTitle, title, commits, cursorPos
        )
    }


    private fun processRatingQuestions(title: String, questions: List<Question.RatingQuestion>): Float {
        handlePageBreakIfNeeded(questions.size)
        return questionsHelper.ratingQuestion(canvas, paint, title, questions, cursorPos)
    }

    private fun processMultipleChoiceQuestions(title: String, questions: List<Question.MultipleChoiceQuestion>): Float {
        var optSize = 0
        questions.forEach {
            optSize += it.options.size
        }
        handlePageBreakIfNeeded(optSize)
        return questionsHelper.multipleChoiceQuestion(canvas, paint, title, questions, cursorPos)
    }

    fun createPdf(context: Context, surveyItem: List<SurveyItem>) {

        pdfDocument = PdfDocument()
        page = documentHelper.createPage(pdfDocument, pageNum)
        canvas = page.canvas

        for (data in surveyItem) {
            val type = data.type
            val title = data.title
            val questions = data.questions

            cursorPos = when (type) {

                "_" -> processTitleCommitFrame(title, questions[0] as Question.SurveyTitle)
                "0" -> processDescriptions(title, questions as List<Question.SurveyDescription>)
                "1" -> processRatingQuestions(title, questions as List<Question.RatingQuestion>)
                "2" -> processMultipleChoiceQuestions(title, questions  as List<Question.MultipleChoiceQuestion>)
                else -> cursorPos
            }
        }

        pdfDocument.finishPage(page)
        documentHelper.savePdf(context, pdfDocument, "anket")
    }

}
