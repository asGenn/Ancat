package edu.aibu.ancat.core.helper

import android.content.Context
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.Page
import android.util.Log
import edu.aibu.ancat.core.renderer.DocumentRenderer
import edu.aibu.ancat.core.renderer.survey_drawings.utils.DrawingMeasurerHandler
import edu.aibu.ancat.core.renderer.survey_drawings.utils.PagedQuestionHandler
import edu.aibu.ancat.data.model.Question
import edu.aibu.ancat.data.model.SurveyItem
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
    private val pagedQuestionHandler: PagedQuestionHandler,
    private val documentStorage: DocumentStorage,
    private val documentFactory: DocumentFactory
) {
    private lateinit var pdfDocument: PdfDocument
    private lateinit var page: Page
    private lateinit var canvas: Canvas

    private var pageNumber = 1
    private var cursor = START_CURSOR

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
            processItem(context, it, index, jsonName)
        }
        
        documentFactory.finishPage(pdfDocument, page)
        
        val survey = data[0].questions[0] as Question.SurveyTitle
        val title = survey.title

        documentStorage.saveDocument(context, pdfDocument, title)
    }
    
    /**
     * Bir anket öğesini işler ve canvas'a çizer
     * @param item İşlenecek anket öğesi
     */
    private suspend fun processItem(context: Context, item: SurveyItem, surveyIndex: Int, jsonName: String) {
        val needPageBreak = drawingMeasurerHandler.handlePageBreakIfNeeded(item.questions, cursor)
        var splitQuest = false
        var splitList = emptyList<SurveyItem>()

        if (needPageBreak) {
            splitList = pagedQuestionHandler.splitQuestion(item, cursor)
            splitQuest = true
            
            if (drawingMeasurerHandler.handlePageBreakIfNeeded(splitList[0].questions, cursor)) {
                documentFactory.finishPage(pdfDocument, page)
                page = documentFactory.createPage(pdfDocument, ++pageNumber)
                canvas = page.canvas
                cursor = START_CURSOR
            }
        }
        
        cursor = if (splitQuest) {
            processSplitItems(context, splitList, surveyIndex, jsonName)
        } else {
            documentRenderer.renderDocument(
                canvas = canvas,
                cursor = cursor + MARGIN * 2,
                data = item,
                jsonFileName = jsonName,
                surveyIndex = surveyIndex,
                context = context
            )
        }
        
        Log.d("Cursor ->", "$cursor")
    }
    
    /**
     * Sayfalara bölünmüş anket öğelerini işler
     * @param splitList Sayfalara bölünmüş anket öğeleri listesi
     * @return Güncellenmiş cursor pozisyonu
     */
    private suspend fun processSplitItems(context: Context, splitList: List<SurveyItem>, surveyIndex: Int, jsonName: String): Float {
        splitList.forEachIndexed { index, spl ->
            if (index != 0) {
                documentFactory.finishPage(pdfDocument, page)
                page = documentFactory.createPage(pdfDocument, ++pageNumber)
                canvas = page.canvas
                cursor = START_CURSOR
            }
            cursor = documentRenderer.renderDocument(
                canvas = canvas,
                cursor = cursor + MARGIN * 2,
                data = spl,
                jsonFileName = jsonName,
                surveyIndex = surveyIndex,
                context = context
            )
        }
        return cursor
    }
}