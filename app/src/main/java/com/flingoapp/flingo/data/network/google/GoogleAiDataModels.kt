package com.flingoapp.flingo.data.network.google

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeminiRequest(
    val model: String,
    val messages: List<Message>,
    @SerialName("response_format")val responseFormat: ResponseFormat
)

@Serializable
data class Message(
    val role: String,
    val content: String
)

@Serializable
data class GeminiResponse(
    val choices: List<Choice>
)

@Serializable
data class Choice(
    val message: Message
)

@Serializable
data class ResponseFormat(
    val type: String,
)