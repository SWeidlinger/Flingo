package com.flingoapp.flingo.data.datasource

import android.content.Context
import com.flingoapp.flingo.data.model.Book
import com.flingoapp.flingo.data.model.Chapter
import com.flingoapp.flingo.data.model.page.Page
import com.flingoapp.flingo.viewmodel.PersonalizationAspects
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface PersonalizationDataSource {
    fun getPersonalizedBookPrompt(personalizationAspects: PersonalizationAspects): String
    fun getPersonalizedChapterPrompt(
        personalizationAspects: PersonalizationAspects,
        sourceBook: Book
    ): String

    fun getPersonalizedPagePrompt(
        personalizationAspects: PersonalizationAspects,
        sourceChapter: Chapter
    ): String

    fun getPersonalizedImagePrompt(
        personalizationAspects: PersonalizationAspects,
        imageContext: String
    ): String
}

class PersonalizationDataSourceImpl(
    private val context: Context
) : PersonalizationDataSource {
    companion object {
        const val TAG = "PersonalizationDataSourceImpl"

        private const val BOOK_INSTRUCTION_PROMPT =
            "You are a renowned children's book author specializing in personalized educational content." +
                    "Your task is to adapt the following JSON to match the preferences and learning needs of a specific child while maintaining the structure of the original content." +
                    "The child is <age> years old, named <name>, and has a strong interest in <interest>. He is working on improving his reading skills.\n" +
                    "\n" +
                    "Modify the text and story elements to align with the interest of <name> while keeping the content engaging and educational." +
                    "Ensure that the difficulty level is appropriate for his age and supports his reading development. " +
                    "Do not add new fields, game modes, or modify the JSON structure—only adapt the content to make it more engaging and relevant for <name>.\n" +
                    "\n" +
                    "The text must be in German and should enhance the motivation of <name> to read while making learning more enjoyable."

        private const val CHAPTER_INSTRUCTION_PROMPT =
            "You are a renowned children's book author specializing in personalized educational content. Your task is to generate a single chapter based on an existing JSON that contains multiple chapters.\n" +
                    "\n" +
                    "First, select the a suitable chapter from the given list.\n" +
                    "Then, generate a new JSON object of that chapter, adapting the content to match the child's preferences while maintaining the original structure.\n" +
                    "The child is <age> years old, named <name>, and has a strong interest in <interest>. The goal is to make the chapter engaging, educational, and age-appropriate.\n" +
                    "\n" +
                    "Rules:\n" +
                    "Do not add new fields or game modes—only adapt the selected chapter.\n" +
                    "The added chapter must not be of chapterType read.\n" +
                    "Keep the exact same JSON structure.\n" +
                    "The text must be in German and should encourage reading motivation while making learning enjoyable."

        private const val PAGE_INSTRUCTION_PROMPT = ""

        private const val IMAGE_INSTRUCTION_PROMPT =
            "Generate a colorful, engaging illustration in the style of a children's reading-learning book. " +
                    "The image should be child-friendly, visually appealing, and designed to support early literacy. " +
                    "Ensure it aligns with the given context: <context>, using bright colors, simple shapes, and expressive characters to make the scene inviting and educational."

        private const val FULL_BOOK_RESOURCE_JSON = "prompt_examples/full_book.json"
    }

    override fun getPersonalizedBookPrompt(personalizationAspects: PersonalizationAspects): String {
        //TODO: change to not use full_book_resource_json
        val instructionPrompt =
            buildPersonalizedPrompt(prompt = BOOK_INSTRUCTION_PROMPT, personalizationAspects)
        val sourceJson =
            context.assets.open(FULL_BOOK_RESOURCE_JSON).bufferedReader().use { it.readText() }

        val finalPrompt = instructionPrompt + sourceJson

        return finalPrompt
    }

    override fun getPersonalizedChapterPrompt(
        personalizationAspects: PersonalizationAspects,
        sourceBook: Book
    ): String {
        val instructionPrompt =
            buildPersonalizedPrompt(prompt = CHAPTER_INSTRUCTION_PROMPT, personalizationAspects)
        val sourceJson = Json.encodeToString<List<Chapter>>(sourceBook.chapters)

        val finalPrompt = instructionPrompt + sourceJson

        return finalPrompt
    }

    override fun getPersonalizedPagePrompt(
        personalizationAspects: PersonalizationAspects,
        sourceChapter: Chapter
    ): String {
        val instructionPrompt =
            buildPersonalizedPrompt(prompt = PAGE_INSTRUCTION_PROMPT, personalizationAspects)
        val sourceJson =
            sourceChapter.pages?.let { Json.encodeToString<List<Page>>(sourceChapter.pages) } ?: ""

        val finalPrompt = instructionPrompt + sourceJson

        return finalPrompt
    }

    override fun getPersonalizedImagePrompt(
        personalizationAspects: PersonalizationAspects,
        imageContext: String
    ): String {
        //TODO: improve
        return IMAGE_INSTRUCTION_PROMPT.replace("<context>", imageContext)
    }

    private fun buildPersonalizedPrompt(
        prompt: String,
        personalizationAspects: PersonalizationAspects
    ): String {
        return prompt
            .replace("<age>", personalizationAspects.age.toString())
            .replace("<name>", personalizationAspects.name)
            .replace("<interest>", personalizationAspects.interests.first())
    }
}