//package com.flingoapp.flingo.data.datasource
//
//import com.flingoapp.flingo.data.model.Book
//import com.flingoapp.flingo.data.model.Chapter
//import com.flingoapp.flingo.data.model.genAi.GenAiRequestBuilder
//import com.flingoapp.flingo.data.model.page.Page
//import com.flingoapp.flingo.viewmodel.PersonalizationAspects
//import kotlinx.serialization.encodeToString
//import kotlinx.serialization.json.Json
//
//interface PersonalizationDataSource {
//    fun getPersonalizedBookPrompt(
//        genAiRequestBuilder: GenAiRequestBuilder,
//        personalizationAspects: PersonalizationAspects
//    ): String
//
//    fun getPersonalizedChapterPrompt(
//        genAiRequestBuilder: GenAiRequestBuilder,
//        personalizationAspects: PersonalizationAspects,
//        sourceBook: Book
//    ): String
//
//    fun getPersonalizedPagePrompt(
//        genAiRequestBuilder: GenAiRequestBuilder,
//        personalizationAspects: PersonalizationAspects,
//        sourceChapter: Chapter
//    ): String
//
//    fun getPersonalizedImagePrompt(
//        genAiRequestBuilder: GenAiRequestBuilder,
//        personalizationAspects: PersonalizationAspects,
//        imageContext: String
//    ): String
//}
//
//class PersonalizationDataSourceImpl : PersonalizationDataSource {
//    companion object {
//        const val TAG = "PersonalizationDataSourceImpl"
//    }
//
//    override fun getPersonalizedBookPrompt(
//        genAiRequestBuilder: GenAiRequestBuilder,
//        personalizationAspects: PersonalizationAspects,
//    ): String {
//        val instructionPrompt =
//            buildPersonalizedPrompt(
//                prompt = genAiRequestBuilder.bookRequest().prompt,
//                personalizationAspects
//            )
//
//        val finalPrompt = instructionPrompt
//
//        return finalPrompt
//    }
//
//    override fun getPersonalizedChapterPrompt(
//        genAiRequestBuilder: GenAiRequestBuilder,
//        personalizationAspects: PersonalizationAspects,
//        sourceBook: Book
//    ): String {
//        val instructionPrompt =
//            buildPersonalizedPrompt(
//                prompt = genAiRequestBuilder.chapterRequest().prompt,
//                personalizationAspects
//            )
//        val sourceJson = Json.encodeToString<List<Chapter>>(sourceBook.chapters)
//
//        val finalPrompt = instructionPrompt + sourceJson
//
//        return finalPrompt
//    }
//
//    override fun getPersonalizedPagePrompt(
//        genAiRequestBuilder: GenAiRequestBuilder,
//        personalizationAspects: PersonalizationAspects,
//        sourceChapter: Chapter
//    ): String {
//        val instructionPrompt =
//            buildPersonalizedPrompt(
//                prompt = genAiRequestBuilder.pageRequest().prompt,
//                personalizationAspects
//            )
//        val sourceJson =
//            sourceChapter.pages?.let { Json.encodeToString<List<Page>>(sourceChapter.pages) } ?: ""
//
//        val finalPrompt = instructionPrompt + sourceJson
//
//        return finalPrompt
//    }
//
//    override fun getPersonalizedImagePrompt(
//        genAiRequestBuilder: GenAiRequestBuilder,
//        personalizationAspects: PersonalizationAspects,
//        imageContext: String
//    ): String {
//        //TODO: improve
////        return IMAGE_INSTRUCTION_PROMPT.replace("<context>", imageContext)
//        return ""
//    }
//
//    private fun buildPersonalizedPrompt(
//        prompt: String,
//        personalizationAspects: PersonalizationAspects
//    ): String {
//        return prompt
//            .replace("<age>", personalizationAspects.age.toString())
//            .replace("<name>", personalizationAspects.name)
//            .replace("<interest>", personalizationAspects.interests.first())
//    }
//}