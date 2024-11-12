package com.example.ancat.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Option(
    val question: String,
    val options: List<String>
)

@Serializable
data class SurveyItem(
    val type: String,
    val title: String,
    val questions: List<Question>
)


@Serializable
sealed class Question {

    @Serializable
    data class SurveyTitle(val description : List<String>) : Question()

    @Serializable
    data class SimpleQuestion(val question: String) : Question()

    @Serializable
    data class MultipleChoiceQuestion(val question: String, val options: List<String>) : Question()


    @Serializable
    data class RatingQuestion(val question: String) : Question()
}
