
import androidx.compose.ui.graphics.Color
import com.flingoapp.flingo.R
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonClassDiscriminator

/**
 * Page details
 *
 * @constructor Create empty Page details
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("pageDetailsType")
sealed interface PageDetails {
    @SerialName("pageDetailsType")
    val type: PageDetailsType

    /**
     * Read
     *
     * @property type
     * @property content
     * @property originalContent
     * @property imagePrompt
     * @property imageData
     * @property isFromVertexAi
     * @constructor Create empty Read
     */
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

    /**
     * Remove word
     *
     * @property type
     * @property content
     * @property answer
     * @constructor Create empty Remove word
     */
    @Serializable
    @SerialName("remove_word")
    data class RemoveWord(
        @Transient @SerialName("pageDetailsType") override val type: PageDetailsType = PageDetailsType.REMOVE_WORD,
        val content: String,
        val answer: String
    ) : PageDetails

    /**
     * Order story
     *
     * @property type
     * @property content
     * @property correctOrder
     * @constructor Create empty Order story
     */
    @Serializable
    @SerialName("order_story")
    data class OrderStory(
        @Transient @SerialName("pageDetailsType") override val type: PageDetailsType = PageDetailsType.ORDER_STORY,
        val content: List<Content>,
        val correctOrder: List<Int>
    ) : PageDetails {
        /**
         * Content
         *
         * @property id
         * @property text
         * @constructor Create empty Content
         */
        @Serializable
        data class Content(
            val id: Int,
            val text: String
        )
    }

    /**
     * Quiz
     *
     * @property type
     * @property quizType
     * @property question
     * @property answers
     * @constructor Create empty Quiz
     */
    @Serializable
    @SerialName("quiz")
    data class Quiz(
        @Transient @SerialName("pageDetailsType") override val type: PageDetailsType = PageDetailsType.QUIZ,
        val quizType: QuizType,
        val question: String,
        val answers: List<Answer>
    ) : PageDetails {
        /**
         * Quiz type
         *
         * @constructor Create empty Quiz type
         */
        @Serializable
        enum class QuizType {
            /**
             * True Or False
             *
             * @constructor Create empty True Or False
             */
            @SerialName("true_or_false")
            TRUE_OR_FALSE,

            /**
             * Single Choice
             *
             * @constructor Create empty Single Choice
             */
            @SerialName("single_choice")
            SINGLE_CHOICE
        }

        /**
         * Answer
         *
         * @property answer
         * @property isCorrect
         * @constructor Create empty Answer
         */
        @Serializable
        data class Answer(
            val answer: String,
            val isCorrect: Boolean
        )
    }
}

/**
 * Page details type
 *
 * @constructor Create empty Page details type
 */
@Serializable
enum class PageDetailsType {
    /**
     * Read
     *
     * @constructor Create empty Read
     */
    @SerialName("read")
    READ,

    /**
     * Remove Word
     *
     * @constructor Create empty Remove Word
     */
    @SerialName("remove_word")
    REMOVE_WORD,

    /**
     * Quiz
     *
     * @constructor Create empty Quiz
     */
    @SerialName("quiz")
    QUIZ,

    /**
     * Order Story
     *
     * @constructor Create empty Order Story
     */
    @SerialName("order_story")
    ORDER_STORY
}

/**
 * Page details selection entry
 *
 * @property iconRes
 * @property title
 * @property pageType
 * @property backgroundColor
 * @property iconTint
 * @constructor Create empty Page details selection entry
 */
enum class PageDetailsSelectionEntry(
    val iconRes: Int,
    val title: String,
    val pageType: PageDetailsType,
    val backgroundColor: Color,
    val iconTint: Color
) {
    /**
     * Remove Word
     *
     * @constructor Create empty Remove Word
     */
    REMOVE_WORD(
        iconRes = R.drawable.pagedetails_remove_word_icon,
        title = "Remove Word",
        pageType = PageDetailsType.REMOVE_WORD,
        backgroundColor = Color(0xFF03A9F4),
        iconTint = Color(0xFF56CDFF)
    ),

    /**
     * Quiz
     *
     * @constructor Create empty Quiz
     */
    QUIZ(
        iconRes = R.drawable.pagedetails_quiz_icon,
        title = "Quiz",
        pageType = PageDetailsType.QUIZ,
        backgroundColor = Color(0xFFf7904d),
        iconTint = Color(0xFFFFBB6F)
    ),

    /**
     * Order Story
     *
     * @constructor Create empty Order Story
     */
    ORDER_STORY(
        iconRes = R.drawable.pagedetails_order_story_icon,
        title = "Order Story",
        pageType = PageDetailsType.ORDER_STORY,
        backgroundColor = Color(0xFFAB80FF),
        iconTint = Color(0xFFD5BFFF)
    ),
}