package edu.aibu.ancat.core.renderer.strategy

import android.content.Context
import android.graphics.Canvas
import edu.aibu.ancat.data.model.Question
import javax.inject.Inject

/**
 * Varsayılan soru stratejisi - bilinmeyen soru tipleri için
 */
class DefaultQuestionStrategy @Inject constructor() : QuestionRendererStrategy {
    
    override fun renderQuestion(
        context: Context,
        canvas: Canvas,
        question: Question,
        surveyIndex: Int,
        questionIndex: Int,
        cursorPosition: Float,
        jsonFileName: String
    ): Float {
        // Bilinmeyen soru tipleri için herhangi bir işlem yapmadan cursor değerini döndür
        return cursorPosition
    }
} 