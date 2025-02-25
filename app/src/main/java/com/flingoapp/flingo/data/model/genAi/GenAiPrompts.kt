package com.flingoapp.flingo.data.model.genAi

import android.content.Context
import android.util.Log
import com.flingoapp.flingo.viewmodel.PersonalizationAspects
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

interface GenAiRequestBuilder {
    //general
    fun bookRequest(
        content: String?,
        personalizationAspects: PersonalizationAspects?
    ): GenAiRequest

    fun chapterRequest(
        content: String?,
        personalizationAspects: PersonalizationAspects?
    ): GenAiRequest

    fun pageRequest(
        content: String?,
        personalizationAspects: PersonalizationAspects?
    ): GenAiRequest

    fun imageRequest(prompt: String): GenAiRequest

    //specific
    fun splitTextRequest(
        content: String?
    ): GenAiRequest

    fun personalizeTextParts(
        content: String?,
        personalizationAspects: PersonalizationAspects?
    ): GenAiRequest

    fun imageGenerationPromptForText(
        content: String?
    ): GenAiRequest
}

data class GenAiRequest(
    val prompt: String,
    val jsonResponseSchema: JsonElement? = null,
    val content: String,
)

class GenAiRequestBuilderDefaultImpl(val context: Context) : GenAiRequestBuilder {
    companion object {
        private const val TAG = "GenAiBasePromptsDefaultImpl"

        //general
        private const val BOOK_PROMPT_RESOURCE = "base_prompts/default/book_instruction.md"
        private const val CHAPTER_PROMPT_RESOURCE = "base_prompts/default/chapter_instruction.md"
        private const val PAGE_PROMPT_RESOURCE = "base_prompts/default/page_instruction.md"
        private const val IMAGE_INSTRUCTION_PROMPT =
            "Generate a colorful, engaging illustration in the style of a children's reading-learning book. " +
                    "The image should be child-friendly, visually appealing, and designed to support early literacy. " +
                    "Ensure it aligns with the given context: <context>, using bright colors, simple shapes, and expressive characters to make the scene inviting and educational."

        //specific
        private const val SPLIT_TEXT_PROMPT_RESOURCE = "base_prompts/default/split_text.md"
        private const val SPLIT_TEXT_JSON_SCHEMA = "base_response_schema/default/split_text.json"

        private const val PERSONALIZE_TEXT_PARTS_PROMPT_RESOURCE =
            "base_prompts/default/personalize_text_parts.md"

        private const val IMAGE_GENERATION_PROMPT_FOR_TEXT_RESOURCE =
            "base_prompts/default/generate_image_prompt_for_text.md"
        private const val IMAGE_GENERATION_PROMPT_FOR_TEXT_JSON_SCHEMA =
            "base_response_schema/default/generate_image_prompt_for_text.json"
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

    override fun imageGenerationPromptForText(content: String?): GenAiRequest {
        //TODO: add image personalization
        return GenAiRequest(
            prompt = convertFileToText(IMAGE_GENERATION_PROMPT_FOR_TEXT_RESOURCE),
            jsonResponseSchema = convertFileToJson(IMAGE_GENERATION_PROMPT_FOR_TEXT_JSON_SCHEMA),
            content = content ?: ""
        )
    }

    private fun convertFileToText(resource: String): String {
        val text = context.assets.open(resource).bufferedReader().use { it.readText() }
        val cleanText = text.replace("\r", "").replace("\n", "")
        Log.d(TAG, cleanText)
        return cleanText
    }

    private fun convertFileToJson(resource: String): JsonElement {
        val text = context.assets.open(resource).bufferedReader().use { it.readText() }
        return Json.parseToJsonElement(text)
    }

    private fun addPersonalizationAspects(
        basePrompt: String,
        personalizationAspects: PersonalizationAspects?
    ): String {
        if (personalizationAspects == null) return basePrompt

        return basePrompt
            .replace("<age>", personalizationAspects.age.toString())
            .replace("<name>", personalizationAspects.name)
            .replace("<interest>", personalizationAspects.interests.first())
    }
}


