package com.flingoapp.flingo.data.network.google

import com.flingoapp.flingo.data.network.GenAiRepository

class GoogleAiRepositoryImpl(
    private val googleAiService: GoogleAiService
) : GenAiRepository {
    companion object {
        private const val TAG = "OpenAiRepositoryImpl"
    }

    override suspend fun getResponse(prompt: String): Result<String> {
        //TODO
        return Result.success("implement")
//        return try {
//            val request = GeminiRequest(
//                model = GenAiModel.OPEN_AI.model,
//                messages = listOf(
//                    Message(
//                        role = "user",
//                        content = prompt
//                    )
//                ),
//                responseFormat = ResponseFormat(
//                    type = "json_object"
//                )
//            )
//
//            val startTime = System.currentTimeMillis()
//
//            val response = withContext(Dispatchers.IO) {
//                googleAiService.getResponse(request)
//            }
//
//            val answer = response.choices.firstOrNull()?.message?.content ?: "No response"
//
//            val elapsedTime = System.currentTimeMillis() - startTime
//            Log.d(TAG, "API Response took: $elapsedTime ms")
//
//            Result.success(answer)
//
//        } catch (e: Exception) {
//            Log.e(TAG, "API call failed: ${e.message}")
//            Result.failure(e)
//        }
    }
}