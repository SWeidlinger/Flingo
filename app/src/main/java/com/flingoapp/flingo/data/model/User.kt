package com.flingoapp.flingo.data.model

import com.flingoapp.flingo.data.model.book.Book
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * User data class
 *
 * @property name of this user
 * @property created date this user was created
 * @property language language this user uses
 * @property profileImage of this user
 * @property currentReadingStreak of this user
 * @property books of this user
 * @constructor Create new User object
 */
@Serializable
data class User(
    @SerialName("name") val name: String,
    @SerialName("created") val created: String,
    @SerialName("language") val language: String,
    @SerialName("profileImage") val profileImage: String? = null,
    @SerialName("currentLives") val currentLives: Int,
    @SerialName("currentReadingStreak") val currentReadingStreak: Int,
    @SerialName("selectedInterests") val selectedInterests: ArrayList<String>,
    @SerialName("book") val books: List<Book>? = listOf()
)
