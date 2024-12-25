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
import edu.aibu.ancat.core.renderer.survey_drawings.utils.DrawingMeasurer
import edu.aibu.ancat.core.renderer.survey_drawings.utils.TextHandler
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SurveyModule {

    @Provides
    @Singleton
    fun provideCanvasContentDrawer(): CanvasContentDrawer = CanvasContentDrawer(PaintFactory)

    @Provides
    @Singleton
    fun provideDrawingMeasurer(
        textHandler: TextHandler

    ): DrawingMeasurer = DrawingMeasurer(textHandler, PaintFactory)

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
        MultipleChoiceQuestions(
            canvasContentDrawer = canvasContentDrawer,
            textHandler = textHandler,
            paintFactory = PaintFactory
        )


    @Provides
    @Singleton
    fun provideRatingQuestion(
        canvasContentDrawer: CanvasContentDrawer,
        textHandler: TextHandler
    ): RatingQuestion = RatingQuestion(
        canvasContentDrawer = canvasContentDrawer,
        textHandler = textHandler,
        paintFactory = PaintFactory
    )

    @Provides
    @Singleton
    fun provideDocumentRenderer(
        titleAndCommits: TitleAndCommits,
        ratingQuestion: RatingQuestion,
        multipleChoiceQuestions: MultipleChoiceQuestions
    ): DocumentRenderer = DocumentRenderer(
        titleAndCommits = titleAndCommits,
        ratingQuestion = ratingQuestion,
        multipleChoiceQuestions = multipleChoiceQuestions
    )

    @Provides
    @Singleton
    fun provideDocumentHelper(
        documentRenderer: DocumentRenderer,
        drawingMeasurer: DrawingMeasurer
    ): DocumentHelper = DocumentHelper(
        documentRenderer = documentRenderer,
        drawingMeasurer = drawingMeasurer
    )


//    @Provides
//    @Singleton
//    fun provideSurveyRenderer(
//        canvasContentDrawer: CanvasContentDrawer,
//        titleAndCommits: TitleAndCommits,
//        multipleChoiceQuestions: MultipleChoiceQuestions,
//        ratingQuestion: RatingQuestion
//    ): SurveyProcessor =
//        SurveyRenderer(
//            canvasContentDrawer = canvasContentDrawer,
//            titleAndCommits = titleAndCommits,
//            multipleChoiceQuestions = multipleChoiceQuestions,
//            ratingQuestion = ratingQuestion
//        )
//
//    @Provides
//    @Singleton
//    fun provideSurveyHelper(
//        surveyProcessor: SurveyProcessor,
//        drawingMeasurer: DrawingMeasurer
//    ): SurveyHelper =
//        SurveyHelper(
//            surveyRenderer = surveyProcessor, drawingMeasurer = drawingMeasurer)

}
