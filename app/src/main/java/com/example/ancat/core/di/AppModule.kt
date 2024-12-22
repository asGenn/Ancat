package com.example.ancat.core.di

import android.content.Context
import androidx.room.Room
import com.example.ancat.core.helper.JsonHelper
import com.example.ancat.data.repository.JsonFilesRepository
import com.example.ancat.data.sources.AppDatabase
import com.example.ancat.data.sources.JsonFilesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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

}