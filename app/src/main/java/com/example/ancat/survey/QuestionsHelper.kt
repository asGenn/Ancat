package com.example.ancat.survey

import android.graphics.Canvas
import android.graphics.Paint

class QuestionsHelper {

    fun ratingQuestion(
        canvas: Canvas,
        paint: Paint,
        question: String,
        cursorPos: Float,
    ): Float {

        val questionWidth = 430f  // Soru sütunu genişliği
        val optionWidth = 21f     // Puan sütunu genişliği (5, 4, 3, 2, 1)
        val cellHeight = 30f

        // Metni dikey olarak ortala
        val start = 20f
        val end = 575f

        val textX = 30f
        val textY = cursorPos + (cellHeight + paint.textSize) / 2

        // Soruyu çiz
        canvas.drawText(question, textX, textY, paint)

        for (i in 1..5)
            canvas.drawText(i.toString(), questionWidth + i * optionWidth, textY, paint)

        canvas.drawLine(start, cursorPos, end, cursorPos, paint) // Üst kenar
        canvas.drawLine(start, cursorPos, start, cursorPos + cellHeight, paint) // Sağ kenar
        canvas.drawLine(
            start,
            cursorPos + cellHeight,
            end,
            cursorPos + cellHeight,
            paint
        ) // Alt kenar
        canvas.drawLine(end, cursorPos, end, cursorPos + cellHeight, paint) // Sol kenar

        return cursorPos + cellHeight

    }
}