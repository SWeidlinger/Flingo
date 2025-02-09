package com.flingoapp.flingo.data.network

import com.flingoapp.flingo.R

enum class GenAiModel(val model: String, val iconRes: Int) {
    OPEN_AI(
        model = "gpt-4o",
        iconRes = R.drawable.openai_icon
    ),
    GOOGLE_AI(
        model = "gemini-1.5-pro-latest",
        iconRes = R.drawable.google_gemini
    );
}