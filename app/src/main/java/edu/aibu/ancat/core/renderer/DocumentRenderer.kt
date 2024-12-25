package edu.aibu.ancat.core.renderer

import android.graphics.Canvas
import edu.aibu.ancat.core.renderer.survey_drawings.drawer.MultipleChoiceQuestions
import edu.aibu.ancat.core.renderer.survey_drawings.drawer.RatingQuestion
import edu.aibu.ancat.core.renderer.survey_drawings.drawer.TitleAndCommits
import edu.aibu.ancat.data.model.Question
import edu.aibu.ancat.data.model.SurveyItem
import javax.inject.Inject

class DocumentRenderer @Inject constructor(
    private val titleAndCommits: TitleAndCommits,
    private val ratingQuestion: RatingQuestion,
    private val multipleChoiceQuestions: MultipleChoiceQuestions
) {

    enum class Type {
        TITLE, DESC, RATING, MULTI, ILLEGAL
    }

    private fun questionTypeCheck(type: String): Type {
        return when (type) {
            "_" -> Type.TITLE
            "0" -> Type.DESC
            "1" -> Type.RATING
            "2" -> Type.MULTI
            else -> Type.ILLEGAL
        }
    }

    private fun renderTitleCommitFrame(canvas: Canvas, title: String, question: Question): Float {
        return titleAndCommits.surveyTitleCommit(
            canvas = canvas,
            title = title,
            commits = question as Question.SurveyTitle
        )
    }

    private fun renderDescriptions(canvas: Canvas, question: Question, cursor: Float): Float {
        return titleAndCommits.questionCommits(
            canvas = canvas,
            descriptions = question as Question.SurveyDescription,
            cursorPosition = cursor,
        )
    }

    private fun renderRatingQuestions(canvas: Canvas, question: Question, cursor: Float): Float {
        return ratingQuestion.drawRatingQuestion(
            canvas = canvas,
            data = question as Question.RatingQuestion,
            cursorPosition = cursor,
        )
    }

    private fun renderMultiChoQuest(canvas: Canvas, question: Question, cursor: Float): Float {
        return multipleChoiceQuestions.drawMultipleChoiceQuestions(
            canvas = canvas,
            data = question as Question.MultipleChoiceQuestion,
            cursorPosition = cursor,
        )
    }

    fun renderDocument(canvas: Canvas, cursor: Float, data: SurveyItem): Float {
        var cursorPosition = cursor
        val type = questionTypeCheck(data.type)
        data.questions.forEach {
            cursorPosition = when (type) {
                Type.TITLE -> renderTitleCommitFrame(canvas, data.title, it)
                Type.DESC -> renderDescriptions(canvas, it, cursorPosition)
                Type.RATING -> renderRatingQuestions(canvas, it, cursorPosition)
                Type.MULTI -> renderMultiChoQuest(canvas, it, cursorPosition)
                Type.ILLEGAL -> cursorPosition
            }
        }
        return cursorPosition
    }


}