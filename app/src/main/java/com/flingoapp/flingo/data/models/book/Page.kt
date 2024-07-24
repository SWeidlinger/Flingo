package com.flingoapp.flingo.data.models.book

import com.flingoapp.flingo.EnumDeserializer
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName

/**
 * Page data class
 *
 * @property id of this page
 * @property type of this page defined by [PageType]
 * @property description of this page
 * @property isCompleted completion parameter
 * @property difficulty of this page, right now not explicitly defined but will be (easy, medium, hard)
 * @property content of this page
 * @property images of this page
 * @property answer of this page
 * @property hint for this page
 * @property feedback for this page
 * @property timeLimit for this page
 * @property score for this page
 * @constructor Create new Page object
 */
data class Page(
    @SerializedName("pageId") val id: String,
    @JsonAdapter(EnumDeserializer::class) @SerializedName("pageType") val type: PageType,
    @SerializedName("pageDescription") val description: String,
    @SerializedName("pageCompleted") var isCompleted: Boolean,
    @SerializedName("difficulty") val difficulty: String,
    @SerializedName("content") val content: String,
    @SerializedName("images") var images: ArrayList<String>? = arrayListOf(),
    @SerializedName("answer") val answer: String,
    @SerializedName("hint") val hint: String,
    @SerializedName("feedback") var feedback: Feedback? = null,
    @SerializedName("timeLimit") var timeLimit: Int? = null,
    @SerializedName("score") var score: Int? = null
)

/**
 * Page type enum can either be remove word type, context based questions type, change character type,
 * currently not in use
 *
 * @constructor Create empty Page type
 */
enum class PageType {
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
     * Change Character type
     *
     */
    CHANGE_CHARACTER
}