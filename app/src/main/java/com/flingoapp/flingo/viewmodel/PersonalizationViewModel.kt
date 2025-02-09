package com.flingoapp.flingo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flingoapp.flingo.data.network.GenAiModel
import com.flingoapp.flingo.di.GenAiModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PersonalizationUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isSuccess: Boolean = false,
    val currentModel: GenAiModel = GenAiModel.OPEN_AI,
    //debug specific
    val isDebug: Boolean = false,
    val lastResponseTime: Long? = null,
    val usedPrompt: String? = null,
    val childName: String? = null,
    val childAge: Int? = null,
    val childInterest: String? = null,
)

class PersonalizationViewModel(
    private val genAiModule: GenAiModule,
    //should be handled differently, viewmodel should not depend on other viewmodel but fine for now
    private val bookViewModel: BookViewModel,
    private val userViewModel: UserViewModel,
) : ViewModel() {
    companion object {
        private const val TAG = "PersonalizationViewModel"

        private const val INSTRUCTION_PROMPT =
            "You are a renowned children's book author specializing in personalized educational content." +
                    "Your task is to adapt the following JSON to match the preferences and learning needs of a specific child while maintaining the structure of the original content." +
                    "The child is <age> years old, named <name>, and has a strong interest in <interest>. He is working on improving his reading skills.\n" +
                    "\n" +
                    "Modify the text and story elements to align with the interest of <name> while keeping the content engaging and educational." +
                    "Ensure that the difficulty level is appropriate for his age and supports his reading development. " +
                    "Do not add new fields, game modes, or modify the JSON structure—only adapt the content to make it more engaging and relevant for <name>.\n" +
                    "\n" +
                    "The text must be in German and should enhance the motivation of <name> to read while making learning more enjoyable."

        private const val FULL_BOOK = "prompt_examples/full_book.json"
    }

    private val _uiState = MutableStateFlow(PersonalizationUiState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: MainAction.PersonalizationAction) {
        when (action) {
            MainAction.PersonalizationAction.GenerateBook -> generateBook()
            is MainAction.PersonalizationAction.ChangeModel -> changeModel(action.model)
            MainAction.PersonalizationAction.ToggleDebugMode -> toggleDebugMode()
        }
    }

    private fun buildPersonalizedPrompt(): String {
        val name = userViewModel.uiState.value.name
        val age = userViewModel.uiState.value.age
        val interests = userViewModel.uiState.value.selectedInterests

        val personalizedPrompt = INSTRUCTION_PROMPT
            .replace("<age>", age.toString())
            .replace("<name>", name)
            .replace("<interest>", interests.first())

        updateUiState(
            _uiState.value.copy(
                usedPrompt = personalizedPrompt,
                childName = name,
                childAge = age,
                childInterest = interests.first()
            )
        )

        val sourceJson =
            genAiModule.application.assets.open(FULL_BOOK).bufferedReader().use { it.readText() }

        return personalizedPrompt + sourceJson
    }

    private fun changeModel(model: GenAiModel) {
        genAiModule.setModelRepository(model)
        updateUiState(_uiState.value.copy(currentModel = model))
    }

    private fun generateBook() {
        viewModelScope.launch {
            updateUiState(_uiState.value.copy(isLoading = true))

            val prompt = buildPersonalizedPrompt()
            val startTime = System.currentTimeMillis()

            genAiModule.repository.getResponse(prompt)
                .onFailure { error ->
                    errorHandling(error)
                }
                .onSuccess { book ->
                    Log.e(TAG, book)
                    val duration = System.currentTimeMillis() - startTime

                    updateUiState(
                        _uiState.value.copy(
                            isLoading = false,
                            isSuccess = true,
                            lastResponseTime = duration
                        )
                    )
                    bookViewModel.onAction(
                        MainAction.BookAction.AddBook(
                            bookJson = book,
                            author = _uiState.value.currentModel.provider
                        )
                    )
                }
        }
    }

    private fun errorHandling(error: Throwable) {
        Log.e(TAG, "Error: ${error.message}")
        updateUiState(_uiState.value.copy(isLoading = false, isError = true))
    }

    private fun toggleDebugMode() {
        val currentState = _uiState.value.isDebug
        updateUiState(_uiState.value.copy(isDebug = !currentState))
    }

    /**
     * Update ui state
     *
     * @param newUiState
     */
    private fun updateUiState(newUiState: PersonalizationUiState) {
        _uiState.update { newUiState }
    }
}