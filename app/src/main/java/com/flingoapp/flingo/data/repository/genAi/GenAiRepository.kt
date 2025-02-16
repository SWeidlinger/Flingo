package com.flingoapp.flingo.data.repository.genAi

interface GenAiRepository {
    suspend fun getTextResponse(prompt: String): Result<String>
    suspend fun getImageResponse(prompt: String): Result<String>
}