package com.flingoapp.flingo.data.models.game

import com.google.gson.annotations.SerializedName


data class Challenges(
    @SerializedName("id") var id: String? = null,
    @SerializedName("content") var content: String? = null,
    @SerializedName("images") var images: ArrayList<String> = arrayListOf(),
    @SerializedName("answer") var answer: String? = null,
    @SerializedName("difficulty") var difficulty: String? = null,
    @SerializedName("hint") var hint: String? = null,
    @SerializedName("feedback") var feedback: Feedback? = Feedback(),
    @SerializedName("timeLimit") var timeLimit: Int? = null,
    @SerializedName("score") var score: Int? = null
)