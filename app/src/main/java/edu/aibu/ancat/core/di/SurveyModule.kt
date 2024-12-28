package edu.aibu.ancat.core.di

import edu.aibu.ancat.utils.PaintFactory
import edu.aibu.ancat.core.renderer.survey_drawings.drawer.CanvasContentDrawer
import edu.aibu.ancat.core.renderer.survey_drawings.drawer.TitleAndCommits
import edu.aibu.ancat.core.renderer.survey_drawings.drawer.MultipleChoiceQuestions
import edu.aibu.ancat.core.renderer.survey_drawings.drawer.RatingQuestion
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.aibu.ancat.core.helper.DocumentHelper
import edu.aibu.ancat.core.renderer.DocumentRenderer
import edu.aibu.ancat.core.renderer.survey_drawings.utils.DrawingMeasurerHandler
import edu.aibu.ancat.core.renderer.survey_drawings.utils.PagedQuestionHandler
import edu.aibu.ancat.core.renderer.survey_drawings.utils.TextHandler
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SurveyModule {

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
    ): MultipleChoiceQuestions =
        MultipleChoiceQuestions(canvasContentDrawer, textHandler, PaintFactory)


    @Provides
    @Singleton
    fun provideRatingQuestion(
        canvasContentDrawer: CanvasContentDrawer,
        textHandler: TextHandler
    ): RatingQuestion = RatingQuestion(canvasContentDrawer, textHandler, PaintFactory)

    @Provides
    @Singleton
    fun provideDocumentRenderer(
        titleAndCommits: TitleAndCommits,
        ratingQuestion: RatingQuestion,
        multipleChoiceQuestions: MultipleChoiceQuestions
    ): DocumentRenderer = DocumentRenderer(titleAndCommits, ratingQuestion, multipleChoiceQuestions)

    @Provides
    @Singleton
    fun provideDocumentHelper(
        documentRenderer: DocumentRenderer,
        drawingMeasurerHandler: DrawingMeasurerHandler,
        pagedQuestionHandler: PagedQuestionHandler
    ): DocumentHelper =
        DocumentHelper(documentRenderer, drawingMeasurerHandler, pagedQuestionHandler)

}