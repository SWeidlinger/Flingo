package com.flingoapp.flingo.data.model.genAi

import PageDetails
import PageDetailsType
import android.content.Context
import android.util.Log
import com.flingoapp.flingo.viewmodel.PersonalizationAspects
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

/**
 * Gen ai request builder, a unified interface for creating different types of genAi requests that cater to various genAI models and frameworks
 *
 * @constructor Create empty Gen ai request builder
 */
interface GenAiRequestBuilder {
    /**
     * Book request
     *
     * @param content
     * @param personalizationAspects
     * @return
     */
    //general
    fun bookRequest(
        content: String?,
        personalizationAspects: PersonalizationAspects?
    ): GenAiRequest

    /**
     * Chapter request
     *
     * @param content
     * @param personalizationAspects
     * @return
     */
    fun chapterRequest(
        content: String?,
        personalizationAspects: PersonalizationAspects?
    ): GenAiRequest

    /**
     * Page request
     *
     * @param content
     * @param personalizationAspects
     * @return
     */
    fun pageRequest(
        content: String?,
        personalizationAspects: PersonalizationAspects?
    ): GenAiRequest

    /**
     * Page details request, generate a reading challenge based on the page details type
     *
     * @param type
     * @param quizType
     * @param requestPageAmount
     * @param content
     * @return
     */
    fun pageDetailsRequest(
        type: PageDetailsType,
        quizType: PageDetails.Quiz.QuizType? = null,
        requestPageAmount: Int,
        content: String?
    ): GenAiRequest

    /**
     * Image request
     *
     * @param prompt
     * @return
     */
    fun imageRequest(prompt: String): GenAiRequest

    /**
     * Split text request, used for generating a book from text
     *
     * @param content
     * @return
     *///specific
    fun splitTextRequest(
        content: String?
    ): GenAiRequest

    /**
     * Personalize text parts, used for generating a book from a text
     *
     * @param content
     * @param personalizationAspects
     * @return
     */
    fun personalizeTextParts(
        content: String?,
        personalizationAspects: PersonalizationAspects?
    ): GenAiRequest

    /**
     * Image generation prompt for text
     *
     * @param content
     * @param personalizationAspects
     * @return
     */
    fun imageGenerationPromptForText(
        content: String?,
        personalizationAspects: PersonalizationAspects?
    ): GenAiRequest

    /**
     * Add personalization aspects
     *
     * @param basePrompt
     * @param personalizationAspects
     * @return
     */
    fun addPersonalizationAspects(basePrompt: String, personalizationAspects: PersonalizationAspects?): String

    /**
     * Add page amount to prompt
     *
     * @param prompt
     * @param amount
     * @return
     */
    fun addPageAmountToPrompt(prompt: String, amount: Int): String
}

/**
 * Gen ai request
 *
 * @property prompt
 * @property jsonResponseSchema
 * @property content
 * @constructor Create empty Gen ai request
 */
data class GenAiRequest(
    val prompt: String,
    val jsonResponseSchema: JsonElement? = null,
    val content: String
)

/**
 * Gen ai request builder default impl
 *
 * @property context
 * @constructor Create empty Gen ai request builder default impl
 */
class GenAiRequestBuilderDefaultImpl(val context: Context) : GenAiRequestBuilder {
    companion object {
        private const val TAG = "GenAiBasePromptsDefaultImpl"

        //general
        private const val BOOK_PROMPT_RESOURCE = "base_prompts/default/book_instruction.md"
        private const val CHAPTER_PROMPT_RESOURCE = "base_prompts/default/chapter_instruction.md"
        private const val PAGE_PROMPT_RESOURCE = "base_prompts/default/page_instruction.md"

        //specific
        private const val SPLIT_TEXT_PROMPT_RESOURCE = "base_prompts/default/book_from_text/split_text.md"
        private const val SPLIT_TEXT_JSON_SCHEMA = "base_response_schema/default/split_text.json"

        private const val PERSONALIZE_TEXT_PARTS_PROMPT_RESOURCE =
            "base_prompts/default/book_from_text/personalize_text_parts.md"

        private const val IMAGE_GENERATION_PROMPT_FOR_TEXT_RESOURCE =
            "base_prompts/default/book_from_text/generate_image_prompt_for_text.md"
        private const val IMAGE_GENERATION_PROMPT_FOR_TEXT_JSON_SCHEMA =
            "base_response_schema/default/generate_image_prompt_for_text.json"

        //page details
        private const val QUIZ_SINGLE_CHOICE_PROMPT_RESOURCE =
            "base_prompts/default/page_details/quiz/single_choice_instruction.md"
        private const val QUIZ_TRUE_OR_FALSE_PROMPT_RESOURCE =
            "base_prompts/default/page_details/quiz/true_or_false_instruction.md"
        private const val QUIZ_JSON_SCHEMA = "base_response_schema/default/page_details/quiz.json"

        private const val REMOVE_WORD_PROMPT_RESOURCE = "base_prompts/default/page_details/remove_word_instruction.md"
        private const val REMOVE_WORD_JSON_SCHEMA = "base_response_schema/default/page_details/remove_word.json"

        private const val ORDER_STORY_PROMPT_RESOURCE = "base_prompts/default/page_details/order_story_instruction.md"
        private const val ORDER_STORY_JSON_SCHEMA = "base_response_schema/default/page_details/order_story.json"
    }

