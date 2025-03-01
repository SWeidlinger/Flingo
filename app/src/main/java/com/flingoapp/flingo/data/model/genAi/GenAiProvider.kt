package com.flingoapp.flingo.data.model.genAi

import com.flingoapp.flingo.R

enum class GenAiProvider(
    val provider: String,
    private val accurateTextModel: GenAiTextModel,
    private val fastTextModel: GenAiTextModel,
    private val accurateImageModel: GenAiImageModel,
    private val fastImageModel: GenAiImageModel,
    val iconRes: Int
) {
    OPEN_AI(
        provider = "OpenAI",
        accurateTextModel = GenAiTextModel.OpenAi.GPT_4O,
        fastTextModel = GenAiTextModel.OpenAi.GPT_4O_MINI,
        accurateImageModel = GenAiImageModel.OpenAi.DALL_E_3,
        fastImageModel = GenAiImageModel.OpenAi.DALL_E_2,
        iconRes = R.drawable.openai_icon
    ),
    GOOGLE_AI(
        provider = "Google",
        accurateTextModel = GenAiTextModel.Google.GEMINI_2_PRO,
        fastTextModel = GenAiTextModel.Google.GEMINI_2_FLASH,
        accurateImageModel = GenAiImageModel.Google.IMAGEN_3,
        fastImageModel = GenAiImageModel.Google.IMAGEN_3_FAST,
        iconRes = R.drawable.google_gemini
    );

    //TODO: refactor could be implemented in a better way
    fun getTextModel(modelPerformance: GenAiModelPerformance): GenAiTextModel {
        return when (modelPerformance) {
            GenAiModelPerformance.ACCURATE -> accurateTextModel
            GenAiModelPerformance.FAST -> fastTextModel
        }
    }

    fun getImageModel(modelPerformance: GenAiModelPerformance): GenAiImageModel {
        return when (modelPerformance) {
            GenAiModelPerformance.ACCURATE -> accurateImageModel
            GenAiModelPerformance.FAST -> fastImageModel
        }
    }
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
        GEMINI_2_PRO("gemini-2.0-pro-exp-02-05"),
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
        DALL_E_2("dall-e-2", "512x512"),
        DALL_E_3("dall-e-3", "1024x1024")
    }

    enum class Google(
        override val modelName: String,
        override val size: String
    ) : GenAiImageModel {
        IMAGEN_3("imagen-3.0-generate-002", ""),
        IMAGEN_3_FAST("imagen-3.0-fast-generate-001", "")
    }
}

enum class GenAiModelPerformance(val displayName: String) {
    ACCURATE("accurate"),
    FAST("fast")
}
