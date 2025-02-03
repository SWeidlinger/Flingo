package com.flingoapp.flingo.data.models.book.page

import com.flingoapp.flingo.data.models.book.Feedback
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
@Serializable
data class Page(
    @SerialName("pageId") val id: String,
    @SerialName("pageDescription") val description: String,
    //TODO: make immutable
    @SerialName("pageCompleted") var isCompleted: Boolean,
    @SerialName("difficulty") val difficulty: String,
    @SerialName("hint") val hint: String,
    @SerialName("timeLimit") val timeLimit: Int?,
    @SerialName("score") val score: Int?,
    @SerialName("feedback") val feedback: Feedback?,
    @SerialName("taskDefinition") val taskDefinition: String,
    @SerialName("pageType") val type: PageType,
    @Serializable(with = PageDetailsSerializer::class) @SerialName("pageDetails") val details: PageDetails
)