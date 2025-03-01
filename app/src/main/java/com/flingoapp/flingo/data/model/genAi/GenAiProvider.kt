package com.flingoapp.flingo.data.model.genAi

import com.flingoapp.flingo.R

enum class GenAiProvider(
    val provider: String,
    val textModel: GenAiTextModel,
    val smallTextModel: GenAiTextModel,
    val imageModel: GenAiImageModel,
    val iconRes: Int
) {
    OPEN_AI(
        provider = "OpenAI",
        textModel = GenAiTextModel.OpenAi.GPT_4O,
        smallTextModel = GenAiTextModel.OpenAi.GPT_4O_MINI,
        imageModel = GenAiImageModel.OpenAi.DALL_E_2,
        iconRes = R.drawable.openai_icon
    ),
    GOOGLE_AI(
        provider = "Google",
        textModel = GenAiTextModel.Google.GEMINI_2_FLASH,
        smallTextModel = GenAiTextModel.Google.GEMINI_2_FLASH,
        imageModel = GenAiImageModel.Google.IMAGEN_3,
        iconRes = R.drawable.google_gemini
    )
}

sealed interface GenAiTextModel {
    val modelName: String

    enum class OpenAi(
        override val modelName: String
    ) : GenAiTextModel {
        GPT_4O("gpt-4o"),
        GPT_4O_MINI("gpt-4o-mini"),
    }

    enum class Google(
        override val modelName: String
    ) : GenAiTextModel {
        GEMINI_2_FLASH("gemini-2.0-flash")
    }
}

sealed interface GenAiImageModel {
    val modelName: String
    val size: String

    enum class OpenAi(
        override val modelName: String,
        override val size: String
    ) : GenAiImageModel {
        DALL_E_2("dall-e-2", "256x256"),
        DALL_E_3("dall-e-3", "1024x1024")
    }

    enum class Google(
        override val modelName: String,
        override val size: String
    ) : GenAiImageModel {
        IMAGEN_3("imagen-3.0-generate-002", "")
    }
}
