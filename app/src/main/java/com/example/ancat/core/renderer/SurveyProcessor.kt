package com.example.ancat.core.renderer

import android.graphics.Canvas
import com.example.ancat.data.model.Question

interface SurveyProcessor {

    fun processSurveyFrame(
        canvas: Canvas,
        cursorPosition: Float
    ): Float

    fun processTitleCommitFrame(
        canvas: Canvas,
        title: String,
        commits: Question.SurveyTitle
    ): Float

    fun processDescriptions(
        canvas: Canvas,
        commits: List<Question.SurveyDescription>,
        currentCursor: Float
    ): Float

    fun processRatingQuestions(
        canvas: Canvas,
        questions: List<Question.RatingQuestion>,
        currentCursor: Float
    ): Float

    fun processMultipleChoiceQuestions(
        canvas: Canvas,
        questions: List<Question.MultipleChoiceQuestion>,
        currentCursor: Float
    ): Float
}
