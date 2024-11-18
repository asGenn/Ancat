package com.example.ancat.core.di

import com.example.ancat.utils.PaintFactory
import com.example.ancat.core.helper.SurveyHelper
import com.example.ancat.core.renderer.SurveyRenderer
import com.example.ancat.core.renderer.survey_drawings.other.CommonDrawings
import com.example.ancat.core.renderer.survey_drawings.other.TitleAndCommits
import com.example.ancat.core.renderer.survey_drawings.questions.MultipleChoiceQuestions
import com.example.ancat.core.renderer.survey_drawings.questions.RatingQuestion
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SurveyModule {

    @Provides
    @Singleton
    fun provideCommonDrawings(): CommonDrawings = CommonDrawings(PaintFactory)

    @Provides
    @Singleton
    fun provideTitleAndCommits(): TitleAndCommits = TitleAndCommits(PaintFactory)

    @Provides
    @Singleton
    fun provideMultipleChoiceQuestions(
        commonDrawings: CommonDrawings
    ): MultipleChoiceQuestions = MultipleChoiceQuestions(commonDrawings, PaintFactory)


    @Provides
    @Singleton
    fun provideRatingQuestion(
        commonDrawings: CommonDrawings
    ): RatingQuestion = RatingQuestion(commonDrawings, PaintFactory)


    @Provides
    @Singleton
    fun provideSurveyRenderer(
        commonDrawings: CommonDrawings,
        titleAndCommits: TitleAndCommits,
        multipleChoiceQuestions: MultipleChoiceQuestions,
        ratingQuestion: RatingQuestion
    ): SurveyRenderer =
        SurveyRenderer(
            commonDrawings = commonDrawings,
            titleAndCommits = titleAndCommits,
            multipleChoiceQuestions = multipleChoiceQuestions,
            ratingQuestion = ratingQuestion
        )

    @Provides
    @Singleton
    fun provideSurveyHelper(
        surveyRenderer: SurveyRenderer,
    ): SurveyHelper = SurveyHelper(surveyRenderer)

}
