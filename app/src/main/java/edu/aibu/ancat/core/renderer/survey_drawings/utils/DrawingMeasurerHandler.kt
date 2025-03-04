package edu.aibu.ancat.core.renderer.survey_drawings.utils

import edu.aibu.ancat.data.model.Question
import edu.aibu.ancat.utils.DocumentConstants.CELL_HEIGHT
import edu.aibu.ancat.utils.DocumentConstants.MARGIN
import edu.aibu.ancat.utils.DocumentConstants.PAGE_WIDTH
import edu.aibu.ancat.utils.DocumentConstants.QUESTION_HEIGHT
import edu.aibu.ancat.utils.DocumentConstants.TEXT
import edu.aibu.ancat.utils.DocumentConstants.TITLE_PADDING
import edu.aibu.ancat.utils.DocumentConstants.ZERO
import edu.aibu.ancat.utils.PaintFactory
import javax.inject.Inject


class DrawingMeasurerHandler @Inject constructor(
    private val textHandler: TextHandler,
    private val paintFactory: PaintFactory
) {
    internal fun descLength(drawings: List<Question.SurveyDescription>): Float {
        var cursorLength = ZERO
        drawings.forEach { data ->
            data.description.forEach {
                cursorLength += textHandler.getWrappedText(
                    it,
                    paint = paintFactory.text(),
                    xCursor = MARGIN * 3,
                    maxWidth = PAGE_WIDTH - MARGIN * 20
                ).size * TITLE_PADDING
            }
        }
        return cursorLength
    }

    internal fun ratingQuestLength(drawings: List<Question.RatingQuestion>): Float {
        var cursorLength = ZERO
        drawings.forEach { data ->
            cursorLength += textHandler.getWrappedText(
                data.question,
                paint = paintFactory.text(),
                xCursor = MARGIN * 3,
                maxWidth = PAGE_WIDTH - MARGIN * 20
            ).size * ((CELL_HEIGHT + paintFactory.text().textSize) / 2 + TEXT)
            cursorLength += CELL_HEIGHT
        }
        return cursorLength
    }

    internal fun multiChoQuestLength(drawings: List<Question.MultipleChoiceQuestion>): Float {
        var cursorLength = ZERO
        var tempCursor = ZERO
        drawings.forEachIndexed { index, data ->
            cursorLength += textHandler.getWrappedText(
                data.question,
                paint = paintFactory.text(),
                xCursor = MARGIN * 3,
                maxWidth = PAGE_WIDTH - MARGIN * 20
            ).size * ((CELL_HEIGHT + paintFactory.text().textSize) / 2 + TEXT)
            cursorLength += CELL_HEIGHT

            data.options.forEach { option ->
                tempCursor += textHandler.getWrappedText(
                    "${index + 1}. (     ) $option",
                    paint = paintFactory.text(),
                    xCursor = PAGE_WIDTH - MARGIN * 17,
                    maxWidth = PAGE_WIDTH - MARGIN * 3
                ).size * ((CELL_HEIGHT + paintFactory.text().textSize) / 2 + TEXT)
                tempCursor += MARGIN
            }
            tempCursor += CELL_HEIGHT
        }
        return if (tempCursor > cursorLength) tempCursor
        else cursorLength
    }

    fun handlePageBreakIfNeeded(drawings: List<Question>, cursorPosition: Float): Boolean {
        val hasSurveyDescription = drawings.any { it is Question.SurveyDescription }
        val hasRatingQuestions = drawings.any { it is Question.RatingQuestion }
        val hasMultipleChoiceQuestions = drawings.any { it is Question.MultipleChoiceQuestion }

        @Suppress("UNCHECKED_CAST")
        val cursorLength = when {
            hasSurveyDescription -> descLength(drawings as List<Question.SurveyDescription>)
            hasRatingQuestions -> ratingQuestLength(drawings as List<Question.RatingQuestion>)
            hasMultipleChoiceQuestions -> multiChoQuestLength(drawings as List<Question.MultipleChoiceQuestion>)
            else -> cursorPosition
        }
        return QUESTION_HEIGHT < cursorLength + cursorPosition
    }
}
