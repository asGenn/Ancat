package edu.aibu.ancat.core.renderer.strategy

import android.graphics.Canvas

/**
 * QRCode render etme stratejisi için arayüz
 * SOLID - ISP: Her strateji sadece gerekli metodu uygular
 * SOLID - DIP: Yüksek seviyeli modüller (DocumentRenderer) bu arayüze bağımlıdır
 */
interface QRRendererStrategy {
    /**
     * Soruyu canvas üzerinde render eder
     * @param canvas Render işlemi yapılacak canvas
     * @param content qr içine gömülecek bilgi
     */
    fun renderQRCode(canvas: Canvas, content: String)
}