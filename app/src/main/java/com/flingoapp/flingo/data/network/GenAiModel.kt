package com.flingoapp.flingo.data.network

import com.flingoapp.flingo.R

enum class GenAiModel(
    val provider: String,
    val textModel: String,
    val imageModel: String,
    val iconRes: Int
) {
    OPEN_AI(
        provider = "OpenAI",
        textModel = "gpt-4o",
        imageModel = "dall-e-2",
        iconRes = R.drawable.openai_icon
    ),
    GOOGLE_AI(
        provider = "Google",
        textModel = "gemini-2.0-flash",
        imageModel = "imagen-3",
        iconRes = R.drawable.google_gemini
    )
}