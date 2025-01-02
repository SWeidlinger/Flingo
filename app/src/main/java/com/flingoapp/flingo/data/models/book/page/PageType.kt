package com.flingoapp.flingo.data.models.book.page

/**
 * Page type enum can either be remove word type, context based questions type, change character type,
 * currently not in use
 *
 * @constructor Create empty Page type
 */
enum class PageType {
    /**
     * Reading page
     *
     */
    READ,

    /**
     * Remove Word type
     *
     */
    REMOVE_WORD,

    /**
     * Context Based Questions type
     *
     */
    CONTEXT_BASED_QUESTIONS,

    /**
     * Order Story type
     *
     */
    ORDER_STORY
}