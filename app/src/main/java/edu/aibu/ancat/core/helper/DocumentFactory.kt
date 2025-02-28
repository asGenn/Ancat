package edu.aibu.ancat.core.helper

import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.Page

/**
 * PDF belgeleri ve sayfaları oluşturma işlemlerini soyutlayan arayüz
 * SOLID - ISP: Sadece gerekli fonksiyonlar sunuluyor
 * SOLID - DIP: Somut implementasyonlar yerine arayüzler kullanılıyor
 */
interface DocumentFactory {
    /**
     * Yeni bir PdfDocument nesnesi oluşturur
     * @return Oluşturulan PdfDocument nesnesi
     */
    fun createPdfDocument(): PdfDocument
    
    /**
     * PdfDocument için yeni bir sayfa oluşturur
     * @param document Sayfa eklenecek PDF belgesi
     * @param pageNumber Sayfa numarası
     * @return Oluşturulan sayfa nesnesi
     */
    suspend fun createPage(document: PdfDocument, pageNumber: Int): Page
    
    /**
     * Bir sayfayı tamamlar ve PDF belgesine ekler
     * @param document PDF belgesi
     * @param page Tamamlanacak sayfa
     */
    fun finishPage(document: PdfDocument, page: Page)
} 