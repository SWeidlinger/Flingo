package com.flingoapp.flingo.data.model.book

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Feedback data class
 *
 * @property correct string showed, if page got completed correctly
 * @property incorrect string showed, if page got completed incorrectly
 */
@Serializable
data class Feedback(
    @SerialName("correct") val correct: String? = null,
    @SerialName("incorrect") val incorrect: String? = null
)