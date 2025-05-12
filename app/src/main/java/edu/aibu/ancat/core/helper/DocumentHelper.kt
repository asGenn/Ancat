package edu.aibu.ancat.core.helper

import android.content.Context
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.Page
import android.util.Log
import edu.aibu.ancat.core.renderer.DocumentRenderer
import edu.aibu.ancat.core.renderer.survey_drawings.utils.DrawingMeasurerHandler
import edu.aibu.ancat.data.model.Question
import edu.aibu.ancat.data.model.SurveyItem
import edu.aibu.ancat.utils.DocumentConstants.FIRST_PAGE
import edu.aibu.ancat.utils.DocumentConstants.MARGIN
import edu.aibu.ancat.utils.DocumentConstants.START_CURSOR
import javax.inject.Inject

/**
 * PDF dokümanları oluşturma ve yönetme işlemlerinden sorumlu yardımcı sınıf
 * SOLID - SRP: Sınıf, temel olarak dokümantasyon oluşturma sorumluluğunu yönetir
 * SOLID - DIP: Sınıf, somut implementasyonlar yerine soyutlamalara bağlıdır
 */
class DocumentHelper @Inject constructor(
    private val documentRenderer: DocumentRenderer,
    private val drawingMeasurerHandler: DrawingMeasurerHandler,
    private val documentStorage: DocumentStorage,
    private val documentFactory: DocumentFactory
) {
    private lateinit var pdfDocument: PdfDocument
    private lateinit var page: Page
    private lateinit var canvas: Canvas

    private var pageNumber = 1
    private var firstSurveySection = 0
    private var fistSurveyQuestion = 0
    private var tempIndex = 0
    private var cursor = FIRST_PAGE

    /**
     * PDF dokümanı oluşturur ve kaydeder
     * @param context Uygulama bağlamı
     * @param data Anket verileri listesi
     */
    suspend fun createDocument(context: Context, data: List<SurveyItem>, jsonName: String) {
        pdfDocument = documentFactory.createPdfDocument()
        page = documentFactory.createPage(pdfDocument, pageNumber)
        canvas = page.canvas

        data.forEachIndexed { index, it ->
            tempIndex = data[index].questions.lastIndex
            processItem(context, it, index, jsonName)
            cursor += MARGIN * 2
        }
        documentRenderer.renderQRCode(canvas, jsonName, pageNumber, firstSurveySection, fistSurveyQuestion, data.lastIndex, data[data.lastIndex].questions.lastIndex)
        documentFactory.finishPage(pdfDocument, page)
        val survey = data[0].questions[0] as Question.SurveyTitle
        val title = survey.title
        documentStorage.saveDocument(context, pdfDocument, title)
    }

    /**
     * Bir anket öğesini işler ve canvas'a çizer
     * @param item İşlenecek anket öğesi
     */
    private suspend fun processItem(
        context: Context,
        item: SurveyItem,
        surveyIndex: Int,
        jsonName: String
    ) {
        var needPageBreak: Boolean
        item.questions.forEachIndexed { index, it ->
            needPageBreak = drawingMeasurerHandler.handlePageBreakIfNeeded(it, cursor)
            Log.d("Cursor ->", "$cursor")

            if (needPageBreak) {
                val adjustedSurveyIndex = if (index == 0) surveyIndex - 1 else surveyIndex
                val adjustedTempIndex = if (index == 0) tempIndex else index - 1
                documentRenderer.renderQRCode(canvas, jsonName, pageNumber, firstSurveySection, fistSurveyQuestion, adjustedSurveyIndex, adjustedTempIndex)
                documentFactory.finishPage(pdfDocument, page)
                page = documentFactory.createPage(pdfDocument, ++pageNumber)
                canvas = page.canvas
                cursor = START_CURSOR
                firstSurveySection = surveyIndex
                fistSurveyQuestion = index
            }

            cursor = documentRenderer.renderDocument(
                canvas = canvas,
                cursor = cursor,
                data = it,
                type = item.type,
                index = index,
                jsonFileName = jsonName,
                surveyIndex = surveyIndex,
                context = context
            )
        }
    }
}