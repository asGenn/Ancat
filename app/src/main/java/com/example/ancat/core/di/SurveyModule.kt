package com.example.ancat.core.di

import android.graphics.pdf.PdfDocument
import com.example.ancat.core.helper.survey.DocumentHelper
import com.example.ancat.core.helper.survey.QuestionsHelper
import com.example.ancat.core.helper.survey.SurveyHelper
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
    fun provideDocumentHelper(): DocumentHelper = DocumentHelper()

    @Provides
    @Singleton
    fun provideQuestionsHelper(): QuestionsHelper = QuestionsHelper()

    @Provides
    @Singleton
    fun provideSurveyHelper(
        documentHelper: DocumentHelper,
        questionsHelper: QuestionsHelper,
    ): SurveyHelper = SurveyHelper(documentHelper, questionsHelper)

}
