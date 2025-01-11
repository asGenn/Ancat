package edu.aibu.ancat.ui.views.home_screen

import android.app.Activity.RESULT_OK
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_JPEG
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_PDF
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.SCANNER_MODE_FULL
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AnalyzeScreenViewModel @Inject constructor() : ViewModel() {

    private val option = GmsDocumentScannerOptions.Builder()
            .setScannerMode(SCANNER_MODE_FULL)
            .setGalleryImportAllowed(true)
            .setResultFormats(RESULT_FORMAT_JPEG, RESULT_FORMAT_PDF)
            .build()

    val scanner = GmsDocumentScanning.getClient(option)

    private val _imageUris  = mutableStateOf<List<Uri>>(emptyList())
    val imageUris: List<Uri> get() = _imageUris.value

    fun handleScanResult(scanner: ActivityResult) {

        if (scanner.resultCode == RESULT_OK) {
            val result = GmsDocumentScanningResult.fromActivityResultIntent(scanner.data)
            if (result != null) {
                _imageUris.value = result.pages?.map { it.imageUri } ?: emptyList()
            }
        }
    }

}
