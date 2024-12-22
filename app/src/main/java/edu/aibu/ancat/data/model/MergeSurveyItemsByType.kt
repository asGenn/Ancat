package edu.aibu.ancat.data.model

import kotlinx.coroutines.flow.MutableStateFlow

fun mergeSurveyItemsByType(items: MutableStateFlow<List<SurveyItem>>): List<SurveyItem> {
    val mergedItems = mutableListOf<SurveyItem>()
    var tempQuestions = mutableListOf<Question>()
    var lastType: String? = null
    var lastTitle: String? = null

    items.value.forEach { item ->
        if (lastType == item.type && lastTitle == item.title) {
            // Aynı type ve title ile ardışık item varsa, questions listesini ekliyoruz
            tempQuestions.addAll(item.questions)
        } else {
            // Farklı bir type ve title gelirse, bir önceki birikimi mergedItems'a ekle
            if (tempQuestions.isNotEmpty()) {
                mergedItems.add(SurveyItem(lastType!!, lastTitle!!, tempQuestions))
            }
            // Yeni type ve title için birikime başla
            lastType = item.type
            lastTitle = item.title
            tempQuestions = item.questions.toMutableList()
        }
    }

    // Son birikimi de ekle
    if (tempQuestions.isNotEmpty()) {
        mergedItems.add(SurveyItem(lastType!!, lastTitle!!, tempQuestions))
    }

    return mergedItems
}