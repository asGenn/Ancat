package edu.aibu.ancat.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.aibu.ancat.core.renderer.survey_drawings.drawer.*
import edu.aibu.ancat.core.renderer.survey_drawings.utils.*
import edu.aibu.ancat.utils.PaintFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SurveyRendererModule {
    @Provides
    @Singleton
    fun provideCanvasContentDrawer(): CanvasContentDrawer = CanvasContentDrawer()

    @Provides
    @Singleton
    fun provideDrawingMeasurer(
        textHandler: TextHandler
    ): DrawingMeasurerHandler = DrawingMeasurerHandler(textHandler, PaintFactory)

    @Provides
    @Singleton
    fun providePagedQuestionHandler(
        drawingMeasurerHandler: DrawingMeasurerHandler
    ): PagedQuestionHandler = PagedQuestionHandler(drawingMeasurerHandler)

    @Provides
    @Singleton
    fun provideTextHandler(): TextHandler = TextHandler()

    @Provides
    @Singleton
    fun provideTitleAndCommits(): TitleAndCommits = TitleAndCommits(PaintFactory)

    @Provides
    @Singleton
    fun provideMultipleChoiceQuestions(
        canvasContentDrawer: CanvasContentDrawer,
        textHandler: TextHandler
    ): MultipleChoiceQuestions = MultipleChoiceQuestions(canvasContentDrawer, textHandler, PaintFactory)

    @Provides
    @Singleton
    fun provideRatingQuestion(
        canvasContentDrawer: CanvasContentDrawer,
        textHandler: TextHandler
    ): RatingQuestion = RatingQuestion(canvasContentDrawer, textHandler, PaintFactory)
} 