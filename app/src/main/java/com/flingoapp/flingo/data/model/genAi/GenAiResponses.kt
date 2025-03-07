package com.flingoapp.flingo.data.model.genAi

import PageDetails
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Gen ai response
 *
 * @constructor Create empty Gen ai response
 */
sealed interface GenAiResponse {
    /**
     * Split text response
     *
     * @property title
     * @property content
     * @constructor Create empty Split text response
     */
    @Serializable
    data class SplitTextResponse(
        val title: String,
        @SerialName("text_parts") val content: List<String>
    ) : GenAiResponse

    /**
     * Image prompts response
     *
     * @property prompts
     * @constructor Create empty Image prompts response
     */
    @Serializable
    data class ImagePromptsResponse(
        @SerialName("image_prompts") val prompts: List<String>
    ) : GenAiResponse

    /**
     * Page details quiz response
     *
     * @property chapterTitle
     * @property taskDefinition
     * @property questions
     * @constructor Create empty Page details quiz response
     */
    @Serializable
    data class PageDetailsQuizResponse(
        val chapterTitle: String,
        val taskDefinition: String,
        val questions: List<Question>,
    ) : GenAiResponse {
        /**
         * Question
         *
         * @property question
         * @property answers
         * @constructor Create empty Question
         */
        @Serializable
        data class Question(
            val question: String,
            val answers: List<PageDetails.Quiz.Answer>
        )
    }

    /**
     * Page details remove word response
     *
     * @property chapterTitle
     * @property taskDefinition
     * @property sentences
     * @constructor Create empty Page details remove word response
     */
    @Serializable
    data class PageDetailsRemoveWordResponse(
        val chapterTitle: String,
        val taskDefinition: String,
        val sentences: List<Sentence>,
    ) {
        /**
         * Sentence
         *
         * @property sentence
         * @property answer
         * @constructor Create empty Sentence
         */
        @Serializable
        data class Sentence(
            val sentence: String,
            val answer: String
        )
    }

    /**
     * Page details order story response
     *
     * @property chapterTitle
     * @property taskDefinition
     * @property content
     * @constructor Create empty Page details order story response
     */
    @Serializable
    data class PageDetailsOrderStoryResponse(
        val chapterTitle: String,
        val taskDefinition: String,
        val content: List<Task>,
    ) {
        /**
         * Task
         *
         * @property snippets
         * @property correctOrder
         * @constructor Create empty Task
         */
        @Serializable
        data class Task(
            val snippets: List<Snippet>,
            val correctOrder: List<Int>
        )

        /**
         * Snippet
         *
         * @property id
         * @property text
         * @constructor Create empty Snippet
         */
        @Serializable
        data class Snippet(
            val id: Int,
            val text: String
        )
    }
}