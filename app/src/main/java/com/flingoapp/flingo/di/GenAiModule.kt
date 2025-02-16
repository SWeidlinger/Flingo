package com.flingoapp.flingo.di

import android.app.Application
import com.flingoapp.flingo.data.model.GenAiModel
import com.flingoapp.flingo.data.repository.genAi.GenAiRepository
import com.flingoapp.flingo.data.repository.genAi.GoogleAiRepositoryImpl
import com.flingoapp.flingo.data.repository.genAi.OpenAiRepositoryImpl
import com.flingoapp.flingo.data.network.OpenAiService

interface GenAiModule {
    val application: Application
    val repository: GenAiRepository
    fun setModelRepository(genAiModel: GenAiModel)
}

class GenAiModuleImpl(override val application: Application) : GenAiModule {
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

