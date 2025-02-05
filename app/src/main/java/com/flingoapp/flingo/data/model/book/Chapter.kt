package com.flingoapp.flingo.data.model.book

import com.flingoapp.flingo.data.model.book.page.Page
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
@Serializable
data class Chapter(
    @SerialName("chapterId") val id: String,
    @SerialName("chapterTitle") val title: String,
    @SerialName("chapterType") val type: ChapterType,
    @SerialName("chapterDescription") val description: String,
    @SerialName("chapterCoverImage") val coverImage: String? = null,
    @SerialName("chapterPositionOffset") val positionOffset: Float,
    //TODO: make immutable
    @SerialName("chapterCompleted") var isCompleted: Boolean,
    @SerialName("pages") val pages: ArrayList<Page>? = arrayListOf()
)

/**
 * Chapter type enum can either be a challenge, read or mixed type, this enum is used for future proofing and
 * allowing easy addition of new chapter types
 *
 */
@Serializable
enum class ChapterType {
    /**
     * Challenge type
     *
     */
    @SerialName("challenge")
    CHALLENGE,

    /**
     * Read type
     *
     */
    @SerialName("read")
    READ,

    /**
     * Mixed type can contain both challenge and read types
     *
     */
    @SerialName("mixed")
    MIXED
}