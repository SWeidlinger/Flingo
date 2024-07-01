package com.flingoapp.flingo.data.models.book

import com.flingoapp.flingo.EnumDeserializer
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName

data class Chapter(
    @SerializedName("chapterId") val id: String,
    @SerializedName("chapterTitle") val title: String,
    @JsonAdapter(EnumDeserializer::class) @SerializedName("chapterType") val type: ChapterType,
    @SerializedName("chapterDescription") val description: String,
    @SerializedName("chapterCoverImage") var coverImage: String? = null,
    @SerializedName("chapterPositionOffset") val positionOffset: Float,
    @SerializedName("chapterCompleted") var isCompleted: Boolean,
    @SerializedName("pages") var pages: ArrayList<Page>? = arrayListOf()
)

enum class ChapterType {
    CHALLENGE,
    READ
}