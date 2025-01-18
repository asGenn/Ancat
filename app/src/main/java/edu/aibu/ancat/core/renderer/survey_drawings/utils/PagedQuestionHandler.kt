package edu.aibu.ancat.core.renderer.survey_drawings.utils

import edu.aibu.ancat.data.model.Question
import edu.aibu.ancat.data.model.SurveyItem
import edu.aibu.ancat.utils.DocumentConstants.QUESTION_HEIGHT
import edu.aibu.ancat.utils.DocumentConstants.START_CURSOR
import javax.inject.Inject


@Suppress("UNCHECKED_CAST")
class PagedQuestionHandler @Inject constructor(
    private val drawingMeasurerHandler: DrawingMeasurerHandler
) {
    private fun splitDesc(drawings: SurveyItem, cursor: Float): List<SurveyItem> {
        val questionDescriptions = drawings.questions as List<Question.SurveyDescription>
        val splitSurveyItems = mutableListOf<SurveyItem>()

        var currentHeight = cursor
        val currentPageQuestions = mutableListOf<Question.SurveyDescription>()
        questionDescriptions.forEach { question ->
            val questionHeight = drawingMeasurerHandler.descLength(listOf(question))
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

    private fun splitRatingQuest(drawings: SurveyItem, cursor: Float): List<SurveyItem> {
        val questionRating = drawings.questions as List<Question.RatingQuestion>
        val splitSurveyItems = mutableListOf<SurveyItem>()

        var currentHeight = cursor
        val currentPageQuestions = mutableListOf<Question.RatingQuestion>()
        questionRating.forEach { question ->
            val questionHeight = drawingMeasurerHandler.ratingQuestLength(listOf(question))
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

    private fun splitMultiChoQuest(drawings: SurveyItem, cursor: Float): List<SurveyItem> {
        val questionMultiChoQuest = drawings.questions as List<Question.MultipleChoiceQuestion>
        val splitSurveyItems = mutableListOf<SurveyItem>()

        var currentHeight = cursor
        val currentPageQuestions = mutableListOf<Question.MultipleChoiceQuestion>()

        questionMultiChoQuest.forEach { question ->
            val questionHeight = drawingMeasurerHandler.multiChoQuestLength(listOf(question))
            if (currentHeight + questionHeight > QUESTION_HEIGHT) {
                if (currentPageQuestions.isNotEmpty())
                    splitSurveyItems.add(
                        SurveyItem(
                            type = drawings.type,
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
                    questions = currentPageQuestions.toList()
                )
            )
        }

        return splitSurveyItems
    }


    fun splitQuestion(drawings: SurveyItem, cursor: Float): List<SurveyItem> {
        val hasSurveyDescription = drawings.type == "0"
        val hasRatingQuestions = drawings.type == "1"
        val hasMultipleChoiceQuestions = drawings.type == "2"

        return when {
            hasSurveyDescription -> splitDesc(drawings, cursor)
            hasRatingQuestions -> splitRatingQuest(drawings, cursor)
            hasMultipleChoiceQuestions -> splitMultiChoQuest(drawings, cursor)
            else -> emptyList()
        }
    }
}