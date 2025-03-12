package edu.aibu.ancat.core.renderer

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import edu.aibu.ancat.core.renderer.strategy.QuestionStrategyFactory
import edu.aibu.ancat.data.model.SurveyItem
import javax.inject.Inject

/**
 * Belgeleri render etmekten sorumlu sınıf
 * SOLID - SRP: Sadece render etme sorumluluğu var
 * SOLID - OCP: Yeni soru tipleri eklendiğinde bu sınıfı değiştirmek gerekmez
 */
class DocumentRenderer @Inject constructor(
    private val questionStrategyFactory: QuestionStrategyFactory
) {
    /**
     * Verilen anket öğesini canvas üzerinde render eder
     * @param canvas Render işlemi yapılacak canvas
     * @param cursor Başlangıç Y pozisyonu
     * @param data Render edilecek anket öğesi
     * @param jsonFileName JSON dosyasının adı
     * @param pageNumber Sayfa numarası
     * @return Render sonrası güncellenmiş Y pozisyonu (cursor)
     */
    fun renderDocument(
        canvas: Canvas,
        cursor: Float,
        data: SurveyItem,
        jsonFileName: String,
        pageNumber: Int
    ): Float {
        var cursorPosition = cursor
        
        // Soru tipi için uygun stratejiyi al
        val strategy = questionStrategyFactory.getStrategyForType(data.type)
        
        // Her soruyu sırayla render et
        data.questions.forEach { question ->
            cursorPosition = strategy.renderQuestion(canvas, question, cursorPosition)
        }

        // QR kodu oluştur ve render et
        renderQRCode(canvas, jsonFileName, pageNumber)
        
        return cursorPosition
    }

    /**
     * QR kodu oluşturur ve canvas'ın sağ alt köşesine render eder
     * @param canvas Render işlemi yapılacak canvas
     * @param jsonFileName JSON dosyasının adı
     * @param pageNumber Sayfa numarası
     */
    private fun renderQRCode(canvas: Canvas, jsonFileName: String, pageNumber: Int) {
        try {
            // QR kod içeriğini oluştur
            val qrContent = "FileName: $jsonFileName, Page: $pageNumber"
            
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
                200, // QR kod genişliği
                200, // QR kod yüksekliği
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
            val margin = 20f // Kenarlardan uzaklık
            val qrSize = 100f // QR kodun boyutu
            
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