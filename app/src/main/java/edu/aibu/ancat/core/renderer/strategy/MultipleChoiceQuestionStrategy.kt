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
        context: Context,
        canvas: Canvas,
        question: Question,
        surveyIndex: Int,
        questionIndex: Int,
        cursorPosition: Float,
        jsonFileName: String
    ): Float {
        if (question !is Question.MultipleChoiceQuestion) return cursorPosition

        return multipleChoiceQuestions.drawMultipleChoiceQuestions(
            context = context,
            canvas = canvas,
            data = question,
            surveyIndex = surveyIndex,
            questionIndex = questionIndex,
            cursorPosition = cursorPosition,
            jsonFileName = jsonFileName,
        )
    }
} 