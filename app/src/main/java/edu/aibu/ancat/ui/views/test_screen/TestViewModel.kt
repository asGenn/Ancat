package edu.aibu.ancat.ui.views.test_screen

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.aibu.ancat.core.helper.DocumentHelper
import edu.aibu.ancat.data.model.SurveyItem
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class TestViewModel @Inject constructor(
    private val documentHelper: DocumentHelper,
    ): ViewModel() {

    private val json = """
        [
          {
            "type": "_",
            "questions": [
              {
                "type": "edu.aibu.ancat.data.model.Question.SurveyTitle",
                "title": "test",
                "description": [
                  "açıklama",
                  "açıklama"
                ]
              }
            ]
          },
          {
            "type": "2",
    "questions": [
      {
        "type": "edu.aibu.ancat.data.model.Question.MultipleChoiceQuestion",
        "question": "Hangi rengi tercih edersiniz?",
        "options": ["Mavi", "Yeşil", "Kırmızı", "Sarı"]
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.MultipleChoiceQuestion",
        "question": "Hangi mevsimi tercih edersiniz?",
        "options": ["İlkbahar", "Yaz", "Sonbahar", "Kış"]
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.MultipleChoiceQuestion",
        "question": "Hangi tür müzikleri dinlersiniz?",
        "options": ["Pop", "Rock", "Caz", "Klasik"]
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.MultipleChoiceQuestion",
        "question": "Hangi yemek türünü daha çok seversiniz?",
        "options": ["Türk Mutfağı", "İtalyan Mutfağı", "Meksika Mutfağı", "Japon Mutfağı"]
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.MultipleChoiceQuestion",
        "question": "Hangi sporları takip ediyorsunuz?",
        "options": ["Futbol", "Basketbol", "Tenis", "Voleybol"]
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.MultipleChoiceQuestion",
        "question": "En çok hangi içeceği tercih edersiniz?",
        "options": ["Kahve", "Çay", "Meyve Suyu", "Su"]
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.MultipleChoiceQuestion",
        "question": "Hangi film türünü seversiniz?",
        "options": ["Aksiyon", "Komedi", "Bilim Kurgu", "Korku"]
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.MultipleChoiceQuestion",
        "question": "Hangi tatlıyı tercih edersiniz?",
        "options": ["Baklava", "Cheesecake", "Dondurma", "Profiterol"]
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.MultipleChoiceQuestion",
        "question": "Hangi hayvanı daha çok seversiniz?",
        "options": ["Köpek", "Kedi", "Kuş", "Balık"]
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.MultipleChoiceQuestion",
        "question": "Hangi tatil yerini tercih edersiniz?",
        "options": ["Deniz Kenarı", "Dağ Evi", "Şehir Gezisi", "Kırsal Alan"]
      }
    ]
  },
          {
    "type": "1",
    "questions": [
      {
        "type": "edu.aibu.ancat.data.model.Question.RatingQuestion",
        "question": "Uygulamamızın genel performansını 1 ile 5 arasında nasıl değerlendirirsiniz?"
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.RatingQuestion",
        "question": "Arayüz tasarımımızın kullanım kolaylığını 1 ile 5 arasında puanlayın."
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.RatingQuestion",
        "question": "Hizmet kalitemizi 1 ile 5 arasında nasıl değerlendirirsiniz?"
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.RatingQuestion",
        "question": "Mobil uygulamamızın hızını 1 ile 5 arasında puanlayın."
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.RatingQuestion",
        "question": "Destek ekibimizin sorunlarınıza çözüm bulma hızını 1 ile 5 arasında değerlendirin."
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.RatingQuestion",
        "question": "Web sitemizin görselliğini 1 ile 5 arasında nasıl buluyorsunuz?"
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.RatingQuestion",
        "question": "Ürünlerimizin kalitesini 1 ile 5 arasında puanlayın."
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.RatingQuestion",
        "question": "Fiyatlarımızın uygunluğunu 1 ile 5 arasında değerlendirir misiniz?"
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.RatingQuestion",
        "question": "Sunduğumuz özelliklerin yeterliliğini 1 ile 5 arasında puanlayın."
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.RatingQuestion",
        "question": "Sosyal medya paylaşımlarımızın faydalılığını 1 ile 5 arasında değerlendirin."
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.RatingQuestion",
        "question": "Kampanyalarımızın ilginçliğini 1 ile 5 arasında puanlayın."
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.RatingQuestion",
        "question": "Ödeme seçeneklerimizin yeterliliğini 1 ile 5 arasında değerlendirin."
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.RatingQuestion",
        "question": "Teslimat sürecimizi 1 ile 5 arasında nasıl buluyorsunuz?"
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.RatingQuestion",
        "question": "Güvenli alışveriş deneyimini sağlama konusundaki başarımızı 1 ile 5 arasında değerlendirin."
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.RatingQuestion",
        "question": "Genel memnuniyetinizi 1 ile 5 arasında nasıl değerlendirirsiniz?"
      }
    ]
  },
          {"type": "2",
    "questions": [
      {
        "type": "edu.aibu.ancat.data.model.Question.MultipleChoiceQuestion",
        "question": "Hangi rengi tercih edersiniz?",
        "options": ["Mavi", "Yeşil", "Kırmızı", "Sarı"]
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.MultipleChoiceQuestion",
        "question": "Hangi mevsimi tercih edersiniz?",
        "options": ["İlkbahar", "Yaz", "Sonbahar", "Kış"]
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.MultipleChoiceQuestion",
        "question": "Hangi tür müzikleri dinlersiniz?",
        "options": ["Pop", "Rock", "Caz", "Klasik"]
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.MultipleChoiceQuestion",
        "question": "Hangi yemek türünü daha çok seversiniz?",
        "options": ["Türk Mutfağı", "İtalyan Mutfağı", "Meksika Mutfağı", "Japon Mutfağı"]
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.MultipleChoiceQuestion",
        "question": "Hangi sporları takip ediyorsunuz?",
        "options": ["Futbol", "Basketbol", "Tenis", "Voleybol"]
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.MultipleChoiceQuestion",
        "question": "En çok hangi içeceği tercih edersiniz?",
        "options": ["Kahve", "Çay", "Meyve Suyu", "Su"]
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.MultipleChoiceQuestion",
        "question": "Hangi film türünü seversiniz?",
        "options": ["Aksiyon", "Komedi", "Bilim Kurgu", "Korku"]
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.MultipleChoiceQuestion",
        "question": "Hangi tatlıyı tercih edersiniz?",
        "options": ["Baklava", "Cheesecake", "Dondurma", "Profiterol"]
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.MultipleChoiceQuestion",
        "question": "Hangi hayvanı daha çok seversiniz?",
        "options": ["Köpek", "Kedi", "Kuş", "Balık"]
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.MultipleChoiceQuestion",
        "question": "Hangi tatil yerini tercih edersiniz?",
        "options": ["Deniz Kenarı", "Dağ Evi", "Şehir Gezisi", "Kırsal Alan"]
      }
    ]
  },
   {
    "type": "1",
    "questions": [
      {
        "type": "edu.aibu.ancat.data.model.Question.RatingQuestion",
        "question": "Uygulamamızın genel performansını 1 ile 5 arasında nasıl değerlendirirsiniz?"
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.RatingQuestion",
        "question": "Arayüz tasarımımızın kullanım kolaylığını 1 ile 5 arasında puanlayın."
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.RatingQuestion",
        "question": "Hizmet kalitemizi 1 ile 5 arasında nasıl değerlendirirsiniz?"
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.RatingQuestion",
        "question": "Mobil uygulamamızın hızını 1 ile 5 arasında puanlayın."
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.RatingQuestion",
        "question": "Destek ekibimizin sorunlarınıza çözüm bulma hızını 1 ile 5 arasında değerlendirin."
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.RatingQuestion",
        "question": "Web sitemizin görselliğini 1 ile 5 arasında nasıl buluyorsunuz?"
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.RatingQuestion",
        "question": "Ürünlerimizin kalitesini 1 ile 5 arasında puanlayın."
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.RatingQuestion",
        "question": "Fiyatlarımızın uygunluğunu 1 ile 5 arasında değerlendirir misiniz?"
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.RatingQuestion",
        "question": "Sunduğumuz özelliklerin yeterliliğini 1 ile 5 arasında puanlayın."
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.RatingQuestion",
        "question": "Sosyal medya paylaşımlarımızın faydalılığını 1 ile 5 arasında değerlendirin."
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.RatingQuestion",
        "question": "Kampanyalarımızın ilginçliğini 1 ile 5 arasında puanlayın."
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.RatingQuestion",
        "question": "Ödeme seçeneklerimizin yeterliliğini 1 ile 5 arasında değerlendirin."
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.RatingQuestion",
        "question": "Teslimat sürecimizi 1 ile 5 arasında nasıl buluyorsunuz?"
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.RatingQuestion",
        "question": "Güvenli alışveriş deneyimini sağlama konusundaki başarımızı 1 ile 5 arasında değerlendirin."
      },
      {
        "type": "edu.aibu.ancat.data.model.Question.RatingQuestion",
        "question": "Genel memnuniyetinizi 1 ile 5 arasında nasıl değerlendirirsiniz?"
      }
    ]
  }
        ]
    """.trimIndent()

    val data = Json.decodeFromString<List<SurveyItem>>(json)

    suspend fun createDoc(context: Context) {
        documentHelper.createDocument(
            context = context,
            data = data,
            jsonName = "Selamlarssss"
        )
    }

}