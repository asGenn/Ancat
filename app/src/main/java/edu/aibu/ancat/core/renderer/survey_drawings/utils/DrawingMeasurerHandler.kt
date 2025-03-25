package edu.aibu.ancat.core.renderer.survey_drawings.utils

import edu.aibu.ancat.data.model.Question
import edu.aibu.ancat.utils.DocumentConstants.CELL_HEIGHT
import edu.aibu.ancat.utils.DocumentConstants.MARGIN
import edu.aibu.ancat.utils.DocumentConstants.PAGE_WIDTH
import edu.aibu.ancat.utils.DocumentConstants.QUESTION_HEIGHT
import edu.aibu.ancat.utils.DocumentConstants.TITLE_PADDING
import edu.aibu.ancat.utils.DocumentConstants.ZERO
import edu.aibu.ancat.utils.PaintFactory
import javax.inject.Inject


class DrawingMeasurerHandler @Inject constructor(
    private val textHandler: TextHandler,
    private val paintFactory: PaintFactory
) {
    private fun descLength(drawing: Question.SurveyDescription): Float {
        var cursorLength = ZERO
        drawing.description.forEach {
            cursorLength += textHandler.getWrappedText(
                it,
                paint = paintFactory.text(),
                xCursor = MARGIN * 3,
                maxWidth = PAGE_WIDTH - MARGIN * 20
            ).size * TITLE_PADDING
        }
        return cursorLength
    }

    private fun ratingQuestLength(drawing: Question.RatingQuestion): Float {
        var cursorLength = ZERO
        cursorLength += textHandler.getWrappedText(
            drawing.question,
            paint = paintFactory.text(),
            xCursor = MARGIN * 3,
            maxWidth = PAGE_WIDTH - MARGIN * 20
        ).size * ((CELL_HEIGHT + paintFactory.text().textSize) / 2)
        cursorLength += MARGIN
        return cursorLength
    }

    private fun multiChoQuestLength(drawing: Question.MultipleChoiceQuestion): Float {
        var cursorLength = ZERO
        var tempCursor = ZERO
        cursorLength += textHandler.getWrappedText(
            drawing.question,
            paint = paintFactory.text(),
            xCursor = MARGIN * 3,
            maxWidth = PAGE_WIDTH - MARGIN * 20
        ).size * ((CELL_HEIGHT + paintFactory.text().textSize) / 2)

        drawing.options.forEachIndexed { index, option ->
            tempCursor += textHandler.getWrappedText(
                "${index + 1}. (     ) $option",
                paint = paintFactory.text(),
                xCursor = PAGE_WIDTH - MARGIN * 17,
                maxWidth = PAGE_WIDTH - MARGIN * 3
            ).size * ((CELL_HEIGHT + paintFactory.text().textSize) / 2)
        }

        return if (tempCursor > cursorLength) tempCursor + MARGIN
        else cursorLength + MARGIN
    }

    fun handlePageBreakIfNeeded(drawing: Question, cursorPosition: Float): Boolean {
        val hasSurveyDescription = drawing is Question.SurveyDescription
        val hasRatingQuestions = drawing is Question.RatingQuestion
        val hasMultipleChoiceQuestions = drawing is Question.MultipleChoiceQuestion

        val cursorLength = when {
            hasSurveyDescription -> descLength(drawing as Question.SurveyDescription)
            hasRatingQuestions -> ratingQuestLength(drawing as Question.RatingQuestion)
            hasMultipleChoiceQuestions -> multiChoQuestLength(drawing as Question.MultipleChoiceQuestion)
            else -> cursorPosition
        }
        return QUESTION_HEIGHT < cursorLength + cursorPosition
    }
}
