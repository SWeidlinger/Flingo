package com.flingoapp.flingo.di

import com.flingoapp.flingo.data.network.GenAiModel
import com.flingoapp.flingo.data.network.GenAiRepository
import com.flingoapp.flingo.data.network.google.GoogleAiRepositoryImpl
import com.flingoapp.flingo.data.network.openAi.OpenAiRepositoryImpl
import com.flingoapp.flingo.data.network.openAi.OpenAiService

interface GenAiModule {
    val repository: GenAiRepository
    fun setModelRepository(genAiModel: GenAiModel)
}

class GenAiModuleImpl : GenAiModule {
    private val openAiService by lazy {
        OpenAiService.instance
    }

    private lateinit var modelRepository: GenAiRepository

    override val repository: GenAiRepository
        get() {
            if (!this::modelRepository.isInitialized) {
                setModelRepository(GenAiModel.OPEN_AI)
            }

            return modelRepository
        }

    override fun setModelRepository(genAiModel: GenAiModel) {
        modelRepository = when (genAiModel) {
            GenAiModel.OPEN_AI -> OpenAiRepositoryImpl(openAiService)
            GenAiModel.GOOGLE_AI -> GoogleAiRepositoryImpl()
        }
    }
}

