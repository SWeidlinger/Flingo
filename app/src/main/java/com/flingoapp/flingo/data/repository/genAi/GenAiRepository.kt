package com.flingoapp.flingo.data.repository.genAi

import com.flingoapp.flingo.data.model.genAi.GenAiImageModel
import com.flingoapp.flingo.data.model.genAi.GenAiRequest
import com.flingoapp.flingo.data.model.genAi.GenAiTextModel

interface GenAiRepository {
    suspend fun getTextResponse(model: GenAiTextModel, request: GenAiRequest): Result<String>
    suspend fun getImageResponse(model: GenAiImageModel, request: GenAiRequest): Result<String>
    fun addContentToPrompt(prompt: String, content: String?): String
}