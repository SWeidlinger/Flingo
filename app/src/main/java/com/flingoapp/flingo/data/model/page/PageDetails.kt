import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("pageDetailsType")
sealed interface PageDetails {
    @SerialName("pageDetailsType")
    val type: PageDetailsType

    @Serializable
    @SerialName("read")
    data class Read(
        @Transient @SerialName("pageDetailsType") override val type: PageDetailsType = PageDetailsType.READ,
        val content: String,
        @Transient val originalContent: String = "",
        val imageUrl: String,
    ) : PageDetails

    @Serializable
    @SerialName("remove_word")
    data class RemoveWord(
        @Transient @SerialName("pageDetailsType") override val type: PageDetailsType = PageDetailsType.REMOVE_WORD,
        val content: String,
        val answer: String
    ) : PageDetails

    @Serializable
    @SerialName("order_story")
    data class OrderStory(
        @Transient @SerialName("pageDetailsType") override val type: PageDetailsType = PageDetailsType.ORDER_STORY,
        val content: List<Content>,
        val correctOrder: List<Int>
    ) : PageDetails {
        @Serializable
        data class Content(
            val id: Int,
            val text: String
        )
    }

    @Serializable
    @SerialName("quiz")
    data class Quiz(
        @Transient @SerialName("pageDetailsType") override val type: PageDetailsType = PageDetailsType.QUIZ,
        val quizType: QuizType,
        val question: String,
        val answers: List<Answer>
    ) : PageDetails {
        @Serializable
        enum class QuizType {
            @SerialName("true_or_false")
            TRUE_OR_FALSE,

            @SerialName("single_choice")
            SINGLE_CHOICE
        }

        @Serializable
        data class Answer(
            val id: Int,
            val answer: String,
            val isCorrect: Boolean
        )
    }
}

/**
 * Page type enum can either be remove word type, context based questions type, change character type,
 * currently not in use
 *
 * @constructor Create empty Page type
 */
@Serializable
enum class PageDetailsType {
    /**
     * Reading page
     *
     */
    @SerialName("read")
    READ,

    /**
     * Remove Word type
     *
     */
    @SerialName("remove_word")
    REMOVE_WORD,

    /**
     * Context Based Questions type
     *
     */
    @SerialName("quiz")
    QUIZ,

    /**
     * Order Story type
     *
     */
    @SerialName("order_story")
    ORDER_STORY
}