package com.flingoapp.flingo.data.network.openAi

import android.util.Log
import com.flingoapp.flingo.data.network.GenAiModel
import com.flingoapp.flingo.data.network.GenAiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OpenAiRepositoryImpl(
    private val openAiService: OpenAiService
) : GenAiRepository {
    companion object {
        private const val TAG = "OpenAiRepositoryImpl"
    }

    override suspend fun getTextResponse(prompt: String): Result<String> {
        return try {
            val request = OpenAiTextRequest(
                model = GenAiModel.OPEN_AI.textModel,
                messages = listOf(
                    Message(
                        role = "user",
                        content = prompt
                    )
                )
            )

            val startTime = System.currentTimeMillis()

            val response = withContext(Dispatchers.IO) {
                openAiService.getTextResponse(request)
            }

            val answer = response.choices.firstOrNull()?.message?.content ?: "No response"

            val elapsedTime = System.currentTimeMillis() - startTime
            Log.d(TAG, "API Response took: $elapsedTime ms")

            Result.success(answer)

        } catch (e: Exception) {
            Log.e(TAG, "API call failed: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getImageResponse(prompt: String): Result<String> {
        return try {
            val request = OpenAiImageRequest(
                model = GenAiModel.OPEN_AI.imageModel,
                prompt = prompt
            )

            val startTime = System.currentTimeMillis()
            val response = withContext(Dispatchers.IO) {
                openAiService.getImageResponse(request)
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