    override fun bookRequest(
        content: String?,
        personalizationAspects: PersonalizationAspects?
    ): GenAiRequest {
        val personalizedPrompt = addPersonalizationAspects(
            convertFileToText(BOOK_PROMPT_RESOURCE),
            personalizationAspects
        )

        return GenAiRequest(
            prompt = personalizedPrompt,
            jsonResponseSchema = null,
            content = content ?: ""
        )
    }

    override fun chapterRequest(
        content: String?,
        personalizationAspects: PersonalizationAspects?
    ): GenAiRequest {
        val personalizedPrompt = addPersonalizationAspects(
            convertFileToText(CHAPTER_PROMPT_RESOURCE),
            personalizationAspects
        )

        return GenAiRequest(
            prompt = personalizedPrompt,
            jsonResponseSchema = null,
            content = content ?: ""
        )
    }

    override fun pageRequest(
        content: String?,
        personalizationAspects: PersonalizationAspects?
    ): GenAiRequest {
        val personalizedPrompt = addPersonalizationAspects(
            convertFileToText(PAGE_PROMPT_RESOURCE),
            personalizationAspects
        )

        return GenAiRequest(
            prompt = personalizedPrompt,
            jsonResponseSchema = null,
            content = content ?: ""
        )
    }

    override fun pageDetailsRequest(
        type: PageDetailsType,
        quizType: PageDetails.Quiz.QuizType?,
        requestPageAmount: Int,
        content: String?
    ): GenAiRequest {
        return when (type) {
            PageDetailsType.REMOVE_WORD -> {
                val basePrompt = convertFileToText(REMOVE_WORD_PROMPT_RESOURCE)

                GenAiRequest(
                    prompt = addPageAmountToPrompt(basePrompt, requestPageAmount),
                    jsonResponseSchema = convertFileToJson(REMOVE_WORD_JSON_SCHEMA),
                    content = content ?: ""
                )
            }

            PageDetailsType.QUIZ -> {
                val basePrompt = when (quizType) {
                    PageDetails.Quiz.QuizType.TRUE_OR_FALSE -> convertFileToText(QUIZ_TRUE_OR_FALSE_PROMPT_RESOURCE)
                    else -> convertFileToText(QUIZ_SINGLE_CHOICE_PROMPT_RESOURCE)
                }

                GenAiRequest(
                    prompt = addPageAmountToPrompt(basePrompt, requestPageAmount),
                    jsonResponseSchema = convertFileToJson(QUIZ_JSON_SCHEMA),
                    content = content ?: ""
                )
            }

            PageDetailsType.ORDER_STORY -> {
                val basePrompt = convertFileToText(ORDER_STORY_PROMPT_RESOURCE)
                GenAiRequest(
                    prompt = addPageAmountToPrompt(basePrompt, requestPageAmount),
                    jsonResponseSchema = convertFileToJson(ORDER_STORY_JSON_SCHEMA),
                    content = content ?: ""
                )
            }

            PageDetailsType.READ -> throw IllegalArgumentException("READ type is not supported")
        }
    }

    override fun imageRequest(
        prompt: String
    ): GenAiRequest {
        return GenAiRequest(
            prompt = prompt,
            jsonResponseSchema = null,
            content = ""
        )
    }

    override fun splitTextRequest(content: String?): GenAiRequest {
        return GenAiRequest(
            prompt = convertFileToText(SPLIT_TEXT_PROMPT_RESOURCE),
            jsonResponseSchema = convertFileToJson(SPLIT_TEXT_JSON_SCHEMA),
            content = content ?: ""
        )
    }

    override fun personalizeTextParts(
        content: String?,
        personalizationAspects: PersonalizationAspects?
    ): GenAiRequest {
        val personalizedPrompt = addPersonalizationAspects(
            convertFileToText(PERSONALIZE_TEXT_PARTS_PROMPT_RESOURCE),
            personalizationAspects
        )

        return GenAiRequest(
            prompt = personalizedPrompt,
            jsonResponseSchema = convertFileToJson(SPLIT_TEXT_JSON_SCHEMA),
            content = content ?: ""
        )
    }

    override fun imageGenerationPromptForText(
        content: String?,
        personalizationAspects: PersonalizationAspects?
    ): GenAiRequest {
        val personalizedPrompt = addPersonalizationAspects(
            convertFileToText(IMAGE_GENERATION_PROMPT_FOR_TEXT_RESOURCE),
            personalizationAspects
        )

        return GenAiRequest(
            prompt = personalizedPrompt,
            jsonResponseSchema = convertFileToJson(IMAGE_GENERATION_PROMPT_FOR_TEXT_JSON_SCHEMA),
            content = content ?: ""
        )
    }

    private fun convertFileToText(resource: String): String {
        val text = context.assets.open(resource).bufferedReader().use { it.readText() }
        val cleanText = text
            .replace("\r", " ")
            .replace("\t", " ")
        Log.d(TAG, cleanText)
        return cleanText
    }

    private fun convertFileToJson(resource: String): JsonElement {
        val text = context.assets.open(resource).bufferedReader().use { it.readText() }
        return Json.parseToJsonElement(text)
    }

    override fun addPersonalizationAspects(
        basePrompt: String,
        personalizationAspects: PersonalizationAspects?
    ): String {
        if (personalizationAspects == null) return basePrompt

        return basePrompt
            .replace("<age>", personalizationAspects.age.toString())
            .replace("<name>", personalizationAspects.name)
            .replace("<interest>", personalizationAspects.interests.joinToString(", "))
            .replace("<image_style>", personalizationAspects.imageStyle)
    }

    override fun addPageAmountToPrompt(prompt: String, amount: Int): String {
        return prompt.replace("<amount>", amount.toString())
    }
}


