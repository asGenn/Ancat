package edu.aibu.ancat.core.renderer.strategy

import android.graphics.Canvas
import edu.aibu.ancat.core.renderer.survey_drawings.drawer.TitleAndCommits
import edu.aibu.ancat.data.model.Question
import javax.inject.Inject

/**
 * Başlık sorusu render stratejisi
 * SOLID - SRP: Sadece başlık sorularını render etme sorumluluğu var
 */
class TitleQuestionStrategy @Inject constructor(
    private val titleAndCommits: TitleAndCommits
) : QuestionRendererStrategy {
    
    override fun renderQuestion(canvas: Canvas, question: Question, cursorPosition: Float): Float {
        if (question !is Question.SurveyTitle) return cursorPosition
        
        return titleAndCommits.surveyTitleCommit(
            canvas = canvas,
            commits = question
        )
    }
} 