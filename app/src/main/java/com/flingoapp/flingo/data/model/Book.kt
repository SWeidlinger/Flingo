package com.flingoapp.flingo.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.Calendar
import java.util.UUID

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
    @Transient val id: String = UUID.randomUUID().toString(),
    @SerialName("author") val author: String,
    @SerialName("date") val date: String = Calendar.getInstance().time.toString(),
    @SerialName("language") val language: String = "de",
    @SerialName("version") val version: String = "1.0",
    @SerialName("title") val title: String,
    @SerialName("description") val description: String,
    @SerialName("coverImage") val coverImage: String?,
    @SerialName("chapters") val chapters: List<Chapter> = listOf()
)