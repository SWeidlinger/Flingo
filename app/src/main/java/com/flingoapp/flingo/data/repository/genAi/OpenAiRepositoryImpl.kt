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

class OpenAiRepositoryImpl(
    private val openAiService: OpenAiService
) : GenAiRepository {
    companion object {
        private const val TAG = "OpenAiRepositoryImpl"
    }

    override suspend fun getTextResponse(
        model: GenAiTextModel,
        request: GenAiRequest
    ): Result<String> {
        //TODO: find better way where to handle this
        val promptWithContent = request.prompt + "\n\n" + request.content

        Log.e(TAG, "Sending request to OpenAI API with: $promptWithContent")

        return try {
            val textRequest = OpenAiTextRequest(
                model = model.modelName,
                messages = listOf(
                    Message(
                        role = "user",
                        content = promptWithContent
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

            Result.success(answer)
        } catch (e: Exception) {
            Log.e(TAG, "API call failed: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getImageResponse(model: GenAiImageModel, request: GenAiRequest): Result<String> {
        val promptWithContent = request.prompt + "\n" + request.content

        Log.e(TAG, "Sending request to OpenAI API with: $promptWithContent")

        return try {
            val imageRequest = OpenAiImageRequest(
                model = model.modelName,
                prompt = promptWithContent,
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
}