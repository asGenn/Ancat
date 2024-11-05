package com.example.ancat.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore by preferencesDataStore("survey_data_store")
@Singleton
class SurveyRepository @Inject constructor(
    @ApplicationContext private  val context: Context
) {
    companion object {
        private val JSON_FILES_KEY = stringSetPreferencesKey("json_files")
    }


    suspend fun saveFileNames(fileNames: Set<String>) {
        context.dataStore.edit { preferences ->
            preferences[JSON_FILES_KEY] = fileNames
        }
    }
    fun getFileNames(): Flow<Set<String>> {
        return context.dataStore.data
            .map { preferences ->
                preferences[JSON_FILES_KEY] ?: emptySet()
            }
    }
//    fun readJsonFromFile(context: Context, fileName: String) {
//        val jsonContent = readJsonFromFile(context, fileName)
//
//    }
}