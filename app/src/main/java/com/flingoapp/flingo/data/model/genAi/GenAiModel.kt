package com.flingoapp.flingo.data.model.genAi

import com.flingoapp.flingo.R

enum class GenAiModel(
    val provider: String,
    val textModel: String,
    val smallTextModel: String,
    val imageModel: String,
    val iconRes: Int
) {
    OPEN_AI(
        provider = "OpenAI",
        textModel = OpenAiModel.GPT_4O.model,
        smallTextModel = OpenAiModel.GPT_4O_MINI.model,
        imageModel = OpenAiModel.DALL_E_2.model,
        iconRes = R.drawable.openai_icon
    ),
    GOOGLE_AI(
        provider = "Google",
        textModel = GoogleAiModel.GEMINI_2_FLASH.model,
        smallTextModel = GoogleAiModel.GEMINI_2_FLASH.model,
        imageModel = GoogleAiModel.IMAGEN_3.model,
        iconRes = R.drawable.google_gemini
    )
}

private enum class OpenAiModel(
    val model: String
) {
    GPT_4O("gpt-4o"),
    GPT_4O_MINI("gpt-4o-mini"),
    DALL_E_2("dall-e-2"),
    DALL_E_3("dall-e-3")
}

private enum class GoogleAiModel(
    val model: String
) {
    GEMINI_2_FLASH("gemini-2.0-flash"),
    IMAGEN_3("imagen-3"),
}