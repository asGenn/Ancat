package edu.aibu.ancat.ui.views.home_screen

import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
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
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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

    // MediaStore'a taranan görüntüleri kaydetme fonksiyonu
    fun saveScannedImagesToMediaStore(context: Context) {
        _imageUris.value.forEach { uri ->
            saveImageToMediaStore(context, uri)
        }
    }
    
    // Bir Uri'yi MediaStore'a kaydetme
    private fun saveImageToMediaStore(context: Context, imageUri: Uri) {
        val contentResolver = context.contentResolver
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "ANCAT_SCAN_$timestamp.jpg"
        
        val inputStream: InputStream? = contentResolver.openInputStream(imageUri)
        if (inputStream != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Ancat")
                }
                
                val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                uri?.let { outputUri ->
                    val outputStream: OutputStream? = contentResolver.openOutputStream(outputUri)
                    outputStream?.use { output ->
                        inputStream.copyTo(output)
                    }
                }
            } else {
                // API 29'dan düşük sürümler için klasik dosya sistemini kullan
                val imagesDir = File(context.getExternalFilesDir(null), "Ancat")
                if (!imagesDir.exists()) imagesDir.mkdirs()
                
                val file = File(imagesDir, fileName)
                val outputStream = FileOutputStream(file)
                outputStream.use { output ->
                    inputStream.copyTo(output)
                }
                
                // MediaStore'a ekle
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.DATA, file.absolutePath)
                }
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            }
            inputStream.close()
        }
    }
    
    // MediaStore'dan görüntüleri al
    fun getImagesFromMediaStore(context: Context): List<Uri> {
        val images = mutableListOf<Uri>()
        val contentResolver = context.contentResolver
        
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME
        )
        
        val selection = "${MediaStore.Images.Media.DISPLAY_NAME} LIKE ?"
        val selectionArgs = arrayOf("ANCAT_SCAN_%")
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
        
        val query = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )
        
        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri = Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id.toString()
                )
                images.add(contentUri)
            }
        }
        
        return images
    }
}
