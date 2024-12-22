package edu.aibu.ancat.core.renderer

import android.graphics.Canvas
import edu.aibu.ancat.core.renderer.survey_drawings.other.CommonDrawings
import edu.aibu.ancat.core.renderer.survey_drawings.other.TitleAndCommits
import edu.aibu.ancat.core.renderer.survey_drawings.questions.MultipleChoiceQuestions
import edu.aibu.ancat.core.renderer.survey_drawings.questions.RatingQuestion
import edu.aibu.ancat.data.model.Question
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SurveyRenderer @Inject constructor(
    private val commonDrawings: CommonDrawings,
    private val titleAndCommits: TitleAndCommits,
    private val multipleChoiceQuestions: MultipleChoiceQuestions,
    private val ratingQuestion: RatingQuestion
): SurveyProcessor {

    override fun processSurveyFrame(
        canvas: Canvas,
        cursorPosition: Float
    ): Float {
        return commonDrawings.surveyFrame(canvas, cursorPosition)
    }

    override fun processTitleCommitFrame(
        canvas: Canvas,
        title: String,
        commits: Question.SurveyTitle
    ): Float {
        val cursorPosition = titleAndCommits.surveyTitleCommit(canvas, title, commits)
        return commonDrawings.surveyFrame(canvas, cursorPosition)
    }

    override fun processDescriptions(
        canvas: Canvas,
        commits: List<Question.SurveyDescription>,
        currentCursor: Float
    ): Float {
        return titleAndCommits.questionCommits(canvas, commits, currentCursor)
    }

    override fun processRatingQuestions(
        canvas: Canvas,
        questions: List<Question.RatingQuestion>,
        currentCursor: Float
    ): Float {
        return ratingQuestion.drawRatingQuestion(canvas, questions, currentCursor)
    }

    override fun processMultipleChoiceQuestions(
        canvas: Canvas,
        questions: List<Question.MultipleChoiceQuestion>,
        currentCursor: Float
    ): Float {
        return multipleChoiceQuestions.drawMultipleChoiceQuestions(
            canvas, questions, currentCursor
        )
    }

}