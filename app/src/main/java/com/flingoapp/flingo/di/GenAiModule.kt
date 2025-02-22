package com.flingoapp.flingo.di

import android.content.Context
import com.flingoapp.flingo.data.model.genAi.GenAiBasePrompts
import com.flingoapp.flingo.data.model.genAi.GenAiBasePromptsDefaultImpl
import com.flingoapp.flingo.data.model.genAi.GenAiModel
import com.flingoapp.flingo.data.network.OpenAiService
import com.flingoapp.flingo.data.repository.genAi.GenAiRepository
import com.flingoapp.flingo.data.repository.genAi.GoogleAiRepositoryImpl
import com.flingoapp.flingo.data.repository.genAi.OpenAiRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface GenAiModule {
    val currentModel: StateFlow<GenAiModel>
    val repository: GenAiRepository
    val basePrompts: GenAiBasePrompts
    fun setModelRepository(genAiModel: GenAiModel)
    fun setBasePrompts(genAiBasePrompts: GenAiBasePrompts)
}

class GenAiModuleImpl(private val context: Context) : GenAiModule {
    private val openAiService by lazy {
        OpenAiService.instance
    }

    private lateinit var modelRepository: GenAiRepository
    lateinit var currentBasePrompts: GenAiBasePrompts

    private var _currentModel = MutableStateFlow(GenAiModel.OPEN_AI)
    override val currentModel = _currentModel.asStateFlow()

    override val repository: GenAiRepository
        get() {
            if (!this::modelRepository.isInitialized) {
                _currentModel.update { GenAiModel.OPEN_AI }
                setModelRepository(GenAiModel.OPEN_AI)
            }

            return modelRepository
        }

    override val basePrompts: GenAiBasePrompts
        get() {
            if (!this::currentBasePrompts.isInitialized) {
                //TODO: refactor this is messy

                //currently always uses default base prompt implementation
                setBasePrompts(GenAiBasePromptsDefaultImpl(context))
            }

            return currentBasePrompts
        }

    override fun setModelRepository(genAiModel: GenAiModel) {
        _currentModel.update { genAiModel }
        modelRepository = when (genAiModel) {
            GenAiModel.OPEN_AI -> OpenAiRepositoryImpl(openAiService)
            GenAiModel.GOOGLE_AI -> GoogleAiRepositoryImpl()
        }
    }

    override fun setBasePrompts(genAiBasePrompts: GenAiBasePrompts) {
        currentBasePrompts = genAiBasePrompts
    }
}

