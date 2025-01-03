package com.flingoapp.flingo.data.models.book.page

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Page type enum can either be remove word type, context based questions type, change character type,
 * currently not in use
 *
 * @constructor Create empty Page type
 */
@Serializable
enum class PageType {
    /**
     * Reading page
     *
     */
    @SerialName("read")
    READ,

    /**
     * Remove Word type
     *
     */
    @SerialName("remove_word")
    REMOVE_WORD,

    /**
     * Context Based Questions type
     *
     */
    @SerialName("context_based_questions")
    CONTEXT_BASED_QUESTIONS,

    /**
     * Order Story type
     *
     */
    @SerialName("order_story")
    ORDER_STORY
}