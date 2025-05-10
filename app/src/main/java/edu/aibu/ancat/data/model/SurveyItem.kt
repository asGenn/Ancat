package edu.aibu.ancat.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SurveyItem(
    val type: String,
    var questions: List<Question>
)


@Serializable
sealed class Question {

    @Serializable
    data class SurveyTitle(val title: String, val description: List<String>) : Question()

    @Serializable
    data class SurveyDescription(var description: String) : Question()

    @Serializable
    data class MultipleChoiceQuestion(
        var question: String,
        var options: List<String>,
        var marks: List<Float>
    ) : Question()

    @Serializable
    data class RatingQuestion(var question: String, var mark: Float) : Question()
}
