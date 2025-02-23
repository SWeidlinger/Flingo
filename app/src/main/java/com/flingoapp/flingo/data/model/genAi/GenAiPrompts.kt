package com.flingoapp.flingo.data.model.genAi

import android.content.Context
import android.util.Log
import com.flingoapp.flingo.viewmodel.PersonalizationAspects

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

    //specific
    fun splitTextRequest(
        content: String?,
        personalizationAspects: PersonalizationAspects?
    ): GenAiRequest
}

data class GenAiRequest(
    val prompt: String,
    val jsonResponseScheme: String? = null,
    val content: String,
)

class GenAiRequestBuilderDefaultImpl(val context: Context) : GenAiRequestBuilder {
    companion object {
        private const val TAG = "GenAiBasePromptsDefaultImpl"

        private const val SPLIT_TEXT_PROMPT_RESOURCE = "base_prompts/default/split_text.md"
        private const val SPLIT_TEXT_JSON_SCHEMA = "base_response_schema/default/split_text.json"

        private const val BOOK_PROMPT_RESOURCE = "base_prompts/default/book_instruction.md"

        private const val CHAPTER_PROMPT_RESOURCE = "base_prompts/default/chapter_instruction.md"

        private const val PAGE_PROMPT_RESOURCE = "base_prompts/default/page_instruction.md"

        private const val IMAGE_INSTRUCTION_PROMPT =
            "Generate a colorful, engaging illustration in the style of a children's reading-learning book. " +
                    "The image should be child-friendly, visually appealing, and designed to support early literacy. " +
                    "Ensure it aligns with the given context: <context>, using bright colors, simple shapes, and expressive characters to make the scene inviting and educational."
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
            jsonResponseScheme = null,
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
            jsonResponseScheme = null,
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
            jsonResponseScheme = null,
            content = content ?: ""
        )
    }

    override fun splitTextRequest(
        content: String?,
        personalizationAspects: PersonalizationAspects?
    ): GenAiRequest {
        val personalizedPrompt = addPersonalizationAspects(
            convertFileToText(SPLIT_TEXT_PROMPT_RESOURCE),
            personalizationAspects
        )

        return GenAiRequest(
            prompt = personalizedPrompt,
            jsonResponseScheme = convertFileToText(SPLIT_TEXT_JSON_SCHEMA),
            content = content ?: ""
        )
    }

    private fun convertFileToText(resource: String): String {
        val text = context.assets.open(resource).bufferedReader().use { it.readText() }
        Log.d(TAG, text)
        return text
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


