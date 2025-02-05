package com.flingoapp.flingo.data.network

interface GenAiRepository {
    suspend fun getResponse(prompt: String): Result<String>
}