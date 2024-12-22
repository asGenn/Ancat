package edu.aibu.ancat.core.renderer.survey_drawings.other

import android.graphics.Canvas
import edu.aibu.ancat.utils.DocumentConstants.MARGIN
import edu.aibu.ancat.utils.PaintFactory
import edu.aibu.ancat.utils.DocumentConstants.PAGE_WIDTH
import edu.aibu.ancat.utils.DocumentConstants.QUESTION_HEIGHT
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommonDrawings @Inject constructor(
    private val paintFactory: PaintFactory
) {

    fun surveyFrame(
        canvas: Canvas,
        cursorPosition: Float
    ): Float {
        drawFrame(
            canvas = canvas,
            xLeft = MARGIN,
            xRight = PAGE_WIDTH - MARGIN,
            yTop = cursorPosition,
            yBottom = QUESTION_HEIGHT
        )
        return cursorPosition + MARGIN * 3
    }

    fun drawFrame(
        canvas: Canvas,
        xLeft: Float,
        xRight: Float,
        yTop: Float,
        yBottom: Float
    ) {
        canvas.drawLine(xLeft, yTop, xLeft, yBottom, paintFactory.text()) // Sol
        canvas.drawLine(xLeft, yTop, xRight, yTop, paintFactory.text()) // üst
        canvas.drawLine(xRight, yTop, xRight, yBottom, paintFactory.text()) // Sağ
        canvas.drawLine(xLeft, yBottom, xRight, yBottom, paintFactory.text()) // Alt
    }

}