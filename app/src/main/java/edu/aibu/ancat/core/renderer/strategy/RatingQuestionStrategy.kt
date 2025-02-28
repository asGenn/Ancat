package edu.aibu.ancat.core.renderer.strategy

import android.graphics.Canvas
import edu.aibu.ancat.core.renderer.survey_drawings.drawer.RatingQuestion
import edu.aibu.ancat.data.model.Question
import javax.inject.Inject

/**
 * Derecelendirme sorusu render stratejisi
 * SOLID - SRP: Sadece derecelendirme sorularını render etme sorumluluğu var
 */
class RatingQuestionStrategy @Inject constructor(
    private val ratingQuestionDrawer: RatingQuestion
) : QuestionRendererStrategy {
    
    override fun renderQuestion(canvas: Canvas, question: Question, cursorPosition: Float): Float {
        if (question !is Question.RatingQuestion) return cursorPosition
        
        return ratingQuestionDrawer.drawRatingQuestion(
            canvas = canvas,
            data = question,
            cursorPosition = cursorPosition
        )
    }
} 