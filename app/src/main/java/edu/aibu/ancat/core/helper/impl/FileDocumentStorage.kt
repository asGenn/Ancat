package edu.aibu.ancat.core.helper.impl

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import edu.aibu.ancat.core.helper.DocumentStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

/**
 * Dosya sistemine PDF belge kaydeden somut implementasyon
 * SOLID - SRP: Sadece belge saklama sorumluluğuna sahip
 */
class FileDocumentStorage @Inject constructor() : DocumentStorage {
    
    override suspend fun saveDocument(context: Context, document: PdfDocument, documentName: String) {
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)!!
        val file = File(path, "$documentName.pdf")
        
        try {
            withContext(Dispatchers.IO) {
                FileOutputStream(file).use { outputStream ->
                    document.writeTo(outputStream)
                }
            }
            Toast.makeText(context, "PDF Oluşturuldu :)", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(context, "PDF oluşturulamadı!", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        } finally {
            document.close()
        }
    }
} 