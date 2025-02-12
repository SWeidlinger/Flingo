package com.flingoapp.flingo.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Book data class represents the most outer level of the JSON object
 *
 * @property author of this book
 * @property date this book was created
 * @property language this book is written in
 * @property version this book version
 * @property title of this book
 * @property description of this book
 * @property coverImage of this book
 * @property chapters of this book
 * @constructor Create new Book object
 */
@Serializable
data class Book(
    @SerialName("author") val author: String,
    @SerialName("date") val date: String,
    @SerialName("language") val language: String,
    @SerialName("version") val version: String,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String,
    @SerialName("coverImage") val coverImage: String?,
    @SerialName("chapters") val chapters: List<Chapter> = listOf()
)