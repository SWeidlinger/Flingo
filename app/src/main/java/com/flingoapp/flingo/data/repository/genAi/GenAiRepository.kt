package com.flingoapp.flingo.data.repository.genAi

import com.flingoapp.flingo.data.model.genAi.GenAiImageModel
import com.flingoapp.flingo.data.model.genAi.GenAiRequest
import com.flingoapp.flingo.data.model.genAi.GenAiTextModel

/**
 * Gen ai repository
 *
 * @constructor Create empty Gen ai repository
 */
interface GenAiRepository {
    /**
     * Get text response
     *
     * @param model
     * @param request
     * @return
     */
    suspend fun generateTextResponse(model: GenAiTextModel, request: GenAiRequest): Result<String>

    /**
     * Get image response
     *
     * @param model
     * @param request
     * @return
     */
    suspend fun generateImageResponse(model: GenAiImageModel, request: GenAiRequest): Result<String>

    /**
     * Add content to prompt
     *
     * @param prompt
     * @param content
     * @return
     */
    fun addContentToPrompt(prompt: String, content: String?): String
}