package com.flingoapp.flingo.data.models.book.page

import com.flingoapp.flingo.EnumDeserializer
import com.flingoapp.flingo.data.models.book.Feedback
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
 * @property hint for this page
 * @property feedback for this page
 * @property timeLimit for this page
 * @property score for this page
 * @constructor Create new Page object
 */
data class Page(
    @SerializedName("pageId") val id: String,
    @SerializedName("pageDescription") val description: String,
    @SerializedName("pageCompleted") var isCompleted: Boolean,
    @SerializedName("difficulty") val difficulty: String,
    @SerializedName("hint") val hint: String,
    @SerializedName("timeLimit") var timeLimit: Int? = null,
    @SerializedName("score") var score: Int? = null,
    @SerializedName("feedback") var feedback: Feedback? = null,
    @SerializedName("taskDefinition") val taskDefinition: String = "",
    @JsonAdapter(EnumDeserializer::class) @SerializedName("pageType") val type: PageType,
    @JsonAdapter(PageDetailsDeserializer::class) @SerializedName("pageDetails") val details: PageDetails
)