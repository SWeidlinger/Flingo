package com.flingoapp.flingo.data.repository.genAi

import android.util.Log
import com.flingoapp.flingo.data.model.genAi.GenAiImageModel
import com.flingoapp.flingo.data.model.genAi.GenAiRequest
import com.flingoapp.flingo.data.model.genAi.GenAiTextModel
import com.flingoapp.flingo.data.model.genAi.Message
import com.flingoapp.flingo.data.model.genAi.OpenAiImageRequest
import com.flingoapp.flingo.data.model.genAi.OpenAiTextRequest
import com.flingoapp.flingo.data.model.genAi.ResponseFormat
import com.flingoapp.flingo.data.network.OpenAiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Open ai repository impl
 *
 * @property openAiService
 * @constructor Create empty Open ai repository impl
 */
class OpenAiRepositoryImpl(
    private val openAiService: OpenAiService
) : GenAiRepository {
    companion object {
        private const val TAG = "OpenAiRepositoryImpl"
    }

    override suspend fun generateTextResponse(
        model: GenAiTextModel,
        request: GenAiRequest
    ): Result<String> {
        Log.e(
            TAG, "Sending text request to OpenAI API with:\n" +
                    request.prompt +
                    "\nContent of Prompt:\n" +
                    request.content
        )

        return try {
            val textRequest = OpenAiTextRequest(
                model = model.modelName,
                messages = listOf(
                    Message(
                        role = "user",
                        content = addContentToPrompt(request.prompt, request.content)
                    )
                ),
                responseFormat = ResponseFormat(
                    type = if (request.jsonResponseSchema == null) "json_object" else "json_schema",
                    schema = request.jsonResponseSchema
                )
            )

            val startTime = System.currentTimeMillis()

            val response = withContext(Dispatchers.IO) {
                try {
                    openAiService.getTextResponse(textRequest)
                } catch (e: Exception) {
                    Log.e(TAG, "API call failed: ${e.message}")
                    null
                }
            }

            val answer = response?.choices?.firstOrNull()?.message?.content ?: "No response"

            val elapsedTime = System.currentTimeMillis() - startTime
            Log.d(TAG, "API Response took: $elapsedTime ms")

            Log.e(TAG, "API response:\n $answer")

            Result.success(answer)
        } catch (e: Exception) {
            Log.e(TAG, "API call failed: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun generateImageResponse(model: GenAiImageModel, request: GenAiRequest): Result<String> {
        Log.e(
            TAG, "Sending image request to OpenAI API with:\n" +
                    request.prompt +
                    "\nContent of Prompt:\n" +
                    request.content
        )

        return try {
            val imageRequest = OpenAiImageRequest(
                model = model.modelName,
                prompt = addContentToPrompt(request.prompt, request.content),
                size = model.size
            )

            val startTime = System.currentTimeMillis()
            val response = withContext(Dispatchers.IO) {
                openAiService.getImageResponse(imageRequest)
            }

            val answer = response.data.firstOrNull()?.url ?: "No response"

            val elapsedTime = System.currentTimeMillis() - startTime
            Log.d(TAG, "API Response took: $elapsedTime ms")

            Result.success(answer)
        } catch (e: Exception) {
            Log.e(TAG, "API call failed: ${e.message}")
            Result.failure(e)
        }
    }

    override fun addContentToPrompt(prompt: String, content: String?): String {
        return prompt.replace("<content>", content ?: "")
    }
}