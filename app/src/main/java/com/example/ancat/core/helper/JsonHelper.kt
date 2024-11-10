package com.example.ancat.core.helper

import android.content.Context
import java.io.File
import java.io.FileOutputStream

class JsonHelper {

    fun saveJsonToFile(jsonData: String, fileName: String,context: Context) {
        val dir = File(context.filesDir, "JsonFiles")
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, "$fileName.json")
        file.writeText(jsonData)
    }

    fun listJsonFiles(context: Context): List<String> {
        return context.filesDir.listFiles()?.filter { it.extension == "json" }?.map { it.name }
            ?: emptyList()
    }
}