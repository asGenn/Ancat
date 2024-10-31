package com.example.ancat.survey

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import com.example.ancat.data.MultipleChoiceQuest
import org.json.JSONArray
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
        val paintTitle = Paint().apply {
            textSize = 16f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        /**
         * ÖRNEK DATA
         */
        val data = """
            [
                {
                    "type": "0",
                    "title": "Anket Başlığı",
                    "questions": [
                        "Açıklama 1",
                        "Açıklama 2",
                        "Açıklama 3"
                    ]
                },
                {
                    "type": "1",
                    "title": "Soru 1 Başlığı",
                    "questions": [
                        "Soru 1",
                        "Soru 2",
                        "Soru 3",
                        "Soru 4",
                        "Soru 5",
                        "Soru 6"
                    ]
                },
                {
                    "type": "2",
                    "title": "Soru 2 Başlığı",
                    "questions": [
                        {
                            question: "Soru1",
                            options: [
                                "Şık1",
                                "Şık2"
                            ]
                        },
                        {
                            question: "Soru2",
                            options: [
                                "Şık1",
                                "Şık2",
                                "Şık3"
                            ]
                        }
                    ]
                },
                {
                    "type": "1",
                    "title": "Soru 1.1 Başlığı",
                    "questions": [
                        "Soru 1",
                        "Soru 2",
                        "Soru 3",
                        "Soru 4",
                        "Soru 5",
                        "Soru 6"
                    ]
                }
            ]
        """.trimIndent()

        /**
         * ÖRNEK DATA BİTİŞ
         */


        val jsonArray = JSONArray(data)
        val questionsHelper = QuestionsHelper()
        var cursorPos = 0f

        // JSON içindeki anahtarları dolaşalım
        for (i in 0 until jsonArray.length()) {
            val questionObject = jsonArray.getJSONObject(i)
            val type = questionObject.getString("type")
            val title = questionObject.getString("title")
            val questions = questionObject.getJSONArray("questions")

            cursorPos = when (type) {
                "0" -> {
                    val commitList = List(questions.length()) { questions.getString(it) }
                    val cursorPosition = questionsHelper.surveyTitleCommit(canvas, paint, paintTitle, title, commitList)
                    questionsHelper.surveyFrame(canvas, paint, cursorPosition)
                }

                "1" -> {
                    val questionList = List(questions.length()) { questions.getString(it) }
                    questionsHelper.ratingQuestion(canvas, paint, title, questionList, cursorPos)
                }

                "2" -> {
                    val questionList = List(questions.length()) {
                        val questionObj = questions.getJSONObject(it)
                        val question = questionObj.getString("question")
                        val options = List(questionObj.getJSONArray("options").length()) { index ->
                            questionObj.getJSONArray("options").getString(index)
                        }
                        MultipleChoiceQuest(question, options)
                    }
                    questionsHelper.multipleChoiceQuestion(canvas, paint, title, questionList, cursorPos)

                }

                else -> {
                    cursorPos
                }
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