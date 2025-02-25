package com.flingoapp.flingo.data.model.genAi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed interface GenAiResponse {
    @Serializable
    data class SplitTextResponse(
        val title: String,
        @SerialName("text_parts") val content: List<String>
    ) : GenAiResponse

    @Serializable
    data class ImagePromptsResponse(
        @SerialName("image_prompts") val prompts: List<String>
    ) : GenAiResponse
}