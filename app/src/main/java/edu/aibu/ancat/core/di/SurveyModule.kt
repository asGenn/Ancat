package edu.aibu.ancat.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.aibu.ancat.core.helper.DocumentFactory
import edu.aibu.ancat.core.helper.DocumentHelper
import edu.aibu.ancat.core.helper.DocumentStorage
import edu.aibu.ancat.core.renderer.DocumentRenderer
import edu.aibu.ancat.core.renderer.survey_drawings.utils.DrawingMeasurerHandler
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SurveyModule {
    @Provides
    @Singleton
    fun provideDocumentHelper(
        documentRenderer: DocumentRenderer,
        drawingMeasurerHandler: DrawingMeasurerHandler,
        documentStorage: DocumentStorage,
        documentFactory: DocumentFactory
    ): DocumentHelper = DocumentHelper(
        documentRenderer,
        drawingMeasurerHandler,
        documentStorage,
        documentFactory
    )
}