package com.flingoapp.flingo.data.models.game

import com.google.gson.annotations.SerializedName


data class Feedback (
  @SerializedName("correct"   ) var correct   : String? = null,
  @SerializedName("incorrect" ) var incorrect : String? = null
)