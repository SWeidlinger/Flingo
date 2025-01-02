package com.flingoapp.flingo.data.models.book.page

import com.flingoapp.flingo.EnumDeserializer
import com.flingoapp.flingo.data.models.book.Feedback
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class PageDeserializer : JsonDeserializer<Page> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Page {
        val jsonObject = json.asJsonObject
        val pageType: PageType = context.deserialize(jsonObject.get("pageType"), EnumDeserializer::class.java)

        val pageDetailsDeserializer = PageDetailsDeserializer(pageType)
        val pageDetails: PageDetails =
            pageDetailsDeserializer.deserialize(jsonObject, PageDetails::class.java, context)

        return Page(
            id = jsonObject.get("pageId").asString,
            description = jsonObject.get("pageDescription").asString,
            isCompleted = jsonObject.get("pageCompleted").asBoolean,
            difficulty = jsonObject.get("difficulty").asString,
            hint = jsonObject.get("hint").asString,
            timeLimit = jsonObject.get("timeLimit")?.asInt,
            score = jsonObject.get("score")?.asInt,
            feedback = context.deserialize(jsonObject.get("feedback"), Feedback::class.java),
            taskDefinition = jsonObject.get("taskDefinition").asString,
            type = pageType,
            details = pageDetails
        )
    }
}