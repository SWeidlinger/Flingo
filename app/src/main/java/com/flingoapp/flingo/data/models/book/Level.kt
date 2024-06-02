package com.flingoapp.flingo.data.models.book

import com.google.gson.annotations.SerializedName

data class Level(
    @SerializedName("levelId") val id: String,
    @SerializedName("levelTitle") val title: String,
    @SerializedName("levelDescription") val description: String,
    @SerializedName("levelCoverImage") var coverImage: String? = null,
    @SerializedName("levelCompleted") var completed: Boolean,
    @SerializedName("levelChallenges") var challenges: ArrayList<Challenge>? = arrayListOf()
)