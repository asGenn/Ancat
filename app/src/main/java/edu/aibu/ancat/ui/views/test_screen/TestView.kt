package edu.aibu.ancat.ui.views.test_screen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.view.LifecycleCameraController

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.aibu.ancat.core.helper.JsonHelper
import edu.aibu.ancat.core.helper.camera.CustomCamera
import edu.aibu.ancat.core.helper.imageProccess.MLKitBarcodeScanner
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
    val barcodeResult = remember { mutableStateOf<String?>(null) }
    val mlKitBarcodeScanner = remember { MLKitBarcodeScanner() }

    // Kamera görünürlüğünü kontrol eden durum
    var showCamera by remember { mutableStateOf(false) }

    // Kamera controller'ı
    val cameraController = remember { LifecycleCameraController(context) }

    // Kamera izinlerini kontrol et
    val camera = CustomCamera()
    camera.checkCameraPermissions(context)

    // Galeriden resim seçmek için launcher
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Seçilen resmi ML Kit ile işle
            Toast.makeText(context, "Görüntü işleniyor...", Toast.LENGTH_SHORT).show()
            mlKitBarcodeScanner.processImageFromUri(
                context = context,
                imageUri = it,
                result = barcodeResult,
                onComplete = { success, barcodes, savedImagePath ->
                    if (success) {
                        Toast.makeText(
                            context,
                            "${barcodes.size} adet barkod tespit edildi",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            "Barkod tarama başarısız",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (showCamera) {
            // Kamera önizleme ekranı gösteriliyor
            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1f)) {
                    // Kamera önizleme bileşeni
                    CameraPreview(
                        modifier = Modifier.fillMaxSize(),
                        controller = cameraController.apply {
                            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                        }
                    )
                }

                // Kamera kapatma butonu
                Button(
                    onClick = { showCamera = false },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Kamerayı Kapat"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Kamerayı Kapat")
                }
            }
        } else {
            // Normal görünüm gösteriliyor
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .align(Alignment.Center)
            ) {
                Button(
                    onClick = {
                        CoroutineScope(Dispatchers.Main).launch {
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

                // Kamerayı açma butonu
                Button(
                    onClick = { showCamera = true }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Kamerayı Aç",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Kamerayı Aç")
                }

                // Barkod tarama butonu
                Button(
                    onClick = { imagePicker.launch("image/*") }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Barkod Tara",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Barkod Tara")
                }

                // Sonuç gösterimi
                barcodeResult.value?.let {
                    Text("Barkod Sonucu: $it")
                }
            }
        }
    }
}