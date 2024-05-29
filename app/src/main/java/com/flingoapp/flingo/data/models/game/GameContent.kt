package com.flingoapp.flingo.data.models.game

import com.google.gson.annotations.SerializedName


data class GameContent(
    @SerializedName("title") var title: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("version") var version: String? = null,
    @SerializedName("language") var language: String? = null,
    @SerializedName("challenges") var data: Challenges? = Challenges()
)