package edu.aibu.ancat.ui.views.home_screen

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import edu.aibu.ancat.core.helper.imageProccess.MLKitBarcodeScanner
import kotlin.collections.plusAssign

@Composable
fun AnalyzeScreen() {
    val viewModel: AnalyzeScreenViewModel = hiltViewModel()
    val context = LocalContext.current
    val barcodeResult = remember { mutableStateOf<String?>(null) }
    val mlKitBarcodeScanner = remember { MLKitBarcodeScanner() }

    LaunchedEffect(viewModel.analyzeStatus.value) {
        val currentStatus = viewModel.analyzeStatus.value
        if (currentStatus.isNotEmpty()) {
            Toast.makeText(context, currentStatus, Toast.LENGTH_SHORT).show()
        }
    }

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
                        // MediaStore'a kaydet
                        if (viewModel.imageUris.isNotEmpty()) {
                            viewModel.saveScannedImagesToMediaStore(context)
                            Toast.makeText(context, "Görüntüler kaydedildi", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text(
                        modifier = Modifier,
                        textAlign = TextAlign.Center,
                        text = "MediaStore'a Kaydet"
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