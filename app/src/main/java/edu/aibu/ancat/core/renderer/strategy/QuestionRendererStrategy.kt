package edu.aibu.ancat.core.renderer.strategy

import android.graphics.Canvas
import edu.aibu.ancat.data.model.Question

/**
 * Soru render etme stratejisi için arayüz
 * SOLID - ISP: Her strateji sadece gerekli metodu uygular
 * SOLID - DIP: Yüksek seviyeli modüller (DocumentRenderer) bu arayüze bağımlıdır
 */
interface QuestionRendererStrategy {
    /**
     * Soruyu canvas üzerinde render eder
     * @param canvas Render işlemi yapılacak canvas
     * @param question Render edilecek soru
     * @param cursorPosition Y-ekseni başlangıç pozisyonu
     * @return Render sonrası güncellenmiş Y pozisyonu
     */
    fun renderQuestion(canvas: Canvas, question: Question, cursorPosition: Float): Float
} 