package com.flingoapp.flingo.di

import android.content.Context
import com.flingoapp.flingo.data.model.genAi.GenAiImageModel
import com.flingoapp.flingo.data.model.genAi.GenAiModelPerformance
import com.flingoapp.flingo.data.model.genAi.GenAiProvider
import com.flingoapp.flingo.data.model.genAi.GenAiRequestBuilder
import com.flingoapp.flingo.data.model.genAi.GenAiRequestBuilderDefaultImpl
import com.flingoapp.flingo.data.model.genAi.GenAiTextModel
import com.flingoapp.flingo.data.network.OpenAiService
import com.flingoapp.flingo.data.repository.genAi.GenAiRepository
import com.flingoapp.flingo.data.repository.genAi.GoogleAiRepositoryImpl
import com.flingoapp.flingo.data.repository.genAi.OpenAiRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface GenAiModule {
    val currentModelProvider: StateFlow<GenAiProvider>
    val currentTextModel: GenAiTextModel
    val currentImageModel: GenAiImageModel
    val repository: GenAiRepository
    val basePrompts: GenAiRequestBuilder
    val modelPerformance: StateFlow<GenAiModelPerformance>
    fun setModelRepository(genAiProvider: GenAiProvider)
    fun setBasePrompts(genAiRequestBuilder: GenAiRequestBuilder)
    fun setModelPerformance(modelPerformance: GenAiModelPerformance)
}

class GenAiModuleImpl(private val context: Context) : GenAiModule {
    private val openAiService by lazy {
        OpenAiService.instance
    }

    private lateinit var modelRepository: GenAiRepository
    private lateinit var currentBasePrompts: GenAiRequestBuilder

    //Default value for model performance
    private var _modelPerformance = MutableStateFlow(GenAiModelPerformance.ACCURATE)
    override var modelPerformance = _modelPerformance.asStateFlow()

    private var _currentModelProvider = MutableStateFlow(GenAiProvider.OPEN_AI)
    override val currentModelProvider = _currentModelProvider.asStateFlow()

    override val currentTextModel: GenAiTextModel
        get() = _currentModelProvider.value.getTextModel(_modelPerformance.value)
    override val currentImageModel: GenAiImageModel
        get() = _currentModelProvider.value.getImageModel(_modelPerformance.value)

    override val repository: GenAiRepository
        get() {
            if (!this::modelRepository.isInitialized) {
                _currentModelProvider.update { GenAiProvider.OPEN_AI }
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
        _currentModelProvider.update { genAiProvider }
        modelRepository = when (genAiProvider) {
            GenAiProvider.OPEN_AI -> OpenAiRepositoryImpl(openAiService)
            GenAiProvider.GOOGLE_AI -> GoogleAiRepositoryImpl()
        }
    }

    override fun setBasePrompts(genAiRequestBuilder: GenAiRequestBuilder) {
        currentBasePrompts = genAiRequestBuilder
    }

    override fun setModelPerformance(modelPerformance: GenAiModelPerformance) {
        _modelPerformance.update { modelPerformance }
    }
}

