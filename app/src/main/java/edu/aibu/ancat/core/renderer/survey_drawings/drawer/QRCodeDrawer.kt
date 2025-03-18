package edu.aibu.ancat.core.renderer.survey_drawings.drawer

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter

class QRCodeDrawer {
    /**
     * QR kodu oluşturur ve canvas'ın sağ alt köşesine render eder
     * @param canvas Render işlemi yapılacak canvas
     * @param content qr gömülecek bilgi
     */
    fun renderQRCode(canvas: Canvas, content: String) {
        try {
            // QR kod içeriğini oluştur
            val qrContent = "FileName: $content"

            // QR kod yazıcısını oluştur
            val writer = QRCodeWriter()

            // QR kod parametrelerini ayarla
            val hints = mapOf(
                EncodeHintType.MARGIN to 1,
                EncodeHintType.CHARACTER_SET to "UTF-8"
            )

            // QR kodu oluştur
            val bitMatrix = writer.encode(
                qrContent,
                BarcodeFormat.QR_CODE,
                80, // QR kod genişliği
                80, // QR kod yüksekliği
                hints
            )

            // QR kodu bitmap'e dönüştür
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }

            // QR kodu canvas'a çiz
            val paint = Paint()
            val margin = 3f // Kenarlardan uzaklık
            val qrSize = 50f // QR kodun boyutu

            // Canvas'ın boyutlarını al
            val canvasWidth = canvas.width.toFloat()
            val canvasHeight = canvas.height.toFloat()

            // QR kodun konumunu hesapla (sağ alt köşe)
            val left = canvasWidth - qrSize - margin
            val top = canvasHeight - qrSize - margin

            // QR kodu çiz
            canvas.drawBitmap(
                bitmap,
                null,
                android.graphics.RectF(left, top, left + qrSize, top + qrSize),
                paint
            )

            // Bitmap'i temizle
            bitmap.recycle()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}