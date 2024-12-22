package edu.aibu.ancat.core.renderer.survey_drawings.questions

import android.graphics.Canvas
import android.graphics.Paint
import edu.aibu.ancat.utils.PaintFactory
import edu.aibu.ancat.core.renderer.survey_drawings.other.CommonDrawings
import edu.aibu.ancat.utils.DocumentConstants.CELL_HEIGHT
import edu.aibu.ancat.utils.DocumentConstants.MARGIN
import edu.aibu.ancat.utils.DocumentConstants.OPTION_SPACING
import edu.aibu.ancat.utils.DocumentConstants.PAGE_WIDTH
import edu.aibu.ancat.data.model.Question
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MultipleChoiceQuestions @Inject constructor(
    private val commonDrawings: CommonDrawings,
    private val paintFactory: PaintFactory
) {

    fun drawMultipleChoiceQuestions(
        canvas: Canvas,
        questions: List<Question.MultipleChoiceQuestion>,
        cursorPosition: Float
    ): Float {
        var currentCursor = cursorPosition

        questions.forEach { question ->
            val textCenter = currentCursor + (CELL_HEIGHT + paintFactory.text().textSize) / 2
            canvas.drawText(question.question, MARGIN * 3, textCenter, paintFactory.text())

            currentCursor = drawOptions(
                canvas,
                question.options,
                currentCursor,
                paintFactory.text()
            )

            currentCursor += MARGIN

            commonDrawings.drawFrame(
                canvas = canvas,
                xLeft = MARGIN * 2,
                xRight = PAGE_WIDTH - MARGIN * 2,
                yTop = cursorPosition,
                yBottom = currentCursor
            )

        }
        return currentCursor + MARGIN * 3
    }

    private fun drawOptions(
        canvas: Canvas,
        options: List<String>,
        currentCursor: Float,
        paint: Paint
    ): Float {

        var cursorPosition = currentCursor
        options.forEachIndexed { index, option ->
            val textCenter = cursorPosition + (OPTION_SPACING + paintFactory.text().textSize) / 2
            canvas.drawText("${index + 1}. (     ) $option", PAGE_WIDTH - MARGIN * 12, textCenter, paint)
            cursorPosition += OPTION_SPACING
        }
        return cursorPosition
    }

}
