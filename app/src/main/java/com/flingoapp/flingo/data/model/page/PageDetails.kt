
import androidx.compose.ui.graphics.Color
import com.flingoapp.flingo.R
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
        @Transient val imagePrompt: String = "",
        //either url for OpenAI or base64 encoded image for Vertex AI
        val imageData: String,
        //special handling since vertex ai only supports base64 encoded images
        @Transient val isFromVertexAi: Boolean = false
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

enum class PageDetailsSelectionEntry(
    val iconRes: Int,
    val title: String,
    val pageType: PageDetailsType,
    val backgroundColor: Color,
    val iconTint: Color
) {
    REMOVE_WORD(
        iconRes = R.drawable.pagedetails_remove_word_icon,
        title = "Remove Word",
        pageType = PageDetailsType.REMOVE_WORD,
        backgroundColor = Color(0xFF0094D0),
        iconTint = Color(0xFF56CDFF)
    ),
    QUIZ(
        iconRes = R.drawable.pagedetails_quiz_icon,
        title = "Quiz",
        pageType = PageDetailsType.QUIZ,
        backgroundColor = Color(0xFFf7904d),
        iconTint = Color(0xFFFFBB6F)
    ),
    ORDER_STORY(
        iconRes = R.drawable.pagedetails_order_story_icon,
        title = "Order Story",
        pageType = PageDetailsType.ORDER_STORY,
        backgroundColor = Color(0xFFAB80FF),
        iconTint = Color(0xFFD5BFFF)
    ),
}