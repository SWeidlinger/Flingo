package com.flingoapp.flingo.data.model.genAi

import PageDetails
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed interface GenAiResponse {
    @Serializable
    data class SplitTextResponse(
        val title: String,
        @SerialName("text_parts") val content: List<String>
    ) : GenAiResponse

    @Serializable
    data class ImagePromptsResponse(
        @SerialName("image_prompts") val prompts: List<String>
    ) : GenAiResponse

    @Serializable
    data class PageDetailsQuizResponse(
        val chapterTitle: String,
        val taskDefinition: String,
        val questions: List<Question>,
    ) : GenAiResponse {
        @Serializable
        data class Question(
            val question: String,
            val answers: List<PageDetails.Quiz.Answer>
        )
    }

    @Serializable
    data class PageDetailsRemoveWordResponse(
        val chapterTitle: String,
        val taskDefinition: String,
        val sentences: List<Sentence>,
    ) {
        @Serializable
        data class Sentence(
            val sentence: String,
            val answer: String
        )
    }

    @Serializable
    data class PageDetailsOrderStoryResponse(
        val chapterTitle: String,
        val taskDefinition: String,
        val content: List<Task>,
    ) {
        @Serializable
        data class Task(
            val snippets: List<Snippet>,
            val correctOrder: List<Int>
        )

        @Serializable
        data class Snippet(
            val id: Int,
            val text: String
        )
    }
}