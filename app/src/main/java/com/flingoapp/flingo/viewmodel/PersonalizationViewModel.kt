package com.flingoapp.flingo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flingoapp.flingo.data.model.Chapter
import com.flingoapp.flingo.data.model.GenAiModel
import com.flingoapp.flingo.data.network.ConnectivityObserver
import com.flingoapp.flingo.data.repository.BookRepository
import com.flingoapp.flingo.di.GenAiModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

data class PersonalizationUiState(
    val isConnectedToNetwork: Boolean = false,
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

//TODO: refactor, move getting API responses to a service, since not needed in viewmodel
class PersonalizationViewModel(
    private val genAiModule: GenAiModule,
    //should be handled differently, viewmodel should not depend on other viewmodel but fine for now
    private val bookViewModel: BookViewModel,
    private val userViewModel: UserViewModel,
    private val bookRepository: BookRepository,
    connectivityObserver: ConnectivityObserver
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

        private const val INSTRUCTION_PROMPT_CHAPTER =
            "You are a renowned children's book author specializing in personalized educational content. Your task is to generate a single chapter based on an existing JSON that contains multiple chapters.\n" +
                    "\n" +
                    "First, select the a suitable chapter from the given list.\n" +
                    "Then, generate a new JSON object of that chapter, adapting the content to match the child's preferences while maintaining the original structure.\n" +
                    "The child is <age> years old, named <name>, and has a strong interest in <interest>. The goal is to make the chapter engaging, educational, and age-appropriate.\n" +
                    "\n" +
                    "Rules:\n" +
                    "Do not add new fields or game modes—only adapt the selected chapter.\n" +
                    "The added chapter must not be of chapterType read.\n" +
                    "Keep the exact same JSON structure.\n" +
                    "The text must be in German and should encourage reading motivation while making learning enjoyable."

        private const val PROMPT_IMAGE =
            "Generate a colorful, engaging illustration in the style of a children's reading-learning book. " +
                    "The image should be child-friendly, visually appealing, and designed to support early literacy. " +
                    "Ensure it aligns with the given context: <context>, using bright colors, simple shapes, and expressive characters to make the scene inviting and educational."

        private const val FULL_BOOK_RESOURCE_JSON = "prompt_examples/full_book.json"
    }

    private val isConnected = connectivityObserver
        .isConnected
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            false
        )

    private val _uiState = MutableStateFlow(PersonalizationUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            isConnected.collect { connected ->
                updateUiState(_uiState.value.copy(isConnectedToNetwork = connected))
            }
        }
    }

    fun onAction(action: MainAction.PersonalizationAction) {
        when (action) {
            MainAction.PersonalizationAction.GenerateBook -> generateBook()
            MainAction.PersonalizationAction.GenerateChapter -> generateChapter()
            is MainAction.PersonalizationAction.ChangeModel -> changeModel(action.model)
            MainAction.PersonalizationAction.ToggleDebugMode -> toggleDebugMode()
            is MainAction.PersonalizationAction.GenerateImage -> generateImage(action.context)
        }
    }

    private fun generateBook() {
        viewModelScope.launch {
            val instructionPrompt = getPersonalizedPrompt(InstructionPromptType.BOOK)
            val sourceJson =
                genAiModule.application.assets.open(FULL_BOOK_RESOURCE_JSON).bufferedReader()
                    .use { it.readText() }
            val prompt = instructionPrompt + sourceJson

            val response = getTextResponse(prompt)

            response?.let { bookJson ->
                bookViewModel.onAction(
                    MainAction.BookAction.AddBook(
                        bookJson = bookJson,
                        author = _uiState.value.currentModel.provider
                    )
                )
            }
        }
    }

    private fun generateChapter() {
        viewModelScope.launch {
            val instructionPrompt = getPersonalizedPrompt(InstructionPromptType.CHAPTER)
            val source =
                bookRepository.getBook(bookViewModel.uiState.value.currentBookId ?: return@launch)
                    .getOrThrow().chapters

            val sourceJson = Json.encodeToString<List<Chapter>>(source)
            val prompt = instructionPrompt + sourceJson

            val response = getTextResponse(prompt)

            response?.let { chapterJson ->
                bookViewModel.onAction(
                    MainAction.BookAction.AddChapter(
                        chapterJson = chapterJson,
                        author = _uiState.value.currentModel.provider
                    )
                )
            }
        }
    }

    private fun generateImage(context: String) {
        viewModelScope.launch {
            val prompt = PROMPT_IMAGE.replace("<context>", context)

            val response = getImageResponse(prompt)

            response?.let { imageUrl ->
                bookViewModel.onAction(
                    MainAction.BookAction.AddImage(
                        author = _uiState.value.currentModel.provider,
                        imageUrl = imageUrl
                    )
                )
            }
        }
    }

    private fun getPersonalizedPrompt(promptType: InstructionPromptType): String {
        val name = userViewModel.uiState.value.name
        val age = userViewModel.uiState.value.age
        val interests = userViewModel.uiState.value.selectedInterests

        val usedInstructionPrompt = when (promptType) {
            InstructionPromptType.BOOK -> INSTRUCTION_PROMPT
            InstructionPromptType.CHAPTER -> INSTRUCTION_PROMPT_CHAPTER
            InstructionPromptType.PAGE -> TODO()
        }

        val personalizedPrompt = usedInstructionPrompt
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

        return personalizedPrompt
    }

    private fun changeModel(model: GenAiModel) {
        genAiModule.setModelRepository(model)
        updateUiState(_uiState.value.copy(currentModel = model))
    }

    private suspend fun getTextResponse(prompt: String): String? {
        return withContext(Dispatchers.IO) {
            updateUiState(_uiState.value.copy(isLoading = true))

            val startTime = System.currentTimeMillis()

            genAiModule.repository.getTextResponse(prompt)
                .onFailure { error ->
                    errorHandling(error)
                    return@withContext null
                }
                .onSuccess { response ->
                    Log.e(TAG, response)
                    val duration = System.currentTimeMillis() - startTime

                    updateUiState(
                        _uiState.value.copy(
                            isLoading = false,
                            isSuccess = true,
                            lastResponseTime = duration
                        )
                    )

                    return@withContext response
                }

            null
        }
    }

    private suspend fun getImageResponse(prompt: String): String? {
        return withContext(Dispatchers.IO) {
            updateUiState(_uiState.value.copy(isLoading = true))

            val startTime = System.currentTimeMillis()

            genAiModule.repository.getImageResponse(prompt)
                .onFailure { error ->
                    errorHandling(error)
                    return@withContext null
                }
                .onSuccess { response ->
                    Log.e(TAG, response)
                    val duration = System.currentTimeMillis() - startTime

                    updateUiState(
                        _uiState.value.copy(
                            isLoading = false,
                            isSuccess = true,
                            lastResponseTime = duration
                        )
                    )

                    return@withContext response
                }

            null
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

private enum class InstructionPromptType {
    BOOK,
    CHAPTER,
    PAGE
}