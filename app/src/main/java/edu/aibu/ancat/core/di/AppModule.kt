package edu.aibu.ancat.core.di

import android.content.Context
import androidx.room.Room
import edu.aibu.ancat.core.helper.JsonHelper
import edu.aibu.ancat.data.repository.JsonFilesRepository
import edu.aibu.ancat.data.sources.AppDatabase
import edu.aibu.ancat.data.sources.JsonFilesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import edu.aibu.ancat.core.helper.TimeConverter
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "json_files"
        ).build()

    }
    @Provides
    @Singleton
    fun provideJsonFilesDao(appDatabase: AppDatabase) : JsonFilesDao = appDatabase.jsonFilesDao()

    @Provides
    @Singleton
    fun provideJsonFilesRepository(jsonFilesRepository: JsonFilesDao): JsonFilesRepository {
        return JsonFilesRepository(jsonFilesRepository)
    }

    @Provides
    @Singleton
    fun provideJsonHelper() = JsonHelper()

    @Provides
    @Singleton
    fun provideTimeConverter() = TimeConverter()

}