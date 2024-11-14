package com.example.ancat.core.helper.survey

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.Page
import android.os.Environment
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream

class DocumentHelper {

    fun readJsonFromFilePath(filePath: String): String? {
        return try {
            val file = File(filePath)
            file.readText()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun createPage(pdfDocument: PdfDocument, pageNum: Int): Page {
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNum).create()
        return pdfDocument.startPage(pageInfo)
    }

    fun savePdf(context: Context, pdfDocument: PdfDocument, pdfName: String) {

        val filePath = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
            "$pdfName.pdf"
        )

        try {
            FileOutputStream(filePath).use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }
            Toast.makeText(context, "PDF oluşturuldu: ${filePath.absolutePath}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            pdfDocument.close()
        }
    }
}