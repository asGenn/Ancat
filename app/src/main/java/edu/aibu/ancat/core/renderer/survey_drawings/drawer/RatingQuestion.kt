package edu.aibu.ancat.core.renderer.survey_drawings.drawer

import android.graphics.Canvas
import edu.aibu.ancat.core.renderer.survey_drawings.utils.TextHandler
import edu.aibu.ancat.utils.PaintFactory
import edu.aibu.ancat.utils.DocumentConstants.CELL_HEIGHT
import edu.aibu.ancat.utils.DocumentConstants.MARGIN
import edu.aibu.ancat.utils.DocumentConstants.PAGE_WIDTH
import edu.aibu.ancat.data.model.Question
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RatingQuestion @Inject constructor(
    private val canvasContentDrawer: CanvasContentDrawer,
    private val textHandler: TextHandler,
    private val paintFactory: PaintFactory
) {

    fun drawRatingQuestion(
        canvas: Canvas,
        data: Question.RatingQuestion,
        cursorPosition: Float,
    ): Float {

        var currentCursor = cursorPosition

        drawRating(canvas = canvas, cursorPosition = currentCursor)

        val textList: List<String> = textHandler.getWrappedText(
            text = data.question,
            paint = paintFactory.text(),
            xCursor = MARGIN * 3,
            maxWidth = PAGE_WIDTH - MARGIN * 20
        )

        textList.forEach { text ->
            val textCenter = currentCursor + (CELL_HEIGHT + paintFactory.text().textSize) / 2
            currentCursor = canvasContentDrawer.writeText(
                canvas = canvas,
                text = text,
                paint = paintFactory.text(),
                xCursor = MARGIN * 3,
                yCursor = textCenter
            )
        }

        canvasContentDrawer.drawFrame(
            canvas = canvas,
            xLeft = MARGIN * 2,
            xRight = PAGE_WIDTH - MARGIN * 2,
            yTop = cursorPosition,
            yBottom = currentCursor + MARGIN,
            paint = paintFactory.text()
        )

        return currentCursor + MARGIN
    }

    private fun drawRating(
        canvas: Canvas,
        cursorPosition: Float
    ) {
        for (rating in 1..5) {
            val positionX = (PAGE_WIDTH - MARGIN * 3) - (rating * CELL_HEIGHT)
            val positionY = cursorPosition + (CELL_HEIGHT + paintFactory.text().textSize) / 2
            canvasContentDrawer.writeText(
                canvas = canvas,
                text = rating.toString(),
                paint = paintFactory.text(),
                xCursor = positionX,
                yCursor = positionY
            )
        }
    }
}