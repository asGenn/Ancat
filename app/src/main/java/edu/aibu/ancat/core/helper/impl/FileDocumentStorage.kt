package edu.aibu.ancat.core.helper.impl

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.util.Log
import android.widget.Toast
import edu.aibu.ancat.core.helper.DocumentStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class FileDocumentStorage @Inject constructor() : DocumentStorage {

    override suspend fun saveDocument(context: Context, document: PdfDocument, documentName: String) {
        // Uygulama özel dizinini alın
        val directory = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)

        // Dizin null ise logla ve çık
        if (directory == null) {
            Log.e("FileDocumentStorage", "Uygulama özel dizini alınamadı!")
            Toast.makeText(context, "PDF oluşturulamadı!", Toast.LENGTH_LONG).show()
            return
        }
        // Dizin yoksa oluştur
        if (!directory.exists()) {
            directory.mkdirs()
        }


        val file = File(directory, "$documentName.pdf")

        try {
            withContext(Dispatchers.IO) {
                FileOutputStream(file).use { outputStream ->
                    document.writeTo(outputStream)
                }
            }
            Toast.makeText(context, "PDF Oluşturuldu :)", Toast.LENGTH_LONG).show()
            // Dosyanın kaydedildiği yolu loglayın
            Log.d("FileDocumentStorage", "PDF dosyası kaydedildi: ${file.absolutePath}")

        } catch (e: Exception) {
            Toast.makeText(context, "PDF oluşturulamadı!", Toast.LENGTH_LONG).show()
            Log.e("FileDocumentStorage", "PDF kaydedilirken hata oluştu!", e)
        } finally {
            document.close()
        }
    }
}