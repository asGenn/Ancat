package edu.aibu.ancat.core.renderer.strategy

import javax.inject.Inject
import javax.inject.Provider

/**
 * Soru tipine göre uygun strateji nesnesi sağlayan fabrika sınıfı
 * SOLID - OCP: Yeni stratejiler eklenerek genişletilebilir, ancak sınıfı değiştirmek gerekmez
 * SOLID - SRP: Sadece strateji oluşturma sorumluluğu var
 */
class QuestionStrategyFactory @Inject constructor(
    private val strategies: Map<String, @JvmSuppressWildcards Provider<QuestionRendererStrategy>>
) {
    /**
     * Verilen soru tipi için uygun strateji nesnesi döndürür
     * @param type Soru tipi (_: Başlık, 0: Açıklama, 1: Derecelendirme, 2: Çoktan Seçmeli)
     * @return Soru tipine uygun strateji nesnesi
     */
    fun getStrategyForType(type: String): QuestionRendererStrategy {
        return strategies[type]?.get() ?: strategies["default"]!!.get()
    }
} 