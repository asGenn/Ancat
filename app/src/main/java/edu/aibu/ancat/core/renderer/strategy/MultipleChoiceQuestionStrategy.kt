package edu.aibu.ancat.core.renderer.strategy

import android.content.Context
import android.graphics.Canvas
import edu.aibu.ancat.core.renderer.survey_drawings.drawer.MultipleChoiceQuestions
import edu.aibu.ancat.data.model.Question
import javax.inject.Inject

/**
 * Çoktan seçmeli soru render stratejisi
 * SOLID - SRP: Sadece çoktan seçmeli soruları render etme sorumluluğu var
 */
class MultipleChoiceQuestionStrategy @Inject constructor(
    private val multipleChoiceQuestions: MultipleChoiceQuestions
) : QuestionRendererStrategy {

    override fun renderQuestion(
        canvas: Canvas,
        question: Question,
        cursorPosition: Float,
        surveyIndex: Int,
        questionIndex: Int,
        jsonFileName: String,
        context: Context
    ): Float {
        if (question !is Question.MultipleChoiceQuestion) return cursorPosition

        return multipleChoiceQuestions.drawMultipleChoiceQuestions(
            canvas = canvas,
            data = question,
            cursorPosition = cursorPosition,
            surveyIndex = surveyIndex,
            questionIndex = questionIndex,
            jsonFileName = jsonFileName,
            context = context
        )
    }
} 