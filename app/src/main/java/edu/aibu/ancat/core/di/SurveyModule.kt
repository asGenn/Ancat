package edu.aibu.ancat.core.di

import edu.aibu.ancat.utils.PaintFactory
import edu.aibu.ancat.core.helper.SurveyHelper
import edu.aibu.ancat.core.renderer.SurveyProcessor
import edu.aibu.ancat.core.renderer.SurveyRenderer
import edu.aibu.ancat.core.renderer.survey_drawings.other.CommonDrawings
import edu.aibu.ancat.core.renderer.survey_drawings.other.TitleAndCommits
import edu.aibu.ancat.core.renderer.survey_drawings.questions.MultipleChoiceQuestions
import edu.aibu.ancat.core.renderer.survey_drawings.questions.RatingQuestion
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
    ): SurveyProcessor =
        SurveyRenderer(
            commonDrawings = commonDrawings,
            titleAndCommits = titleAndCommits,
            multipleChoiceQuestions = multipleChoiceQuestions,
            ratingQuestion = ratingQuestion
        )

    @Provides
    @Singleton
    fun provideSurveyHelper(
        surveyProcessor: SurveyProcessor,
    ): SurveyHelper = SurveyHelper(surveyProcessor)

}
