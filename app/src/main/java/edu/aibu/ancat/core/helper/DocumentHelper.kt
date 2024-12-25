package edu.aibu.ancat.core.helper

import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.Page
import android.graphics.pdf.PdfDocument.PageInfo.Builder
import android.os.Environment
import edu.aibu.ancat.core.renderer.DocumentRenderer
import edu.aibu.ancat.core.renderer.survey_drawings.utils.DrawingMeasurer
import edu.aibu.ancat.data.model.SurveyItem
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
    private val drawingMeasurer: DrawingMeasurer
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
            e.printStackTrace()
        } finally {
            pdfDocument.close()
        }

    }

    suspend fun createDocument(data: List<SurveyItem>) {
        pdfDocument = PdfDocument()
        page = createPage()
        canvas = page.canvas
        data.forEach {
            val sum = drawingMeasurer.handlePageBreakIfNeeded(it.questions, cursor)
            var splitQuest = false
            var splitList = emptyList<SurveyItem>()

            if (sum) {
                savePage(page)
                page = createPage()
                canvas = page.canvas
                cursor = START_CURSOR

                if (drawingMeasurer.handlePageBreakIfNeeded(it.questions, cursor)) {
                    splitQuest = true
                    splitList = drawingMeasurer.splitQuestion(it)
                }
            }
            cursor = if (splitQuest) {
                splitList.forEach {spl ->
                    cursor = START_CURSOR
                    cursor = documentRenderer.renderDocument(canvas, cursor + 20, spl)
                    savePage(page)
                    page = createPage()
                    canvas = page.canvas
                }
                cursor
            } else {
                documentRenderer.renderDocument(canvas, cursor + 20, it)
            }
        }
        savePage(page)
        saveDocument(data[0].title)
    }

}