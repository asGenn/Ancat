import android.net.Uri

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import edu.aibu.ancat.ui.views.camera_screen.CameraScreenViewModel.ProblemImage


@Composable
fun ProblemImagesDialog(
    showDialog: Boolean,
    problemImages: List<ProblemImage>,
    //selectedImage: Uri?,
    onDismiss: () -> Unit,
    //onRetake: () -> Unit,
    //onShowImage: (Uri) -> Unit,
    onRetakeSpecificImage: (Uri, Int) -> Unit
) {
    if (showDialog && problemImages.isNotEmpty()) {
        var currentIndex = remember { mutableStateOf(0) }
        val currentProblem = problemImages[currentIndex.value]

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Resim ${currentIndex.value + 1}/${problemImages.size} - QR Kod Problemi") },
            text = {
                Column {
                    Text(
                        text = "Problem: ${currentProblem.reason}",
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Problemli resim önizleme
                    AsyncImage(
                        model = currentProblem.uri,
                        contentDescription = "Problemli Resim",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .padding(vertical = 8.dp),
                        contentScale = ContentScale.Fit
                    )

                    // İleri/geri butonları
                    if (problemImages.size > 1) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = {
                                    if (currentIndex.value > 0)
                                        currentIndex.value--
                                },
                                enabled = currentIndex.value > 0
                            ) {
                                Text("Önceki")
                            }

                            Button(
                                onClick = {
                                    if (currentIndex.value < problemImages.size - 1)
                                        currentIndex.value++
                                },
                                enabled = currentIndex.value < problemImages.size - 1
                            ) {
                                Text("Sonraki")
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { onRetakeSpecificImage(currentProblem.uri, currentProblem.index) }
                    ) {
                        Text("Bu Resmi Yeniden Çek")
                    }

                    Button(
                        onClick = {
                            if (currentIndex.value < problemImages.size - 1) {
                                currentIndex.value++
                            } else {
                                onDismiss()
                            }
                        }
                    ) {
                        Text(if (currentIndex.value < problemImages.size - 1) "Atla" else "Bitir")
                    }
                }
            },

        )
    }
}