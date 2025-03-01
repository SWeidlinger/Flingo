package com.flingoapp.flingo.data.model

import com.flingoapp.flingo.data.model.page.Page
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.UUID
import kotlin.random.Random

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
    @Transient val author: String = "Author",
    @Transient val id: String = UUID.randomUUID().toString(),
    @SerialName("chapterTitle") val title: String,
    @SerialName("chapterType") val type: ChapterType,
    @SerialName("chapterDescription") val description: String,
    @SerialName("chapterCoverImage") val coverImage: String? = null,
    //TODO: make immutable
    @Transient @SerialName("chapterCompleted") var isCompleted: Boolean = false,
    @SerialName("pages") val pages: List<Page>? = listOf(),
    @Transient val positionOffset: Float = Random.nextFloat(),
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