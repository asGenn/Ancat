package edu.aibu.ancat.core.helper

import android.content.Context
import android.graphics.pdf.PdfDocument

/**
 * PDF belge saklama işlemlerini soyutlayan arayüz
 * SOLID - SRP: Belge saklama sorumluluğu ayrı bir arayüze taşındı
 */
interface DocumentStorage {
    /**
     * PDF belgesini cihazda saklar
     * @param context Uygulama bağlamı
     * @param document Kaydedilecek PDF belgesi
     * @param documentName Belge adı
     */
    suspend fun saveDocument(context: Context, document: PdfDocument, documentName: String)
} 