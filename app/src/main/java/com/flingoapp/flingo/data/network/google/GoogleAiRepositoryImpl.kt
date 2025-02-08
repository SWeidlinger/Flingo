package com.flingoapp.flingo.data.network.google

import android.util.Log
import com.flingoapp.flingo.BuildConfig
import com.flingoapp.flingo.data.network.GenAiModel
import com.flingoapp.flingo.data.network.GenAiRepository
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GoogleAiRepositoryImpl(

) : GenAiRepository {
    companion object {
        private const val TAG = "OpenAiRepositoryImpl"
    }

    override suspend fun getResponse(prompt: String): Result<String> {
        return try {
            val generativeModel = GenerativeModel(
                modelName = GenAiModel.GOOGLE_AI.model,
                apiKey = BuildConfig.GEMINI_API_KEY,
                generationConfig = generationConfig {
                    responseMimeType = "application/json"
                },
                //TODO: check if adding this instead of in the prompt changes anything
//                systemInstruction =
            )

            val startTime = System.currentTimeMillis()

            val response = withContext(Dispatchers.IO) {
                generativeModel.generateContent(
                    content("user") {
                        text(prompt)
                    }
                )
            }

            val answer = response.text ?: "No response"

            val elapsedTime = System.currentTimeMillis() - startTime
            Log.d(TAG, "API Response took: $elapsedTime ms")

            Result.success(answer)

        } catch (e: Exception) {
            Log.e(TAG, "API call failed: ${e.message}")
            Result.failure(e)
        }
    }
}