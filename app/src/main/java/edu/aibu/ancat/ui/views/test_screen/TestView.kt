package edu.aibu.ancat.ui.views.test_screen

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.aibu.ancat.core.helper.JsonHelper
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
                        // Barkodları işleme
                        for (barcode in barcodes) {
                            // 3) Bounding box’a zaten sahipsiniz:
                            val rect = barcode.boundingBox
                            // 4) Gömülü veriyi alalım:
                            val rawValue = barcode.rawValue          // Ham veri (String?)
                            val displayValue = barcode.displayValue  // Kullanıcıya gösterilebilir versiyon

                            // 5) İsterseniz format bilgisi de alabilirsiniz:
                            val format = barcode.format  // örn. Barcode.FORMAT_QR_CODE

                            // Örnek log:
                            Log.d("MLKit", "Bulunan barkod: format=$format, data=$rawValue")
                            println("Bulunan barkod: format=$format, data=$rawValue")
                        }
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