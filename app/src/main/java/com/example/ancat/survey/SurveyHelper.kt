package com.example.ancat.survey

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.Page
import android.os.Environment
import android.widget.Toast
import com.example.ancat.data.MultipleChoiceQuest
import com.example.ancat.data.jsonData
import org.json.JSONArray
import java.io.File
import java.io.FileOutputStream

class SurveyHelper {

    private fun callQuestSize(size: Int): Float = size * 20f

    private fun createPage(pdfDocument: PdfDocument, pageNum: Int): Page {
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNum).create()
        return pdfDocument.startPage(pageInfo)
    }

    fun createPdf(context: Context, data: String) {
        val pdfDocument = PdfDocument()
        var pageNum = 1
        var page = createPage(pdfDocument, pageNum)
        var canvas: Canvas = page.canvas

        val paint = Paint().apply {
            textSize = 12f
        }
        val paintTitle = Paint().apply {
            textSize = 16f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val jsonArray = JSONArray(data)
        val questionsHelper = QuestionsHelper()
        var cursorPos = 30f

        for (i in 0 until jsonArray.length()) {
            val questionObject = jsonArray.getJSONObject(i)
            val type = questionObject.getString("type")
            val title = questionObject.getString("title")
            val questions = questionObject.getJSONArray("questions")

            cursorPos = when (type) {
                "0" -> {
                    val commitList = List(questions.length()) {val questionObj = questions.getJSONObject(it)
                        questionObj.getString("question")}
                    val cursorPosition = questionsHelper.surveyTitleCommit(canvas, paint, paintTitle, title, commitList)
                    questionsHelper.surveyFrame(canvas, paint, cursorPosition)
                }

                "1" -> {
                    val questionList = List(questions.length()) { questions.getString(it) }
                    if (callQuestSize(questionList.size) + cursorPos > 792f) {
                        pdfDocument.finishPage(page)
                        page = createPage(pdfDocument, ++pageNum)
                        canvas = page.canvas
                        cursorPos = questionsHelper.surveyFrame(canvas, paint, 30f)
                    }
                    questionsHelper.ratingQuestion(canvas, paint, title, questionList, cursorPos)
                }

                "2" -> {
                    var optSize = 0
                    val questionList = List(questions.length()) {
                        val questionObj = questions.getJSONObject(it)
                        val question = questionObj.getString("question")
                        val options = List(questionObj.getJSONArray("options").length()) { index ->
                            optSize++
                            questionObj.getJSONArray("options").getString(index)
                        }
                        MultipleChoiceQuest(question, options)
                    }
                    if (callQuestSize(optSize) + cursorPos > 812f) {
                        pdfDocument.finishPage(page)
                        page = createPage(pdfDocument, ++pageNum)
                        canvas = page.canvas
                        cursorPos = 30f
                    }
                    questionsHelper.multipleChoiceQuestion(canvas, paint, title, questionList, cursorPos)
                }

                else -> cursorPos
            }
        }

        pdfDocument.finishPage(page)
        savePdf(context, pdfDocument, "anket")
    }

    private fun savePdf(context: Context, pdfDocument: PdfDocument, pdfName: String) {
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
