package edu.aibu.ancat.core.renderer.survey_drawings.drawer

import android.graphics.Canvas
import android.graphics.Paint
import javax.inject.Singleton

@Singleton
class CanvasContentDrawer {

    fun writeText(
        canvas: Canvas,
        text: String,
        paint: Paint,
        xCursor: Float,
        yCursor: Float,
    ): Float {
        canvas.drawText(text, xCursor, yCursor, paint)
        return yCursor
    }

    fun drawFrame(
        canvas: Canvas,
        xLeft: Float,
        xRight: Float,
        yTop: Float,
        yBottom: Float,
        paint: Paint
    ) {
        canvas.drawLine(xLeft, yTop, xLeft, yBottom, paint) // Sol
        canvas.drawLine(xLeft, yTop, xRight, yTop, paint) // üst
        canvas.drawLine(xRight, yTop, xRight, yBottom, paint) // Sağ
        canvas.drawLine(xLeft, yBottom, xRight, yBottom, paint) // Alt
    }

}