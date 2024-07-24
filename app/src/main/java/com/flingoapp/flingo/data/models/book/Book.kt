package com.flingoapp.flingo.data.models.book

import com.google.gson.annotations.SerializedName

/**
 * Book data class represents the most outer level of the JSON object
 *
 * @property author of this book
 * @property date this book was created
 * @property language this books is written in
 * @property version this book version
 * @property title of this book
 * @property description of this book
 * @property coverImage of this book
 * @property chapters of this book
 * @constructor Create new Book object
 */
data class Book(
    @SerializedName("author") val author: String,
    @SerializedName("date") val date: String,
    @SerializedName("language") val language: String,
    @SerializedName("version") val version: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("coverImage") var coverImage: String? = null,
    @SerializedName("chapters") val chapters: ArrayList<Chapter> = arrayListOf()
)