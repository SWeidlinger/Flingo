package com.flingoapp.flingo.data.model.genAi

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Open ai text request
 *
 * @property model
 * @property messages
 * @property responseFormat
 * @constructor Create empty Open ai text request
 */
@Serializable
data class OpenAiTextRequest(
    val model: String,
    val messages: List<Message>,
    @SerialName("response_format") val responseFormat: ResponseFormat
)

/**
 * Open ai text response
 *
 * @property choices
 * @constructor Create empty Open ai text response
 */
@Serializable
data class OpenAiTextResponse(
    val choices: List<Choice>
)

/**
 * Open ai image request
 *
 * @property model
 * @property prompt
 * @property n
 * @property quality
 * @property size
 * @constructor Create empty Open ai image request
 */
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

/**
 * Open ai image response
 *
 * @property data
 * @constructor Create empty Open ai image response
 */
@Serializable
data class OpenAiImageResponse(
    val data: List<ImageData>
)

/**
 * Image data
 *
 * @property url
 * @property revisedPrompt
 * @constructor Create empty Image data
 */
@Serializable
data class ImageData(
    val url: String,
    @SerialName("revised_prompt") val revisedPrompt: String? = null
)

/**
 * Message
 *
 * @property role
 * @property content
 * @constructor Create empty Message
 */
@Serializable
data class Message(
    val role: String,
    val content: String
)

/**
 * Choice
 *
 * @property message
 * @constructor Create empty Choice
 */
@Serializable
data class Choice(
    val message: Message
)

/**
 * Response format
 *
 * @property type
 * @property schema
 * @constructor Create empty Response format
 */
@Serializable
data class ResponseFormat(
    val type: String,
    @SerialName("json_schema") val schema: JsonElement? = null
)