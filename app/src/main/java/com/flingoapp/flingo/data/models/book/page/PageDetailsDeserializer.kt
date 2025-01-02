package com.flingoapp.flingo.data.models.book.page

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

/**
 * Page details deserializer to deserialize data from JSON into enum
 *
 * @param T
 * @constructor Create empty Enum deserializer
 */
class PageDetailsDeserializer(private val pageType: PageType) : JsonDeserializer<PageDetails> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): PageDetails {
        val jsonObject = json.asJsonObject

        return when (pageType) {
            PageType.READ -> context.deserialize(jsonObject, PageDetails.ReadPageDetails::class.java)
            PageType.REMOVE_WORD -> context.deserialize(jsonObject, PageDetails.RemoveWordPageDetails::class.java)
            else -> throw IllegalArgumentException("Unknown pageType: $pageType")
        }
    }
}