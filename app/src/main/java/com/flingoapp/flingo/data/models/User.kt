package com.flingoapp.flingo.data.models

import com.flingoapp.flingo.data.models.book.Book
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
    @SerialName("profileImage") var profileImage: String? = null,
    @SerialName("currentReadingStreak") var currentReadingStreak: Int = 0,
    @SerialName("currentReadingStreak") var currentReadingStreak: Int,
    @SerialName("selectedInterests") val selectedInterests: ArrayList<String>,
    @SerialName("books") var books: ArrayList<Book>? = arrayListOf()
)
