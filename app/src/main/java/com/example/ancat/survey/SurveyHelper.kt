package com.example.ancat.survey

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

class SurveyHelper {
    fun createPdf(context: Context) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)

        val canvas: Canvas = page.canvas
        val paint = Paint().apply {
            textSize = 12f
        }

        var cursorPos = 30f //Default

        /**
         * ÖRNEK DATA
         */
        val data = """
            {
              "1": {
                "type": "1",
                "question": "Adın ne?"
              },
              "2": {
                "type": "1",
                "question": "Soyadın ne?"
              },
              "3": {
                "type": "1",
                "question": "ne?"
              }
              
            }
        """.trimIndent()

        /**
         * ÖRNEK DATA BİTİŞ
         */


        val jsonObject = JSONObject(data)

        // JSON içindeki anahtarları dolaşalım
        for (key in jsonObject.keys()) {
            val questionObject = jsonObject.getJSONObject(key)
            val type = questionObject.getString("type")
            val question = questionObject.getString("question")

            cursorPos = when (type) {
                "1" -> QuestionsHelper().ratingQuestion(canvas, paint, question, cursorPos)
                else -> return
            }
        }

        pdfDocument.finishPage(page)
        savePdf(context, pdfDocument, "anket")

    }

    private fun savePdf(context: Context, pdfDocument: PdfDocument, pdfName: String) {

        // PDF dosyasını kaydet
        val filePath = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
            "$pdfName.pdf"
        )

        try {
            val outputStream = FileOutputStream(filePath)
            pdfDocument.writeTo(outputStream)
            outputStream.close()
            Toast.makeText(context, "PDF oluşturuldu: ${filePath.absolutePath}", Toast.LENGTH_SHORT)
                .show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            pdfDocument.close()
        }
    }
}