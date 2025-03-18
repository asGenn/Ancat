package edu.aibu.ancat.core.renderer.strategy

import android.graphics.Canvas
import edu.aibu.ancat.core.renderer.survey_drawings.drawer.QRCodeDrawer
import javax.inject.Inject

/**
 * Başlık sorusu render stratejisi
 * SOLID - SRP: Sadece QR code render etme sorumluluğu var
 */
class QRCodeFactory @Inject constructor(
    private val qrCodeDrawer: QRCodeDrawer
): QRRendererStrategy {
    override fun renderQRCode(canvas: Canvas, content: String) {
        qrCodeDrawer.renderQRCode(
            canvas = canvas,
            content = content,
        )
    }
}