package edu.aibu.ancat.core.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import edu.aibu.ancat.core.renderer.DocumentRenderer
import edu.aibu.ancat.core.renderer.strategy.*
import edu.aibu.ancat.core.renderer.survey_drawings.drawer.QRCodeDrawer
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SurveyStrategyModule {
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

    @Binds
    abstract fun bindQrCodeStrategy(qrCodeFactory: QRCodeFactory): QRRendererStrategy

    companion object {
        @Provides
        @Singleton
        fun provideQuestionStrategyFactory(
            strategies: Map<String, @JvmSuppressWildcards Provider<QuestionRendererStrategy>>
        ): QuestionStrategyFactory = QuestionStrategyFactory(strategies)

        @Provides
        @Singleton
        fun provideQrCodeFactory(
            qrCodeDrawer: QRCodeDrawer
        ): QRCodeFactory = QRCodeFactory(qrCodeDrawer)

        @Provides
        @Singleton
        fun provideDocumentRenderer(
            questionStrategyFactory: QuestionStrategyFactory,
            qrCodeFactory: QRCodeFactory
        ): DocumentRenderer = DocumentRenderer(questionStrategyFactory, qrCodeFactory)
    }
} 