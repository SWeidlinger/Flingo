package com.flingoapp.flingo.data.models.book

import com.flingoapp.flingo.EnumDeserializer
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName

data class Page(
    @SerializedName("pageId") val id: String,
    @JsonAdapter(EnumDeserializer::class) @SerializedName("pageType") val type: PageType,
    @SerializedName("pageCompleted") var completed: Boolean,
    @SerializedName("difficulty") val difficulty: String,
    @SerializedName("content") val content: String,
    @SerializedName("images") var images: ArrayList<String>? = arrayListOf(),
    @SerializedName("answer") val answer: String,
    @SerializedName("hint") val hint: String,
    @SerializedName("feedback") var feedback: Feedback? = null,
    @SerializedName("timeLimit") var timeLimit: Int? = null,
    @SerializedName("score") var score: Int? = null
)

enum class PageType {
    REMOVE_WORD,
    CONTEXT_BASED_QUESTIONS,
    CHANGE_CHARACTER,
    READ
}