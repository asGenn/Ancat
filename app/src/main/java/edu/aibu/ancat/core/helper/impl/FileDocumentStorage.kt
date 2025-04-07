package edu.aibu.ancat.core.helper.impl

import android.content.ContentValues
import android.content.Context
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import edu.aibu.ancat.core.helper.DocumentStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStream
import javax.inject.Inject

/**
 * Dosya sistemine PDF belge kaydeden somut implementasyon
 * SOLID - SRP: Sadece belge saklama sorumluluğuna sahip
 */
class FileDocumentStorage @Inject constructor() : DocumentStorage {

    @RequiresApi(Build.VERSION_CODES.Q)
    override suspend fun saveDocument(context: Context, document: PdfDocument, documentName: String) {
        // Scoped Storage için MediaStore API kullanımı
        val contentResolver = context.contentResolver

        // MediaStore için ContentValues oluşturuyoruz
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$documentName.pdf") // Dosya adı
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf") // Dosya türü
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Documents") // Konum, "Documents" dizinine kaydedilecek
        }

        try {
            // Android 10 ve sonrası için MediaStore üzerinden dosya kaydetme
            val uri = contentResolver.insert(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL), contentValues)

            // URI null kontrolü
            if (uri != null) {
                // OutputStream ile veriyi yazma işlemi
                withContext(Dispatchers.IO) {
                    contentResolver.openOutputStream(uri)?.use { outputStream: OutputStream ->
                        // PDF dosyasını outputStream'e yazıyoruz
                        document.writeTo(outputStream)
                    }
                }

                // Başarı mesajı
                Toast.makeText(context, "PDF Oluşturuldu ve Kaydedildi :)", Toast.LENGTH_LONG).show()
            } else {
                // Eğer URI elde edilemezse hata mesajı
                Toast.makeText(context, "Dosya kaydedilemedi.", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            // Hata durumunda loglama
            Toast.makeText(context, "PDF oluşturulamadı!", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        } finally {
            // PDF belgesini kapatma
            document.close()
        }
    }
}
