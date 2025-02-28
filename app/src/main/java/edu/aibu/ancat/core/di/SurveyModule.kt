package edu.aibu.ancat.core.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import edu.aibu.ancat.core.helper.DocumentFactory
import edu.aibu.ancat.core.helper.DocumentHelper
import edu.aibu.ancat.core.helper.DocumentStorage
import edu.aibu.ancat.core.helper.impl.FileDocumentStorage
import edu.aibu.ancat.core.helper.impl.PdfDocumentFactory
import edu.aibu.ancat.core.renderer.DocumentRenderer
import edu.aibu.ancat.core.renderer.strategy.*
import edu.aibu.ancat.core.renderer.survey_drawings.drawer.CanvasContentDrawer
import edu.aibu.ancat.core.renderer.survey_drawings.drawer.MultipleChoiceQuestions
import edu.aibu.ancat.core.renderer.survey_drawings.drawer.RatingQuestion
import edu.aibu.ancat.core.renderer.survey_drawings.drawer.TitleAndCommits
import edu.aibu.ancat.core.renderer.survey_drawings.utils.DrawingMeasurerHandler
import edu.aibu.ancat.core.renderer.survey_drawings.utils.PagedQuestionHandler
import edu.aibu.ancat.core.renderer.survey_drawings.utils.TextHandler
import edu.aibu.ancat.utils.PaintFactory
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SurveyBindingsModule {
    
    @Binds
    @Singleton
    abstract fun bindDocumentStorage(fileDocumentStorage: FileDocumentStorage): DocumentStorage
    
    @Binds
    @Singleton
    abstract fun bindDocumentFactory(pdfDocumentFactory: PdfDocumentFactory): DocumentFactory
    
    @Binds
    @IntoMap
    @StringKey("_")
    abstract fun bindTitleQuestionStrategy(strategy: TitleQuestionStrategy): QuestionRendererStrategy
    
    @Binds
    @IntoMap
    @StringKey("0")
    abstract fun bindDescriptionQuestionStrategy(strategy: DescriptionQuestionStrategy): QuestionRendererStrategy
    
    @Binds
    @IntoMap
    @StringKey("1")
    abstract fun bindRatingQuestionStrategy(strategy: RatingQuestionStrategy): QuestionRendererStrategy
    
    @Binds
    @IntoMap
    @StringKey("2")
    abstract fun bindMultipleChoiceQuestionStrategy(strategy: MultipleChoiceQuestionStrategy): QuestionRendererStrategy
    
    @Binds
    @IntoMap
    @StringKey("default")
    abstract fun bindDefaultQuestionStrategy(strategy: DefaultQuestionStrategy): QuestionRendererStrategy
}

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
    fun provideQuestionStrategyFactory(
        strategies: Map<String, @JvmSuppressWildcards Provider<QuestionRendererStrategy>>
    ): QuestionStrategyFactory = QuestionStrategyFactory(strategies)

    @Provides
    @Singleton
    fun provideDocumentRenderer(
        questionStrategyFactory: QuestionStrategyFactory
    ): DocumentRenderer = DocumentRenderer(questionStrategyFactory)

    @Provides
    @Singleton
    fun provideDocumentHelper(
        documentRenderer: DocumentRenderer,
        drawingMeasurerHandler: DrawingMeasurerHandler,
        pagedQuestionHandler: PagedQuestionHandler,
        documentStorage: DocumentStorage,
        documentFactory: DocumentFactory
    ): DocumentHelper =
        DocumentHelper(documentRenderer, drawingMeasurerHandler, pagedQuestionHandler, documentStorage, documentFactory)
}