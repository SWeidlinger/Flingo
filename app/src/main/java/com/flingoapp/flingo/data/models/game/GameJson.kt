package com.flingoapp.flingo.data.models.game

import com.google.gson.annotations.SerializedName

data class GameJson(
    @SerializedName("author") var author: String? = null,
    @SerializedName("date") var date: String? = null,
    @SerializedName("gameType") var gameType: String? = null,
    @SerializedName("gameContent") var gameContent: GameContent? = GameContent()
)