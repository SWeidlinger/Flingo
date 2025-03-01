package com.flingoapp.flingo.di

import android.content.Context
import com.flingoapp.flingo.data.model.genAi.GenAiProvider
import com.flingoapp.flingo.data.model.genAi.GenAiRequestBuilder
import com.flingoapp.flingo.data.model.genAi.GenAiRequestBuilderDefaultImpl
import com.flingoapp.flingo.data.network.OpenAiService
import com.flingoapp.flingo.data.repository.genAi.GenAiRepository
import com.flingoapp.flingo.data.repository.genAi.GoogleAiRepositoryImpl
import com.flingoapp.flingo.data.repository.genAi.OpenAiRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface GenAiModule {
    val currentModel: StateFlow<GenAiProvider>
    val repository: GenAiRepository
    val basePrompts: GenAiRequestBuilder
    fun setModelRepository(genAiProvider: GenAiProvider)
    fun setBasePrompts(genAiRequestBuilder: GenAiRequestBuilder)
}

class GenAiModuleImpl(private val context: Context) : GenAiModule {
    private val openAiService by lazy {
        OpenAiService.instance
    }

    private lateinit var modelRepository: GenAiRepository
    private lateinit var currentBasePrompts: GenAiRequestBuilder

    private var _currentModel = MutableStateFlow(GenAiProvider.OPEN_AI)
    override val currentModel = _currentModel.asStateFlow()

    override val repository: GenAiRepository
        get() {
            if (!this::modelRepository.isInitialized) {
                _currentModel.update { GenAiProvider.OPEN_AI }
                setModelRepository(GenAiProvider.OPEN_AI)
            }

            return modelRepository
        }

    override val basePrompts: GenAiRequestBuilder
        get() {
            if (!this::currentBasePrompts.isInitialized) {
                //TODO: refactor this is messy

                //currently always uses default base prompt implementation
                setBasePrompts(GenAiRequestBuilderDefaultImpl(context))
            }

            return currentBasePrompts
        }

    override fun setModelRepository(genAiProvider: GenAiProvider) {
        _currentModel.update { genAiProvider }
        modelRepository = when (genAiProvider) {
            GenAiProvider.OPEN_AI -> OpenAiRepositoryImpl(openAiService)
            GenAiProvider.GOOGLE_AI -> GoogleAiRepositoryImpl()
        }
    }

    override fun setBasePrompts(genAiRequestBuilder: GenAiRequestBuilder) {
        currentBasePrompts = genAiRequestBuilder
    }
}

