package edu.aibu.ancat.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class SurveyAnalysisResult(
    @SerialName("sectionIdx") var sectionIdx: Int? = null,
    @SerialName("questionIdx") var questionIdx: Int? = null,
    @SerialName("type") var type: String? = null,
    @SerialName("analysis") var analysis: List<Int> = emptyList<Int>()
)
