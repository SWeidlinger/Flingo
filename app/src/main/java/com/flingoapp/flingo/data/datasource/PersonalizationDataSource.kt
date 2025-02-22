package com.flingoapp.flingo.data.datasource

import com.flingoapp.flingo.data.model.Book
import com.flingoapp.flingo.data.model.Chapter
import com.flingoapp.flingo.data.model.genAi.GenAiBasePrompts
import com.flingoapp.flingo.data.model.page.Page
import com.flingoapp.flingo.viewmodel.PersonalizationAspects
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface PersonalizationDataSource {
    fun getPersonalizedBookPrompt(
        genAiBasePrompts: GenAiBasePrompts,
        personalizationAspects: PersonalizationAspects
    ): String

    fun getPersonalizedChapterPrompt(
        genAiBasePrompts: GenAiBasePrompts,
        personalizationAspects: PersonalizationAspects,
        sourceBook: Book
    ): String

    fun getPersonalizedPagePrompt(
        genAiBasePrompts: GenAiBasePrompts,
        personalizationAspects: PersonalizationAspects,
        sourceChapter: Chapter
    ): String

    fun getPersonalizedImagePrompt(
        genAiBasePrompts: GenAiBasePrompts,
        personalizationAspects: PersonalizationAspects,
        imageContext: String
    ): String
}

class PersonalizationDataSourceImpl : PersonalizationDataSource {
    companion object {
        const val TAG = "PersonalizationDataSourceImpl"
    }

    override fun getPersonalizedBookPrompt(
        genAiBasePrompts: GenAiBasePrompts,
        personalizationAspects: PersonalizationAspects,
    ): String {
        //TODO: change to not use full_book_resource_json
        val instructionPrompt =
            buildPersonalizedPrompt(
                prompt = genAiBasePrompts.bookInstructionPrompt(),
                personalizationAspects
            )

        val finalPrompt = instructionPrompt

        return finalPrompt
    }

    override fun getPersonalizedChapterPrompt(
        genAiBasePrompts: GenAiBasePrompts,
        personalizationAspects: PersonalizationAspects,
        sourceBook: Book
    ): String {
        val instructionPrompt =
            buildPersonalizedPrompt(
                prompt = genAiBasePrompts.chapterInstructionPrompt(),
                personalizationAspects
            )
        val sourceJson = Json.encodeToString<List<Chapter>>(sourceBook.chapters)

        val finalPrompt = instructionPrompt + sourceJson

        return finalPrompt
    }

    override fun getPersonalizedPagePrompt(
        genAiBasePrompts: GenAiBasePrompts,
        personalizationAspects: PersonalizationAspects,
        sourceChapter: Chapter
    ): String {
        val instructionPrompt =
            buildPersonalizedPrompt(
                prompt = genAiBasePrompts.pageInstructionPrompt(),
                personalizationAspects
            )
        val sourceJson =
            sourceChapter.pages?.let { Json.encodeToString<List<Page>>(sourceChapter.pages) } ?: ""

        val finalPrompt = instructionPrompt + sourceJson

        return finalPrompt
    }

    override fun getPersonalizedImagePrompt(
        genAiBasePrompts: GenAiBasePrompts,
        personalizationAspects: PersonalizationAspects,
        imageContext: String
    ): String {
        //TODO: improve
//        return IMAGE_INSTRUCTION_PROMPT.replace("<context>", imageContext)
        return ""
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