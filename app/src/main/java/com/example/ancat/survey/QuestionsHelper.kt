package com.example.ancat.survey

import android.graphics.Canvas
import android.graphics.Paint
import com.example.ancat.data.MultipleChoiceQuest

class QuestionsHelper {

    fun ratingQuestion(
        canvas: Canvas,
        paint: Paint,
        questions: List<String>,
        cursorPosition: Float,
    ): Float {

        val questionWidth = 430f  // Soru sütunu genişliği
        val optionWidth = 21f     // Puan sütunu genişliği (5, 4, 3, 2, 1)
        val cellHeight = 30f

        var cursorPos = cursorPosition

        val start = 20f
        val end = 575f
        val textX = 30f

        questions.forEach { question ->
            val textY = cursorPos + (cellHeight + paint.textSize) / 2
            canvas.drawText(question, textX, textY, paint)
            for (i in 1..5)
                canvas.drawText(i.toString(), questionWidth + i * optionWidth, textY, paint)
            canvas.drawLine(start, cursorPos, end, cursorPos, paint)
            canvas.drawLine(start, cursorPos, start, cursorPos + cellHeight, paint)
            canvas.drawLine(start, cursorPos + cellHeight, end, cursorPos + cellHeight, paint)
            canvas.drawLine(end, cursorPos, end, cursorPos + cellHeight, paint)
            cursorPos += cellHeight
        }
        return cursorPos + 20f // Sorular arası boşluk
    }

    fun multipleChoiceQuestion(
        canvas: Canvas,
        paint: Paint,
        questions: List<MultipleChoiceQuest>,
        cursorPosition: Float,
    ): Float {

        val questionWidth = 380f  // Soru sütunu genişliği
        val cellHeight = 30f

        var cursorPos = cursorPosition

        val start = 20f
        val end = 575f
        val textX = 30f

        questions.forEach { arr ->
            var textY = cursorPos + (cellHeight + paint.textSize) / 2
            canvas.drawText(arr.question, textX, textY, paint)
            cursorPos += (arr.options.size - 1) * 30f

            arr.options.forEachIndexed { index, option ->
                canvas.drawText("${index+1}. (     )   $option", questionWidth, textY, paint)
                textY += 30f
            }

            canvas.drawLine(start, cursorPosition, end, cursorPosition, paint) // üst
            canvas.drawLine(end, cursorPosition, end, cursorPos + cellHeight, paint) // Sağ
            canvas.drawLine(start, cursorPos + cellHeight, end, cursorPos + cellHeight, paint) // Alt
            canvas.drawLine(start, cursorPosition, start, cursorPos + cellHeight, paint) // Sol

            cursorPos += cellHeight
        }

        return cursorPos + 20f // Sorular arası boşluk
    }
}