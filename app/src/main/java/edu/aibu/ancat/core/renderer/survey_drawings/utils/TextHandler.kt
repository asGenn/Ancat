package edu.aibu.ancat.core.renderer.survey_drawings.utils

import android.graphics.Paint
import javax.inject.Inject

class TextHandler @Inject constructor() {

    fun getWrappedText(
        text: String,
        paint: Paint,
        xCursor: Float,
        maxWidth: Float
    ): List<String> {
        val wrappedText = mutableListOf<String>()
        var remainingText = text

        while (xCursor + paint.measureText(remainingText) > maxWidth) {
            var breakIndex = remainingText.length
            while (xCursor + paint.measureText(remainingText.substring(0, breakIndex)) > maxWidth) {
                breakIndex--
            }

            // Boşlukta kırılmaya çalışılır
            val spaceIndex = remainingText.lastIndexOf(' ', breakIndex)
            val lineEndIndex = if (spaceIndex != -1) spaceIndex else breakIndex

            // Kırılan satırı listeye ekle
            wrappedText.add(remainingText.substring(0, lineEndIndex).trim())
            remainingText = remainingText.substring(lineEndIndex).trim()
        }

        // Son kalan kısmı ekle
        if (remainingText.isNotEmpty()) {
            wrappedText.add(remainingText)
        }

        return wrappedText
    }

}