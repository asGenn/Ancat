package edu.aibu.ancat.ui.views.test_screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import edu.aibu.ancat.core.helper.JsonHelper
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

                val path = JsonHelper().saveJsonToFile(
                    context = context,
                    fileName = "TestData",
                    jsonData = Json.encodeToString(viewModel.data)
                )

                viewModelSec.saveJsonFileToDB(
                    fileName = "TestData",
                    filePath = path,
                    title = "Test Anketi"
                )
            } }
        ) {
            Text(text = "Test Anketi Ekle")
        }
    }
}