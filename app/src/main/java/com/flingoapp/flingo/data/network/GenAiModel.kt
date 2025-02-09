package com.flingoapp.flingo.data.network

import com.flingoapp.flingo.R

enum class GenAiModel(
    val provider: String,
    val model: String,
    val iconRes: Int
) {
    OPEN_AI(
        provider = "OpenAI",
        model = "gpt-4o",
        iconRes = R.drawable.openai_icon
    ),
    GOOGLE_AI(
        provider = "Google",
        model = "gemini-2.0-flash",
        iconRes = R.drawable.google_gemini
    );
}