package edu.aibu.ancat.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.aibu.ancat.core.helper.DocumentFactory
import edu.aibu.ancat.core.helper.DocumentStorage
import edu.aibu.ancat.core.helper.impl.FileDocumentStorage
import edu.aibu.ancat.core.helper.impl.PdfDocumentFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DocumentModule {
    @Binds
    @Singleton
    abstract fun bindDocumentStorage(fileDocumentStorage: FileDocumentStorage): DocumentStorage
    
    @Binds
    @Singleton
    abstract fun bindDocumentFactory(pdfDocumentFactory: PdfDocumentFactory): DocumentFactory
} 