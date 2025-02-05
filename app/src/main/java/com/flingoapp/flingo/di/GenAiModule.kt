package com.flingoapp.flingo.di

import com.flingoapp.flingo.data.network.GenAiModelName
import com.flingoapp.flingo.data.network.GenAiRepository
import com.flingoapp.flingo.data.network.openAi.OpenAiRepositoryImpl
import com.flingoapp.flingo.data.network.openAi.OpenAiService

interface GenAiModule {
    val repository: GenAiRepository
    fun setModelRepository(genAiModel: GenAiModelName)
}

class GenAiModuleImpl : GenAiModule {
    private val openAiService by lazy {
        OpenAiService.instance
    }

    private lateinit var modelRepository: GenAiRepository

    override val repository: GenAiRepository
        get() {
            if (!this::modelRepository.isInitialized) {
                setModelRepository(GenAiModelName.OPENAI)
            }

            return modelRepository
        }

    override fun setModelRepository(genAiModel: GenAiModelName) {
        modelRepository = when (genAiModel) {
            GenAiModelName.OPENAI -> OpenAiRepositoryImpl(openAiService)
        }
    }
}

