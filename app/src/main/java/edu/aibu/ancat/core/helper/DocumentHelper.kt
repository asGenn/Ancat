package edu.aibu.ancat.core.helper

import android.content.Context
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.Page
import android.graphics.pdf.PdfDocument.PageInfo.Builder
import android.os.Environment
import android.util.Log
import android.widget.Toast
import edu.aibu.ancat.core.renderer.DocumentRenderer
import edu.aibu.ancat.core.renderer.survey_drawings.utils.DrawingMeasurerHandler
import edu.aibu.ancat.core.renderer.survey_drawings.utils.PagedQuestionHandler
import edu.aibu.ancat.data.model.Question
import edu.aibu.ancat.data.model.SurveyItem
import edu.aibu.ancat.utils.DocumentConstants.MARGIN
import edu.aibu.ancat.utils.DocumentConstants.PAGE_HEIGHT
import edu.aibu.ancat.utils.DocumentConstants.PAGE_WIDTH
import edu.aibu.ancat.utils.DocumentConstants.START_CURSOR
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class DocumentHelper @Inject constructor(
    private val documentRenderer: DocumentRenderer,
    private val drawingMeasurerHandler: DrawingMeasurerHandler,
    private val pagedQuestionHandler: PagedQuestionHandler
) {
    private lateinit var pdfDocument: PdfDocument
    private lateinit var page: Page
    private lateinit var canvas: Canvas

    private var pageNumber = 1
    private var cursor = START_CURSOR

    private suspend fun createPage(): Page {
        return withContext(Dispatchers.IO) {
            val pageInfo = Builder(PAGE_WIDTH, PAGE_HEIGHT, ++pageNumber).create()
            pdfDocument.startPage(pageInfo)
        }
    }

    private fun savePage(page: Page) {
        pdfDocument.finishPage(page)
    }

    private suspend fun saveDocument(
        context: Context,
        documentName: String
    ) {
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)!!
        val fos = File(path, "$documentName.pdf")
        try {
            withContext(Dispatchers.IO) {
                FileOutputStream(fos).use { outputStream ->
                    pdfDocument.writeTo(outputStream)
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "PDF oluşturulamadı!", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        } finally {
            Toast.makeText(context, "PDF Oluşturuldu :)", Toast.LENGTH_LONG).show()
            pdfDocument.close()
        }

    }

    suspend fun createDocument(context: Context, data: List<SurveyItem>) {
        pdfDocument = PdfDocument()
        page = createPage()
        canvas = page.canvas
        data.forEach {
            val sum = drawingMeasurerHandler.handlePageBreakIfNeeded(it.questions, cursor)
            var splitQuest = false
            var splitList = emptyList<SurveyItem>()

            if (sum) {
                splitList = pagedQuestionHandler.splitQuestion(it, cursor)
                splitQuest = true
                if (drawingMeasurerHandler.handlePageBreakIfNeeded(splitList[0].questions, cursor)) {
                    savePage(page)
                    page = createPage()
                    canvas = page.canvas
                    cursor = START_CURSOR
                }
            }
            cursor = if (splitQuest) {
                splitList.forEachIndexed {index, spl ->
                    if (index != 0) {
                        savePage(page)
                        page = createPage()
                        canvas = page.canvas
                        cursor = START_CURSOR
                    }
                    cursor = documentRenderer.renderDocument(canvas, cursor + MARGIN*2, spl)
                }
                cursor
            } else {
                documentRenderer.renderDocument(canvas, cursor + MARGIN*2, it)
            }
            Log.d("Cursor ->", "$cursor")
        }
        savePage(page)
        val survey = data[0].questions[0] as Question.SurveyTitle
        val title = survey.title

        saveDocument(context, title)
    }

}