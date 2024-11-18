package com.example.ancat.core.renderer

import android.graphics.Canvas
import com.example.ancat.core.renderer.survey_drawings.other.CommonDrawings
import com.example.ancat.core.renderer.survey_drawings.other.TitleAndCommits
import com.example.ancat.core.renderer.survey_drawings.questions.MultipleChoiceQuestions
import com.example.ancat.core.renderer.survey_drawings.questions.RatingQuestion
import com.example.ancat.data.model.Question
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SurveyRenderer @Inject constructor(
    private val commonDrawings: CommonDrawings,
    private val titleAndCommits: TitleAndCommits,
    private val multipleChoiceQuestions: MultipleChoiceQuestions,
    private val ratingQuestion: RatingQuestion
) {

    fun processSurveyFrame(
        canvas: Canvas,
        cursorPosition: Float
    ): Float {
        return commonDrawings.surveyFrame(canvas, cursorPosition)
    }

    fun processTitleCommitFrame(
        canvas: Canvas,
        title: String,
        commits: Question.SurveyTitle
    ): Float {
        val cursorPosition = titleAndCommits.surveyTitleCommit(canvas, title, commits)
        return commonDrawings.surveyFrame(canvas, cursorPosition)
    }

    fun processDescriptions(
        canvas: Canvas,
        commits: List<Question.SurveyDescription>,
        currentCursor: Float
    ): Float {
        return titleAndCommits.questionCommits(canvas, commits, currentCursor)
    }

    fun processRatingQuestions(
        canvas: Canvas,
        questions: List<Question.RatingQuestion>,
        currentCursor: Float
    ): Float {
        return ratingQuestion.drawRatingQuestion(canvas, questions, currentCursor)
    }

    fun processMultipleChoiceQuestions(
        canvas: Canvas,
        questions: List<Question.MultipleChoiceQuestion>,
        currentCursor: Float
    ): Float {
        return multipleChoiceQuestions.drawMultipleChoiceQuestions(
            canvas, questions, currentCursor
        )
    }

}