package edu.aibu.ancat.ui.views.home_screen

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage

@Composable
fun AnalyzeScreen() {
    val viewModel: AnalyzeScreenViewModel = hiltViewModel()
    val context = LocalContext.current
    
    // MediaStore'dan görüntüleri yükle
    LaunchedEffect(Unit) {
        // Uygulama başladığında MediaStore'dan kayıtlı görüntüleri almak isterseniz
        // bu kısmı etkinleştirebilirsiniz.
        // val mediaStoreImages = viewModel.getImagesFromMediaStore(context)
        // if (mediaStoreImages.isNotEmpty()) {
        //    viewModel.loadImagesFromMediaStore(mediaStoreImages)
        // }
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
                columns = GridCells.Fixed(2), // Yatayda 2 sütun olacak
                modifier = Modifier.fillMaxSize(), // Tüm kullanılabilir alanı kaplar
                verticalArrangement = Arrangement.spacedBy(8.dp), // Kartlar arası dikey boşluk
                horizontalArrangement = Arrangement.spacedBy(8.dp), // Kartlar arası yatay boşluk
                contentPadding = androidx.compose.foundation.layout.PaddingValues(8.dp)
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
