package edu.aibu.ancat.ui.views.test_screen

import android.net.Uri
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
import edu.aibu.ancat.core.helper.imageProccess.MLKitBarcodeScanner
import edu.aibu.ancat.ui.views.create_screen.CreateScreenViewModel



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
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { /* Anket oluşturma kodu */ }
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

