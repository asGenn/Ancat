package edu.aibu.ancat.ui.views.test_screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import edu.aibu.ancat.core.helper.JsonHelper
import edu.aibu.ancat.domain.entity.JsonFilesInfoEntity
import edu.aibu.ancat.ui.views.create_screen.CreateScreenViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun TestView() {
    val viewModel: TestViewModel = hiltViewModel()
    val viewModelSec: CreateScreenViewModel = hiltViewModel()
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = { CoroutineScope(Dispatchers.Main).launch {

                // Saving JSON to file
                val path = JsonHelper().saveJsonToFile(
                    context = context,
                    fileName = "TestData",
                    jsonData = Json.encodeToString(viewModel.data)
                )

                // Fetching existing files from DB
                val data: List<JsonFilesInfoEntity> = viewModelSec.getJsonFiles()

                // Check if the file already exists in the database
                val fileExists = data.any { it.fileName == "TestData" }

                if (fileExists) {
                    // Show a toast if the file already exists
                    Toast.makeText(context, "Already exists", Toast.LENGTH_SHORT).show()
                } else {
                    // Save the file to the database if it doesn't exist
                    viewModelSec.saveJsonFileToDB(
                        fileName = "TestData",
                        filePath = path,
                        title = "Test Anketi"
                    )
                    // Show a toast that the file was added successfully
                    Toast.makeText(context, "Added successfully", Toast.LENGTH_SHORT).show()
                }

            } }
        ) {
            Text(text = "Test Anketi Ekle")
        }
    }
}