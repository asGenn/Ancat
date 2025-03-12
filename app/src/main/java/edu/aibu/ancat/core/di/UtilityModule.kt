package edu.aibu.ancat.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.aibu.ancat.core.helper.JsonHelper
import edu.aibu.ancat.core.helper.TimeConverter
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UtilityModule {
    @Provides
    @Singleton
    fun provideJsonHelper() = JsonHelper()

    @Provides
    @Singleton
    fun provideTimeConverter() = TimeConverter()
} 