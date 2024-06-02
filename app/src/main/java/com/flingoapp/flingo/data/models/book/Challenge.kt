package com.flingoapp.flingo.data.models.book

import com.google.gson.annotations.SerializedName

data class Challenge(
    @SerializedName("challengeId") val id: String,
    @SerializedName("challengeType") val type: String,
    @SerializedName("difficulty") val difficulty: String,
    @SerializedName("content") val content: String,
    @SerializedName("images") var images: ArrayList<String>? = arrayListOf(),
    @SerializedName("answer") val answer: String,
    @SerializedName("hint") val hint: String,
    @SerializedName("feedback") var feedback: Feedback? = null,
    @SerializedName("timeLimit") var timeLimit: Int? = null,
    @SerializedName("score") var score: Int? = null
)

enum class ChallengeType {
    REMOVE_WORD,
    CONTEXT_BASED_QUESTIONS,
    CHANGE_CHARACTER,
}