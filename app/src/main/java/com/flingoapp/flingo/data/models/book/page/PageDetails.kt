package com.flingoapp.flingo.data.models.book.page

import kotlinx.serialization.KSerializer
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
            "read" -> decoder.json.decodeFromJsonElement(PageDetails.ReadPageDetails.serializer(), element)
            "remove_word" -> decoder.json.decodeFromJsonElement(
                PageDetails.RemoveWordPageDetails.serializer(),
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
        }
    }
}