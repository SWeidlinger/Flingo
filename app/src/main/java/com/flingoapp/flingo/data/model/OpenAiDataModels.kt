package com.flingoapp.flingo.data.model

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenAiTextRequest(
    val model: String,
    val messages: List<Message>,
    @SerialName("response_format")val responseFormat: ResponseFormat
)

@Serializable
data class OpenAiTextResponse(
    val choices: List<Choice>
)

@Serializable
data class OpenAiImageRequest(
    val model: String,
    val prompt: String,
    @Required val n: Int = 1,
    // standard or hd
    @Required val quality: String = "standard",
    // 256x256, 512x512, 1024x1024, dall-e-3 only supports from 1024x1024
    @Required val size: String = "256x256"
)

@Serializable
data class OpenAiImageResponse(
    val data: List<ImageData>
)

@Serializable
data class ImageData(
    val url: String,
    @SerialName("revised_prompt") val revisedPrompt: String? = null
)

@Serializable
data class Message(
    val role: String,
    val content: String
)

@Serializable
data class Choice(
    val message: Message
)

@Serializable
data class ResponseFormat(
    val type: String,
)