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

    override suspend fun getResponse(prompt: String): Result<String> {
        return try {
            val request = OpenAiRequest(
                model = GenAiModel.OPEN_AI.model,
                messages = listOf(
                    Message(
                        role = "user",
                        content = prompt
                    )
                ),
                responseFormat = ResponseFormat(
                    type = "json_object"
                )
            )

            val startTime = System.currentTimeMillis()

            val response = withContext(Dispatchers.IO) {
                openAiService.getResponse(request)
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
}