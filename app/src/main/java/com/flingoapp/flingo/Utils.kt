package com.flingoapp.flingo

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

/**
 * Generic JsonAdapter that convert strings to enum during JSON deserialization
 * */
class EnumDeserializer<T : Enum<T>>() : JsonDeserializer<T> {
    override fun deserialize(
        json: JsonElement?, typeOfT: Type?,
        context: JsonDeserializationContext?
    ): T? {
        return json?.asString?.let {
            if (it.isNotEmpty()) {
                val enumClass = typeOfT as? Class<T>
                return enumClass?.enumConstants?.first { enumValue -> enumValue.name.equals(it, true) }
            } else {
                return null
            }
        }
    }
}