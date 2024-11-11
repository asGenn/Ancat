package com.example.ancat.core.helper

import android.content.Context
import java.io.File


class JsonHelper {


    // read json file from the app's internal storage
    // return the json file content as a string
    fun saveJsonToFile(jsonData: String, fileName: String, context: Context): String {
        val dir = File(context.filesDir, "JsonFiles")
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, "$fileName.json")
        file.writeText(jsonData)
        return file.absolutePath
    }


    // list all json files in the app's internal storage
    // return a list of json file names
    fun listJsonFiles(context: Context): List<String> {
        return context.filesDir.listFiles()?.filter { it.extension == "json" }?.map { it.name }
            ?: emptyList()
    }

    // remove json file from the app's internal storage
    // return true if the file is removed successfully
    fun removeJsonFile(fileName: String, context: Context): Boolean {
        try {
            val file = File(context.filesDir, "JsonFiles/$fileName.json")
            return file.delete()
        } catch (e: Exception) {
            println("Error: ${e.message}")
            return false
        }
    }

}