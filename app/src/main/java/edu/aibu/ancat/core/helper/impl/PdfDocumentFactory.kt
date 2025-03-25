package edu.aibu.ancat.core.helper.impl

import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.Page
import android.graphics.pdf.PdfDocument.PageInfo.Builder
import edu.aibu.ancat.core.helper.DocumentFactory
import edu.aibu.ancat.utils.DocumentConstants.PAGE_HEIGHT
import edu.aibu.ancat.utils.DocumentConstants.PAGE_WIDTH
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * PDF doküman fabrikası somut implementasyonu
 * SOLID - SRP: Sadece PDF doküman ve sayfa oluşturma sorumluluğuna sahip
 */
class PdfDocumentFactory @Inject constructor() : DocumentFactory {
    
    override fun createPdfDocument(): PdfDocument {
        return PdfDocument()
    }
    
    override suspend fun createPage(document: PdfDocument, pageNumber: Int): Page {
        return withContext(Dispatchers.IO) {
            val pageInfo = Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
            document.startPage(pageInfo)

        }
    }
    
    override fun finishPage(document: PdfDocument, page: Page) {
        document.finishPage(page)
    }
} 