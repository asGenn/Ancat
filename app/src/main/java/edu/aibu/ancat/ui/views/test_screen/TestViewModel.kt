package edu.aibu.ancat.ui.views.test_screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.aibu.ancat.data.model.SurveyAnalysisResult
import edu.aibu.ancat.data.model.SurveyItem
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class TestViewModel @Inject constructor() : ViewModel() {
    private val json = """
        [
          {
            "type": "_",
            "questions": [
              {
                "type": "edu.aibu.ancat.data.model.Question.SurveyTitle",
                "title": "Test Anketi",
                "description": [
                  "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce varius nunc ut sapien gravida.",
                  " Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum euismod, nunc vel tincidunt luctus, purus sapien tristique ligula, non scelerisque metus libero at justo. Donec vehicula orci nec lorem efficitur."
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
                "options": [
                  "Mavi",
                  "Yeşil",
                  "Kırmızı",
                  "Sarı"
                ],
                "marks": []
              },
              {
                "type": "edu.aibu.ancat.data.model.Question.MultipleChoiceQuestion",
                "question": "Hangi mevsimi tercih edersiniz?",
                "options": [
                  "İlkbahar",
                  "Yaz",
                  "Sonbahar",
                  "Kış"
                ],
                "marks": []
              },
              {
                "type": "edu.aibu.ancat.data.model.Question.MultipleChoiceQuestion",
                "question": "Hangi tür müzikleri dinlersiniz?",
                "options": [
                  "Pop",
                  "Rock",
                  "Caz",
                  "Klasik"
                ],
                "marks": []
              }
            ]
          },
          {
            "type": "1",
            "questions": [
              {
                "type": "edu.aibu.ancat.data.model.Question.RatingQuestion",
                "question": "Uygulamamızın genel performansını 1 ile 5 arasında nasıl değerlendirirsiniz?",
                "mark" : 0
              },
              {
                "type": "edu.aibu.ancat.data.model.Question.RatingQuestion",
                "question": "Arayüz tasarımımızın kullanım kolaylığını 1 ile 5 arasında puanlayın.",
                "mark" : 0
              },
              {
                "type": "edu.aibu.ancat.data.model.Question.RatingQuestion",
                "question": "Hizmet kalitemizi 1 ile 5 arasında nasıl değerlendirirsiniz?",
                "mark" : 0
              }
            ]
          }
        ]
    """.trimIndent()

    private val jsonResult = """
        [
          {"sectionIdx":1,"questionIdx":0,"type":"2","analysis":[25137,31245,17620,25998]},
          {"sectionIdx":1,"questionIdx":1,"type":"2","analysis":[12983,14327,60249,12441]},
          {"sectionIdx":1,"questionIdx":2,"type":"2","analysis":[67442,12987,9234,8347]},
          {"sectionIdx":1,"questionIdx":3,"type":"2","analysis":[13546,67834,9232,5388]},
          {"sectionIdx":2,"questionIdx":0,"type":"1","analysis":[92347,3821,1453,936,443]},
          {"sectionIdx":2,"questionIdx":1,"type":"1","analysis":[41125,35223,10847,9023,7782]},
          {"sectionIdx":2,"questionIdx":2,"type":"1","analysis":[24037,21458,20473,17861,16171]},
          {"sectionIdx":2,"questionIdx":3,"type":"1","analysis":[24672,22311,20774,16780,15463]}
        ]
    """.trimIndent()

    val data = Json.decodeFromString<List<SurveyItem>>(json)
    val resultData = Json.decodeFromString<List<SurveyAnalysisResult>>(jsonResult)
}