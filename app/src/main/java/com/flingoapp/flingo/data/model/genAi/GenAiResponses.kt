package com.flingoapp.flingo.data.model.genAi

sealed interface GenAiResponse {
    data class SplitTextResponse(
        val title: String,
        val content: List<String>
    ) : GenAiResponse
}