package com.flingoapp.flingo.data.repository.genAi

import com.flingoapp.flingo.data.model.genAi.GenAiRequest

interface GenAiRepository {
    suspend fun getTextResponse(model: String, request: GenAiRequest): Result<String>
    suspend fun getImageResponse(model: String, request: GenAiRequest): Result<String>
}