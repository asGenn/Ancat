package edu.aibu.ancat.core.renderer

import android.content.Context
import android.graphics.Canvas
import edu.aibu.ancat.core.renderer.strategy.QRRendererStrategy
import edu.aibu.ancat.core.renderer.strategy.QuestionStrategyFactory
import edu.aibu.ancat.data.model.Question
import javax.inject.Inject

/**
 * Belgeleri render etmekten sorumlu sınıf
 * SOLID - SRP: Sadece render etme sorumluluğu var
 * SOLID - OCP: Yeni soru tipleri eklendiğinde bu sınıfı değiştirmek gerekmez
 */
class DocumentRenderer @Inject constructor(
    private val questionStrategyFactory: QuestionStrategyFactory,
    private val qrRendererStrategy: QRRendererStrategy
) {
    /**
     * Verilen anket öğesini canvas üzerinde render eder
     * @param canvas Render işlemi yapılacak canvas
     * @param cursor Başlangıç Y pozisyonu
     * @param data Render edilecek anket öğesi
     * @param jsonFileName JSON dosyasının adı
     * @param surveyIndex Index numarası
     * @return Render sonrası güncellenmiş Y pozisyonu (cursor)
     */
    fun renderDocument(
        canvas: Canvas,
        cursor: Float,
        data: Question,
        type: String,
        index: Int,
        jsonFileName: String,
        surveyIndex: Int,
        context: Context
    ): Float {
        var cursorPosition = cursor
        
        // Soru tipi için uygun stratejiyi al
        val strategy = questionStrategyFactory.getStrategyForType(type)
        
        // Soruyu sırayla render et
        cursorPosition = strategy.renderQuestion(
            canvas, data, cursorPosition,
            surveyIndex = surveyIndex,
            questionIndex = index,
            jsonFileName = jsonFileName,
            context = context,
        )
//        data.questions.forEachIndexed { index, question ->
//            cursorPosition = strategy.renderQuestion(
//                canvas, question, cursorPosition,
//                surveyIndex = surveyIndex,
//                questionIndex = index,
//                jsonFileName = jsonFileName,
//                context = context,
//            )
//        }

        // QR kodu oluştur ve render et
        qrRendererStrategy.renderQRCode(canvas, jsonFileName)

        return cursorPosition
    }
}