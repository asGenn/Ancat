package com.example.ancat.ui.views.home_screen

import android.app.Activity.RESULT_OK
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.documentscanner.GmsDocumentScanner
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_JPEG
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_PDF
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.SCANNER_MODE_FULL
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class AnalyzeScreenViewModel @Inject constructor() : ViewModel() {

    private val _option = GmsDocumentScannerOptions.Builder()
            .setScannerMode(SCANNER_MODE_FULL)
            .setGalleryImportAllowed(true)
            .setResultFormats(RESULT_FORMAT_JPEG, RESULT_FORMAT_PDF)
            .build()

    private val option: GmsDocumentScannerOptions get() = _option

    private val _scanner = GmsDocumentScanning.getClient(option)
    val scanner: GmsDocumentScanner get() = _scanner

    private var _imageUris  = mutableStateOf<List<Uri>>(emptyList())
    val imageUris: List<Uri> get() = _imageUris.value

    /**
     *
     * Temp save location
     *
     */

    private val filePath = File(
        Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOCUMENTS
        ), "scan.pdf"
    )

    private val fos = FileOutputStream(filePath)

    fun handleScanResult(scanner: ActivityResult, context: Context) {
        if (scanner.resultCode == RESULT_OK) {
            val result = GmsDocumentScanningResult.fromActivityResultIntent(scanner.data)
            if (result != null) {
                _imageUris.value = result.pages?.map { it.imageUri } ?: emptyList()
                result.pdf?.let { pdf ->
                    val contentResolver = context.contentResolver
                    contentResolver.openInputStream(pdf.uri)?.use { image ->
                        image.copyTo(fos)
                    }
                }
            }
        }
    }

}
