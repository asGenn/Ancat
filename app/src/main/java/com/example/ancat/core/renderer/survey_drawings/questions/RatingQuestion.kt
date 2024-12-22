package com.example.ancat.core.renderer.survey_drawings.questions

import android.graphics.Canvas
import com.example.ancat.utils.PaintFactory
import com.example.ancat.core.renderer.survey_drawings.other.CommonDrawings
import com.example.ancat.utils.DocumentConstants.CELL_HEIGHT
import com.example.ancat.utils.DocumentConstants.MARGIN
import com.example.ancat.utils.DocumentConstants.PAGE_WIDTH
import com.example.ancat.data.model.Question
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RatingQuestion @Inject constructor(
    private val commonDrawings: CommonDrawings,
    private val paintFactory: PaintFactory
) {

    fun drawRatingQuestion(
        canvas: Canvas,
        questions: List<Question.RatingQuestion>,
        cursorPosition: Float,
    ): Float {

        var currentCursor = cursorPosition

        questions.forEach { data ->
            val textCenter = currentCursor + (CELL_HEIGHT + paintFactory.text().textSize) / 2
            canvas.drawText(data.question, CELL_HEIGHT, textCenter, paintFactory.text())
            drawRating(canvas, textCenter)
            currentCursor += CELL_HEIGHT

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

    private fun drawRating(
        canvas: Canvas,
        textCenter: Float
    ) {
        for (rating in 1..5) {
            val position = (PAGE_WIDTH - MARGIN * 3) - (rating * CELL_HEIGHT)
            canvas.drawText(rating.toString(), position, textCenter, paintFactory.text())
        }
    }
}