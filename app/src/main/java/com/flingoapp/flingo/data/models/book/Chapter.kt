package com.flingoapp.flingo.data.models.book

import com.flingoapp.flingo.EnumDeserializer
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName

/**
 * Chapter data class representing one chapter in a book
 *
 * @property id of this chapter
 * @property title of this chapter
 * @property type of this chapter, defined by [ChapterType]
 * @property description of this chapter
 * @property coverImage of this chapter
 * @property positionOffset used to calculate offset of this chapter in levelSelectionScreen
 * @property isCompleted completion parameter
 * @property pages available in this chapter
 * @constructor Create new Chapter object
 */
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

/**
 * Chapter type enum can either be a challenge type or read type, this enum is used for future proofing and
 * allow easy addition of new chapter types
 *
 */
enum class ChapterType {
    /**
     * Challenge type
     *
     */
    CHALLENGE,

    /**
     * Read type
     *
     */
    READ
}