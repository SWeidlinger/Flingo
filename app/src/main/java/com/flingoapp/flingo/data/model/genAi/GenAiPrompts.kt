package com.flingoapp.flingo.data.model.genAi

import android.content.Context
import android.util.Log

interface GenAiBasePrompts {
    //general
    fun bookInstructionPrompt(): String
    fun chapterInstructionPrompt(): String
    fun pageInstructionPrompt(): String

    //specific
    fun separateTextPrompt(): String
}

class GenAiBasePromptsDefaultImpl(val context: Context) : GenAiBasePrompts {
    companion object {
        private const val TAG = "GenAiBasePromptsDefaultImpl"

        private const val SEPARATE_TEXT_RESOURCE = "base_prompts/default/separate_text.md"
        private const val BOOK_INSTRUCTION_RESOURCE = "base_prompts/default/book_instruction.md"
        private const val CHAPTER_INSTRUCTION_RESOURCE =
            "base_prompts/default/chapter_instruction.md"
        private const val PAGE_INSTRUCTION_RESOURCE = "base_prompts/default/page_instruction.md"
        private const val IMAGE_INSTRUCTION_PROMPT =
            "Generate a colorful, engaging illustration in the style of a children's reading-learning book. " +
                    "The image should be child-friendly, visually appealing, and designed to support early literacy. " +
                    "Ensure it aligns with the given context: <context>, using bright colors, simple shapes, and expressive characters to make the scene inviting and educational."
    }

    override fun bookInstructionPrompt(): String {
        return convertFileToText(BOOK_INSTRUCTION_RESOURCE)
    }

    override fun chapterInstructionPrompt(): String {
        return convertFileToText(CHAPTER_INSTRUCTION_RESOURCE)
    }

    override fun pageInstructionPrompt(): String {
        return convertFileToText(PAGE_INSTRUCTION_RESOURCE)
    }

    override fun separateTextPrompt(): String {
        return convertFileToText(SEPARATE_TEXT_RESOURCE)
    }

    private fun convertFileToText(resource: String): String {
        val text = context.assets.open(resource).bufferedReader().use { it.readText() }
        Log.d(TAG, text)
        return text
    }
}


