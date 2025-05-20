package edu.aibu.ancat.ui.views.camera_screen

import ProblemImagesDialog
import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import edu.aibu.ancat.core.helper.JsonHelper

import edu.aibu.ancat.domain.entity.JsonFilesInfoEntity
import edu.aibu.ancat.ui.views.create_screen.CreateScreenViewModel
import edu.aibu.ancat.ui.views.test_screen.TestViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun AnalyzeScreen() {
    val viewModel: CameraScreenViewModel = hiltViewModel()
    val testViewModel: TestViewModel = hiltViewModel()
    val viewModelSec: CreateScreenViewModel = hiltViewModel()
    val context = LocalContext.current
    //val barcodeResult = remember { mutableStateOf<String?>(null) }
    //val mlKitBarcodeScanner = remember { MLKitBarcodeScanner() }

    val scannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { scanner ->
            viewModel.handleScanResult(scanner)
        }
    )
    LaunchedEffect(viewModel.imageUris) {
        if (viewModel.autoAnalyzeAfterScan && viewModel.imageUris.isNotEmpty()) {
            viewModel.analyzeAllImages(context)
        }
    }
    // Bir resim yeniden çekilmek istendiğinde scanner'ı başlat
    LaunchedEffect(viewModel.retakeImageTrigger) {
        if (viewModel.retakeImageTrigger) {
            viewModel.scanner.getStartScanIntent(context as Activity)
                .addOnSuccessListener {
                    scannerLauncher.launch(
                        IntentSenderRequest.Builder(it).build()
                    )
                    // Tetikleyiciyi sıfırla
                    viewModel.resetRetakeTrigger()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Tarayıcı başlatılamadı", Toast.LENGTH_SHORT).show()
                    viewModel.resetRetakeTrigger()
                }
        }
    }


    ProblemImagesDialog(
        showDialog = viewModel.showProblemDialog,
        problemImages = viewModel.problemImages,
        //selectedImage = viewModel.selectedProblemImage,
        onDismiss = { viewModel.closeProblemDialog() },
        //onRetake = { viewModel.retakeImages() },
        //onShowImage = { viewModel.showProblemImage(it) },
        onRetakeSpecificImage = { uri, index -> viewModel.retakeSpecificImage(uri) }
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        val scannerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult(),
            onResult = { scanner ->
                viewModel.handleScanResult(scanner)

            }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(viewModel.imageUris) { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = null,
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                // Barkod Analiz butonu
                Button(
                    modifier = Modifier
                        .width(160.dp)
                        .height(48.dp),
                    shape = RoundedCornerShape(3.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    onClick = {


                        viewModel.analyzeAllImages(context)


                    }
                ) {
                    Text(
                        modifier = Modifier,
                        textAlign = TextAlign.Center,
                        text = "Barkodları Analiz Et"
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    modifier = Modifier
                        .width(160.dp)
                        .height(48.dp),
                    shape = RoundedCornerShape(3.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    onClick = {
                        CoroutineScope(Dispatchers.Main).launch {
                            // Saving JSON to file
                            val path = JsonHelper().saveJsonToFile(
                                context = context,
                                fileName = "TestData",
                                jsonData = Json.encodeToString(testViewModel.data)
                            )
                            // Add result JSON file
                            JsonHelper().saveJsonToFile(
                                context = context,
                                fileName = "TestData_result",
                                jsonData = Json.encodeToString(testViewModel.resultData)
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
                        }
                    }
                ) {
                    Text(
                        modifier = Modifier,
                        textAlign = TextAlign.Center,
                        text = "Test Anketi Ekle"
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    modifier = Modifier
                        .width(160.dp)
                        .height(48.dp),
                    shape = RoundedCornerShape(3.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    onClick = {
                        viewModel.scanner.getStartScanIntent(context as Activity)
                            .addOnSuccessListener {
                                scannerLauncher.launch(
                                    IntentSenderRequest.Builder(it).build()
                                )
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show()
                            }
                    }
                ) {
                    Text(
                        modifier = Modifier,
                        textAlign = TextAlign.Center,
                        text = "Belge Tara"
                    )
                }
            }
        }
    }
}