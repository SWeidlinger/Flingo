package com.flingoapp.flingo.data.models.book

import com.google.gson.annotations.SerializedName

/**
 * Feedback data class
 *
 * @property correct string showed, if page got completed correctly
 * @property incorrect string showed, if page got completed incorrectly
 */
data class Feedback(
    @SerializedName("correct") var correct: String? = null,
    @SerializedName("incorrect") var incorrect: String? = null
)