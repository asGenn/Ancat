package edu.aibu.ancat.core.renderer.strategy

import android.content.Context
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
    
    override fun renderQuestion(
        context: Context,
        canvas: Canvas,
        question: Question,
        surveyIndex: Int,
        questionIndex: Int,
        cursorPosition: Float,
        jsonFileName: String
    ): Float {
        if (question !is Question.RatingQuestion) return cursorPosition
        
        return ratingQuestionDrawer.drawRatingQuestion(
            context = context,
            canvas = canvas,
            data = question,
            surveyIndex = surveyIndex,
            questionIndex = questionIndex,
            cursorPosition = cursorPosition,
            jsonFileName = jsonFileName
        )
    }
} 