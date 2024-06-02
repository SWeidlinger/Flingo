package com.flingoapp.flingo.data.models

import com.flingoapp.flingo.data.models.book.Book
import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("name") val name: String,
    @SerializedName("created") val created: String,
    @SerializedName("language") val language: String,
    @SerializedName("profileImage") var profileImage: String? = null,
    @SerializedName("currentReadingStreak") var currentReadingStreak: Int = 0,
    @SerializedName("books") var books: ArrayList<Book>? = arrayListOf()
)
