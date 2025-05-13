import kotlinx.serialization.Serializable


@Serializable
data class QrPayload(
    val jsonFileName: String,
    val pageNumber: Int,
    val firstQuestion: QuestionPosition,
    val lastQuestion: QuestionPosition
)

@Serializable
data class QuestionPosition(
    val sectionIndex: Int,
    val questionIndex: Int
)