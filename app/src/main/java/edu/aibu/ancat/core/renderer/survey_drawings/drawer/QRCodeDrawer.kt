package edu.aibu.ancat.core.renderer.survey_drawings.drawer

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter

class QRCodeDrawer {
    // QR kodu canvas'a çiz
    private val paint = Paint()
    private val margin = 3f // Kenarlardan uzaklık
    private val qrSize = 50f // QR kodun boyutu

    /**
     * QR kodu oluşturur ve canvas'ın sağ alt köşesine render eder
     * @param canvas Render işlemi yapılacak canvas
     * @param content qr gömülecek bilgi
     */
    fun renderQRCode(canvas: Canvas, content: String) {
        try {
            // QR kod içeriğini oluştur
            val qrBitmapSize = 800

            // QR kod yazıcısını oluştur
            val writer = QRCodeWriter()

            // QR kod parametrelerini ayarla
            val hints = mapOf(
                EncodeHintType.MARGIN to 1,
                EncodeHintType.CHARACTER_SET to "UTF-8"
            )

            // QR kodu oluştur
            val bitMatrix = writer.encode(
                content,
                BarcodeFormat.QR_CODE,
                qrBitmapSize,
                qrBitmapSize,
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

            // Canvas'ın boyutlarını al
            val canvasWidth = canvas.width.toFloat()
            val canvasHeight = canvas.height.toFloat()

            // Sol üst köşe
            val topLeftX = margin
            val topLeftY = margin
            drawQR(canvas, bitmap, paint,topLeftX, topLeftY)

            // Sağ üst köşe
            /*val topRightX = canvasWidth - qrSize - margin
            val topRightY = margin
            drawQR(canvas, bitmap, paint, topRightX, topRightY)*/

            // Sağ alt köşe
            val bottomRightX = canvasWidth - qrSize - margin
            val bottomRightY = canvasHeight - qrSize - margin
            drawQR(canvas, bitmap, paint, bottomRightX, bottomRightY)

            // Sol alt köşe
            /*val bottomLeftX = margin
            val bottomLeftY = canvasHeight - qrSize - margin
            drawQR(canvas, bitmap, paint, bottomLeftX, bottomLeftY)*/

            // Bitmap'i temizle
            bitmap.recycle()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun drawQR(
        canvas: Canvas,
        bitmap: Bitmap,
        paint: Paint,
        posX: Float,
        posY: Float
    ) {
        canvas.drawBitmap(
            bitmap,
            null,
            android.graphics.RectF(posX, posY, posX + qrSize, posY + qrSize),
            paint
        )
    }
}