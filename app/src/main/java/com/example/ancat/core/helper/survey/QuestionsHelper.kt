package com.example.ancat.core.helper.survey

import android.graphics.Canvas
import android.graphics.Paint
import com.example.ancat.data.MultipleChoiceQuest

class QuestionsHelper {

    fun surveyFrame(
        canvas: Canvas,
        paint: Paint,
        cursorPosition: Float
    ): Float {

        val right = 585f
        val bottom = 822f
        val left = 10f

        canvas.drawLine(left, cursorPosition, right, cursorPosition, paint) // üst
        canvas.drawLine(right, cursorPosition, right, bottom, paint) // Sağ
        canvas.drawLine(right, bottom, left, bottom, paint) // Alt
        canvas.drawLine(left, cursorPosition, left, bottom, paint) // Sol

        return cursorPosition + 30f
    }

    fun surveyTitleCommit(
        canvas: Canvas,
        paint: Paint,
        paintTitle: Paint,
        title: String,
        commits: List<String>
    ): Float {

        var cursorPos = 30f

        val textTitleX = (595f - paintTitle.measureText(title)) / 2
        canvas.drawText(title, textTitleX, cursorPos, paintTitle)
        cursorPos += 15f

        commits.forEach { commit ->
            val textCommitX = 30f //(595f - paint.measureText(commit)) / 2
            canvas.drawText(commit, textCommitX, cursorPos, paint)
            cursorPos += 20f
        }
        return cursorPos + 15f
    }

    fun questionCommits(
        canvas: Canvas,
        paint: Paint,
        paintTitle: Paint,
        title: String,
        commits: List<String>,
        cursorPosition: Float,
    ): Float {

        var cursorPos = cursorPosition

        val textTitleX = (595f - paintTitle.measureText(title)) / 2
        canvas.drawText(title, textTitleX, cursorPos, paintTitle)
        cursorPos += 15f

        commits.forEach { commit ->
            val textCommitX = 30f //(595f - paint.measureText(commit)) / 2
            canvas.drawText(commit, textCommitX, cursorPos, paint)
            cursorPos += 20f
        }
        return cursorPos + 15f
    }

    fun ratingQuestion(
        canvas: Canvas,
        paint: Paint,
        title: String,
        questions: List<String>,
        cursorPosition: Float,
    ): Float {

        val questionWidth = 430f  // Soru sütunu genişliği
        val optionWidth = 21f     // Puan sütunu genişliği (5, 4, 3, 2, 1)
        val cellHeight = 25f

        var cursorPos = cursorPosition

        val start = 20f
        val end = 575f
        val textX = 30f

        val textTitleX = (595f - paint.measureText(title)) / 2
        canvas.drawText(title, textTitleX, cursorPos, paint)
        cursorPos += 10f

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
        return cursorPos + 30f // Sorular arası boşluk
    }

    fun multipleChoiceQuestion(
        canvas: Canvas,
        paint: Paint,
        title: String,
        questions: List<MultipleChoiceQuest>,
        cursorPosition: Float,
    ): Float {

        val questionWidth = 380f  // Soru sütunu genişliği
        val cellHeight = 20f

        var cursorPos = cursorPosition
        val cursorP = cursorPosition + 10f

        val start = 20f
        val end = 575f
        val textX = 30f

        val textTitleX = (595f - paint.measureText(title)) / 2
        canvas.drawText(title, textTitleX, cursorPos, paint)
        cursorPos += 10f

        questions.forEach { arr ->
            var textY = cursorPos + (cellHeight + paint.textSize) / 2
            canvas.drawText(arr.question, textX, textY, paint)
            cursorPos += (arr.options.size - 1) * 30f

            arr.options.forEachIndexed { index, option ->
                canvas.drawText("${index + 1}. (     )   $option", questionWidth, textY, paint)
                textY += 20f
            }

            canvas.drawLine(start, cursorP, end, cursorP, paint) // üst
            canvas.drawLine(end, cursorP, end, cursorPos + cellHeight, paint) // Sağ
            canvas.drawLine(start, cursorPos + cellHeight, end, cursorPos + cellHeight, paint) // Alt
            canvas.drawLine(start, cursorP, start, cursorPos + cellHeight, paint) // Sol

            cursorPos += cellHeight
        }

        return cursorPos + 30f // Sorular arası boşluk
    }
}