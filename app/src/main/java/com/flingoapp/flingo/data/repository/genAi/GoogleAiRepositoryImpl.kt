package com.flingoapp.flingo.data.repository.genAi

import android.util.Log
import com.flingoapp.flingo.bitmapToString
import com.flingoapp.flingo.data.model.genAi.GenAiImageModel
import com.flingoapp.flingo.data.model.genAi.GenAiRequest
import com.flingoapp.flingo.data.model.genAi.GenAiTextModel
import com.google.firebase.Firebase
import com.google.firebase.vertexai.type.PublicPreviewAPI
import com.google.firebase.vertexai.type.Schema
import com.google.firebase.vertexai.type.content
import com.google.firebase.vertexai.type.generationConfig
import com.google.firebase.vertexai.vertexAI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement


class GoogleAiRepositoryImpl : GenAiRepository {
    companion object {
        private const val TAG = "GoogleAiRepositoryImpl"
    }

    override suspend fun getTextResponse(model: GenAiTextModel, request: GenAiRequest): Result<String> {
        Log.e(
            TAG, "Sending request to GoogleAI API with:\n" +
                    request.prompt +
                    "\nContent of Prompt:\n" +
                    request.content
        )

        return try {
            val genConfig = if (request.jsonResponseSchema != null) {
                val vertexSchema: Schema = loadVertexSchemaFromJsonElement(request.jsonResponseSchema)
                generationConfig {
                    responseMimeType = "application/json"
                    responseSchema = vertexSchema
                }
            } else {
                generationConfig {
                    responseMimeType = "application/json"
                }
            }

            val firebaseModel = Firebase.vertexAI.generativeModel(
                modelName = model.modelName,
                generationConfig = genConfig
            )

            val startTime = System.currentTimeMillis()

            val response = withContext(Dispatchers.IO) {
                firebaseModel.generateContent(
                    content("user") {
                        text(addContentToPrompt(request.prompt, request.content))
                    }
                )
            }

            val answer = response.text ?: "No response"

            val elapsedTime = System.currentTimeMillis() - startTime
            Log.d(TAG, "API Response took: $elapsedTime ms")

            Log.e(TAG, "API response:\n $answer")

            Result.success(answer)
        } catch (e: Exception) {
            Log.e(TAG, "API call failed: ${e.message}")
            Result.failure(e)
        }
    }

    @OptIn(PublicPreviewAPI::class)
    override suspend fun getImageResponse(model: GenAiImageModel, request: GenAiRequest): Result<String> {
        Log.e(
            TAG, "Sending image request to GoogleAi API with:\n" +
                    request.prompt +
                    "\nContent of Prompt:\n" +
                    request.content
        )

        return try {
            val firebaseModel = Firebase.vertexAI.imagenModel(
                modelName = model.modelName,
            )

            val startTime = System.currentTimeMillis()
            val response = withContext(Dispatchers.IO) {
                firebaseModel.generateImages(
                    prompt = addContentToPrompt(request.prompt, request.content)
                )
            }

            val answer = response.images.first()

            val elapsedTime = System.currentTimeMillis() - startTime
            Log.d(TAG, "API Response took: $elapsedTime ms")

            Log.e(TAG, "API response:\n${answer.mimeType}")

            val bitmapString = bitmapToString(answer.asBitmap())

            Result.success(bitmapString)
        } catch (e: Exception) {
            Log.e(TAG, "API call failed: ${e.message}")
            Result.failure(e)
        }
    }

    override fun addContentToPrompt(prompt: String, content: String?): String {
        return prompt.replace("<content>", content ?: "")
    }
}

//TODO: move to separate file
@Serializable
data class OpenAISchemaFile(
    val name: String,
    val strict: Boolean,
    val schema: JSONSchema
)

@Serializable
data class JSONSchema(
    val type: String,
    val properties: Map<String, SchemaProperty>? = null,
    val required: List<String>,
    val additionalProperties: Boolean = false
)


@Serializable
data class SchemaProperty(
    val type: String,
    val description: String = "",
    val items: SchemaProperty? = null,
    val properties: Map<String, SchemaProperty>? = null,
    val required: List<String>? = null,
    val additionalProperties: Boolean? = null
)

// --- Vertex schema conversion functions ---

fun convertSchemaPropertyToVertexSchema(prop: SchemaProperty): Schema {
    return when (prop.type.lowercase()) {
        "string" -> Schema.string(prop.description, nullable = false)
        "number" -> Schema.double(prop.description, nullable = false)
        "integer" -> Schema.integer(prop.description, nullable = false)
        "boolean" -> Schema.boolean(prop.description, nullable = false)
        "object" -> {
            val childProperties = mutableMapOf<String, Schema>()
            prop.properties?.forEach { (key, childProp) ->
                childProperties[key] = convertSchemaPropertyToVertexSchema(childProp)
            }
            // Use provided required list for the object if available
            val childRequired = prop.required?.toSet() ?: childProperties.keys.toSet()
            val optionalProps = childProperties.keys.filter { it !in childRequired }
            Schema.obj(childProperties, optionalProps, prop.description, nullable = false)
        }

        "array" -> {
            val itemSchema = prop.items?.let { convertSchemaPropertyToVertexSchema(it) }
            if (itemSchema != null) {
                Schema.array(itemSchema, prop.description, nullable = false)
            } else {
                Schema.array(
                    Schema.string("Fallback array item", nullable = false),
                    prop.description,
                    nullable = false
                )
            }
        }

        else -> throw IllegalArgumentException("Unhandled type: ${prop.type}")
    }
}

fun convertJSONSchemaToVertexSchema(jsonSchema: JSONSchema): Schema {
    if (jsonSchema.type.lowercase() != "object") {
        throw IllegalArgumentException("Root schema must be an object")
    }
    // Build properties from the provided properties map
    val properties = mutableMapOf<String, Schema>()
    jsonSchema.properties?.forEach { (key, prop) ->
        properties[key] = convertSchemaPropertyToVertexSchema(prop)
    }
    // Ensure that every required field is present.
    // If a required field is missing from the properties, add a default Schema (e.g., a string).
    val requiredSet = jsonSchema.required.toSet()
    for (key in requiredSet) {
        if (!properties.containsKey(key)) {
            // Adding a default field as a string with a placeholder description.
            properties[key] = Schema.string("Default for missing required field $key", nullable = false)
        }
    }
    // Determine optional properties: any property not listed as required.
    val optionalProps = properties.keys.filter { it !in requiredSet }
    return Schema.obj(properties, optionalProps, "Root schema generated from JSON", nullable = false)
}

fun loadVertexSchemaFromJsonElement(jsonElement: JsonElement): Schema {
    val jsonDeserializer = Json { ignoreUnknownKeys = true }
    val openAISchemaFile = jsonDeserializer.decodeFromJsonElement<OpenAISchemaFile>(jsonElement)
    return convertJSONSchemaToVertexSchema(openAISchemaFile.schema)
}