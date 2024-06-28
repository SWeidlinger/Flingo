package com.flingoapp.flingo.data.models.book

import com.google.gson.annotations.SerializedName

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