package com.flingoapp.flingo.data.models.book.page

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

sealed class PageDetails {
    @Serializable
    data class ReadPageDetails(
        val content: String,
        val images: ArrayList<String>
    ) : PageDetails()

    @Serializable
    data class RemoveWordPageDetails(
        val content: String,
        val answer: String
    ) : PageDetails()

    @Serializable
    data class OrderStoryPageDetails(
        val content: ArrayList<Content>,
        val correctOrder: ArrayList<Int>
    ) : PageDetails() {
        companion object {
            @Serializable
            data class Content(
                val id: Int,
                val text: String
            )
        }
    }

    @Serializable
    data class QuizPageDetails(
        val quizType: QuizType,
        val question: String,
        val answers: ArrayList<Answer>
    ) : PageDetails() {
        companion object {
            @Serializable
            enum class QuizType {
                @SerialName("trueOrFalse")
                TRUE_OR_FALSE,

                @SerialName("singleChoice")
                SINGLE_CHOICE
            }

            @Serializable
            data class Answer(
                val id: Int,
                val answer: String
            )
        }
    }
}

object PageDetailsSerializer : KSerializer<PageDetails> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("PageDetails")

    override fun deserialize(decoder: Decoder): PageDetails {
        require(decoder is JsonDecoder)
        val element = decoder.decodeJsonElement()
        val jsonObject = element.jsonObject

        val pageType = jsonObject["pageType"]?.jsonPrimitive?.contentOrNull
            ?: throw IllegalArgumentException("Missing pageType in PageDetails")

        return when (pageType) {
            "read" -> decoder.json.decodeFromJsonElement(
                PageDetails.ReadPageDetails.serializer(),
                element
            )

            "remove_word" -> decoder.json.decodeFromJsonElement(
                PageDetails.RemoveWordPageDetails.serializer(),
                element
            )

            "order_story" -> decoder.json.decodeFromJsonElement(
                PageDetails.OrderStoryPageDetails.serializer(),
                element
            )

            "quiz" -> decoder.json.decodeFromJsonElement(
                PageDetails.QuizPageDetails.serializer(),
                element
            )

            else -> throw IllegalArgumentException("Unknown pageType: $pageType")
        }
    }

    override fun serialize(encoder: Encoder, value: PageDetails) {
        when (value) {
            is PageDetails.ReadPageDetails -> encoder.encodeSerializableValue(
                PageDetails.ReadPageDetails.serializer(),
                value
            )

            is PageDetails.RemoveWordPageDetails -> encoder.encodeSerializableValue(
                PageDetails.RemoveWordPageDetails.serializer(),
                value
            )

            is PageDetails.OrderStoryPageDetails -> encoder.encodeSerializableValue(
                PageDetails.OrderStoryPageDetails.serializer(),
                value
            )

            is PageDetails.QuizPageDetails -> encoder.encodeSerializableValue(
                PageDetails.QuizPageDetails.serializer(),
                value
            )
        }
    }
}