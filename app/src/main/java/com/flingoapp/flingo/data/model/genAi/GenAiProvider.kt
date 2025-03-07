package com.flingoapp.flingo.data.model.genAi

import com.flingoapp.flingo.R

/**
 * Gen ai provider
 *
 * @property provider
 * @property accurateTextModel
 * @property fastTextModel
 * @property accurateImageModel
 * @property fastImageModel
 * @property iconRes
 * @constructor Create empty Gen ai provider
 */
enum class GenAiProvider(
    val provider: String,
    private val accurateTextModel: GenAiTextModel,
    private val fastTextModel: GenAiTextModel,
    private val accurateImageModel: GenAiImageModel,
    private val fastImageModel: GenAiImageModel,
    val iconRes: Int
) {
    /**
     * Open Ai
     *
     * @constructor Create empty Open Ai
     */
    OPEN_AI(
        provider = "OpenAI",
        accurateTextModel = GenAiTextModel.OpenAi.GPT_4O,
        fastTextModel = GenAiTextModel.OpenAi.GPT_4O_MINI,
        accurateImageModel = GenAiImageModel.OpenAi.DALL_E_3,
        fastImageModel = GenAiImageModel.OpenAi.DALL_E_2,
        iconRes = R.drawable.openai_icon
    ),

    /**
     * Google Ai
     *
     * @constructor Create empty Google Ai
     */
    GOOGLE_AI(
        provider = "Google",
        accurateTextModel = GenAiTextModel.Google.GEMINI_2_PRO,
        fastTextModel = GenAiTextModel.Google.GEMINI_2_FLASH,
        accurateImageModel = GenAiImageModel.Google.IMAGEN_3,
        fastImageModel = GenAiImageModel.Google.IMAGEN_3_FAST,
        iconRes = R.drawable.google_gemini
    );

    /**
     * Get text model
     *
     * @param modelPerformance
     * @return
     */
    //TODO: refactor could be implemented in a better way
    fun getTextModel(modelPerformance: GenAiModelPerformance): GenAiTextModel {
        return when (modelPerformance) {
            GenAiModelPerformance.ACCURATE -> accurateTextModel
            GenAiModelPerformance.FAST -> fastTextModel
        }
    }

    /**
     * Get image model
     *
     * @param modelPerformance
     * @return
     */
    fun getImageModel(modelPerformance: GenAiModelPerformance): GenAiImageModel {
        return when (modelPerformance) {
            GenAiModelPerformance.ACCURATE -> accurateImageModel
            GenAiModelPerformance.FAST -> fastImageModel
        }
    }
}

/**
 * Gen ai text model
 *
 * @constructor Create empty Gen ai text model
 */
sealed interface GenAiTextModel {
    val modelName: String

    /**
     * Open ai
     *
     * @property modelName
     * @constructor Create empty Open ai
     */
    enum class OpenAi(
        override val modelName: String
    ) : GenAiTextModel {
        /**
         * Gpt 4o
         *
         * @constructor Create empty Gpt 4o
         */
        GPT_4O("gpt-4o"),

        /**
         * Gpt 4o Mini
         *
         * @constructor Create empty Gpt 4o Mini
         */
        GPT_4O_MINI("gpt-4o-mini"),
    }

    /**
     * Google
     *
     * @property modelName
     * @constructor Create empty Google
     */
    enum class Google(
        override val modelName: String
    ) : GenAiTextModel {
        /**
         * Gemini 2 Pro
         *
         * @constructor Create empty Gemini 2 Pro
         */
        GEMINI_2_PRO("gemini-2.0-pro-exp-02-05"),

        /**
         * Gemini 2 Flash
         *
         * @constructor Create empty Gemini 2 Flash
         */
        GEMINI_2_FLASH("gemini-2.0-flash")
    }
}

/**
 * Gen ai image model
 *
 * @constructor Create empty Gen ai image model
 */
sealed interface GenAiImageModel {
    val modelName: String
    val size: String

    /**
     * Open ai
     *
     * @property modelName
     * @property size
     * @constructor Create empty Open ai
     */
    enum class OpenAi(
        override val modelName: String,
        override val size: String
    ) : GenAiImageModel {
        /**
         * Dall E 2
         *
         * @constructor Create empty Dall E 2
         */
        DALL_E_2("dall-e-2", "512x512"),

        /**
         * Dall E 3
         *
         * @constructor Create empty Dall E 3
         */
        DALL_E_3("dall-e-3", "1024x1024")
    }

    /**
     * Google
     *
     * @property modelName
     * @property size
     * @constructor Create empty Google
     */
    enum class Google(
        override val modelName: String,
        override val size: String
    ) : GenAiImageModel {
        /**
         * Imagen 3
         *
         * @constructor Create empty Imagen 3
         */
        IMAGEN_3("imagen-3.0-generate-002", ""),

        /**
         * Imagen 3 Fast
         *
         * @constructor Create empty Imagen 3 Fast
         */
        IMAGEN_3_FAST("imagen-3.0-fast-generate-001", "")
    }
}

/**
 * Gen ai model performance
 *
 * @property displayName
 * @constructor Create empty Gen ai model performance
 */
enum class GenAiModelPerformance(val displayName: String) {
    /**
     * Accurate
     *
     * @constructor Create empty Accurate
     */
    ACCURATE("accurate"),

    /**
     * Fast
     *
     * @constructor Create empty Fast
     */
    FAST("fast")
}
