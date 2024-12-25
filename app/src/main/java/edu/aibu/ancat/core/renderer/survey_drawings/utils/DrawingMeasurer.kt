package edu.aibu.ancat.core.renderer.survey_drawings.utils

import edu.aibu.ancat.data.model.Question
import edu.aibu.ancat.data.model.SurveyItem
import edu.aibu.ancat.utils.DocumentConstants.CELL_HEIGHT
import edu.aibu.ancat.utils.DocumentConstants.MARGIN
import edu.aibu.ancat.utils.DocumentConstants.PAGE_WIDTH
import edu.aibu.ancat.utils.DocumentConstants.QUESTION_HEIGHT
import edu.aibu.ancat.utils.DocumentConstants.START_CURSOR
import edu.aibu.ancat.utils.PaintFactory
import javax.inject.Inject


@Suppress("UNCHECKED_CAST")
class DrawingMeasurer @Inject constructor(
    private val textHandler: TextHandler,
    private val paintFactory: PaintFactory
) {
    private fun descLength(drawings: List<Question.SurveyDescription>): Float {
        var cursorLength = START_CURSOR
        drawings.forEach { data ->
            data.description.forEach {
                cursorLength += textHandler.getWrappedText(
                    it,
                    paint = paintFactory.text(),
                    xCursor = MARGIN * 3,
                    maxWidth = PAGE_WIDTH - MARGIN * 20
                ).size * ((CELL_HEIGHT + paintFactory.text().textSize) / 2)
            }
        }
        return cursorLength
    }

    private fun ratingQuestLength(drawings: List<Question.RatingQuestion>): Float {
        var cursorLength = START_CURSOR
        drawings.forEach { data ->
            cursorLength += textHandler.getWrappedText(
                data.question,
                paint = paintFactory.text(),
                xCursor = MARGIN * 3,
                maxWidth = PAGE_WIDTH - MARGIN * 20
            ).size * ((CELL_HEIGHT + paintFactory.text().textSize) / 2 + MARGIN)
        }
        return cursorLength
    }

    private fun multiChoQuestLength(drawings: List<Question.MultipleChoiceQuestion>): Float {
        var cursorLength = START_CURSOR
        var tempCursor = START_CURSOR
        drawings.forEachIndexed { index, data ->
            cursorLength += textHandler.getWrappedText(
                data.question,
                paint = paintFactory.text(),
                xCursor = MARGIN * 3,
                maxWidth = PAGE_WIDTH - MARGIN * 20
            ).size * ((CELL_HEIGHT + paintFactory.text().textSize) / 2 + MARGIN)

            data.options.forEach { option ->
                tempCursor += textHandler.getWrappedText(
                    "${index + 1}. (     ) $option",
                    paint = paintFactory.text(),
                    xCursor = PAGE_WIDTH - MARGIN * 17,
                    maxWidth = PAGE_WIDTH - MARGIN * 3
                ).size * ((CELL_HEIGHT + paintFactory.text().textSize) / 2 + MARGIN)
            }
        }
        return if (tempCursor > cursorLength) tempCursor
        else cursorLength
    }

    fun handlePageBreakIfNeeded(drawings: List<Question>, cursorPosition: Float): Boolean {
        val hasSurveyDescription = drawings.any { it is Question.SurveyDescription }
        val hasRatingQuestions = drawings.any { it is Question.RatingQuestion }
        val hasMultipleChoiceQuestions = drawings.any { it is Question.MultipleChoiceQuestion }

        val cursorLength = when {
            hasSurveyDescription -> descLength(drawings as List<Question.SurveyDescription>)
            hasRatingQuestions -> ratingQuestLength(drawings as List<Question.RatingQuestion>)
            hasMultipleChoiceQuestions -> multiChoQuestLength(drawings as List<Question.MultipleChoiceQuestion>)
            else -> cursorPosition
        }

        return QUESTION_HEIGHT < cursorLength + cursorPosition
    }

    private fun splitDesc(drawings: SurveyItem): List<SurveyItem> {
        val questionDescriptions = drawings.questions as List<Question.SurveyDescription>
        val splitSurveyItems = mutableListOf<SurveyItem>()

        var currentHeight = START_CURSOR
        val currentPageQuestions = mutableListOf<Question.SurveyDescription>()
        questionDescriptions.forEach { question ->
            val questionHeight = descLength(listOf(question))
            if (currentHeight + questionHeight > QUESTION_HEIGHT) {
                splitSurveyItems.add(
                    drawings.copy(
                        questions = currentPageQuestions.toList()
                    )
                )
                currentPageQuestions.clear()
                currentHeight = START_CURSOR
            }
            currentPageQuestions.add(question)
            currentHeight += questionHeight
        }
        if (currentPageQuestions.isNotEmpty()) {
            splitSurveyItems.add(
                drawings.copy(
                    questions = currentPageQuestions.toList()
                )
            )
        }

        return splitSurveyItems
    }


    private fun splitRatingQuest(drawings: SurveyItem): List<SurveyItem> {
        val questionRating = drawings.questions as List<Question.RatingQuestion>
        val splitSurveyItems = mutableListOf<SurveyItem>()

        var currentHeight = START_CURSOR
        val currentPageQuestions = mutableListOf<Question.RatingQuestion>()
        questionRating.forEach { question ->
            val questionHeight = ratingQuestLength(listOf(question))
            if (currentHeight + questionHeight > QUESTION_HEIGHT) {
                splitSurveyItems.add(
                    drawings.copy(
                        questions = currentPageQuestions.toList()
                    )
                )
                currentPageQuestions.clear()
                currentHeight = START_CURSOR
            }
            currentPageQuestions.add(question)
            currentHeight += questionHeight
        }
        if (currentPageQuestions.isNotEmpty()) {
            splitSurveyItems.add(
                drawings.copy(
                    questions = currentPageQuestions.toList()
                )
            )
        }

        return splitSurveyItems
    }

    private fun splitMultiChoQuest(drawings: SurveyItem): List<SurveyItem> {
        val questionMultiChoQuest = drawings.questions as List<Question.MultipleChoiceQuestion>
        val splitSurveyItems = mutableListOf<SurveyItem>()

        var currentHeight = START_CURSOR
        val currentPageQuestions = mutableListOf<Question.MultipleChoiceQuestion>()

        questionMultiChoQuest.forEach { question ->
            val questionHeight = multiChoQuestLength(listOf(question))
            if (currentHeight + questionHeight > QUESTION_HEIGHT) {
                splitSurveyItems.add(
                    SurveyItem(
                        type = drawings.type,
                        title = drawings.title,
                        questions = currentPageQuestions.toList()
                    )
                )
                currentPageQuestions.clear()
                currentHeight = START_CURSOR
            }
            currentPageQuestions.add(question)
            currentHeight += questionHeight
        }

        if (currentPageQuestions.isNotEmpty()) {
            splitSurveyItems.add(
                SurveyItem(
                    type = drawings.type,
                    title = drawings.title,
                    questions = currentPageQuestions.toList()
                )
            )
        }

        return splitSurveyItems
    }


    fun splitQuestion(drawings: SurveyItem): List<SurveyItem> {
        val hasSurveyDescription = drawings.type == "0"
        val hasRatingQuestions = drawings.type == "1"
        val hasMultipleChoiceQuestions = drawings.type == "2"

        return when {
            hasSurveyDescription -> splitDesc(drawings)
            hasRatingQuestions -> splitRatingQuest(drawings)
            hasMultipleChoiceQuestions -> splitMultiChoQuest(drawings)
            else -> emptyList()
        }
    }
}
