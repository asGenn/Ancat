package edu.aibu.ancat.core.helper

import android.content.Context
import java.io.File
import javax.inject.Singleton

@Singleton
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
//    fun listJsonFiles(context: Context): List<String> {
//        return context.filesDir.listFiles()?.filter { it.extension == "json" }?.map { it.name }
//            ?: emptyList()
//    }

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

    // read json file from the app's internal storage
    // return the json file content as a string
    fun readJsonFile(fileName: String, context: Context): String {
        val file = File(context.filesDir, "JsonFiles/$fileName.json")
        return file.readText()
    }

    // open file from and write new content to it
    // before writing clear the file content
    fun openFileAndWriteNewContent(
        fileName: String,
        newContent: String,
        context: Context
    ): Boolean {
        try {
            val file = File(context.filesDir, "JsonFiles/$fileName.json")
            file.writeText(newContent)
            return true
        } catch (e: Exception) {
            println("Error: ${e.message}")
            return false
        }
    }

}