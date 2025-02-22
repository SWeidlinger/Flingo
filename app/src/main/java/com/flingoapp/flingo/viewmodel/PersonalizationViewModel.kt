package com.flingoapp.flingo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flingoapp.flingo.data.model.genAi.GenAiModel
import com.flingoapp.flingo.data.network.ConnectivityObserver
import com.flingoapp.flingo.data.repository.PersonalizationRepository
import com.flingoapp.flingo.di.GenAiModule
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
    private val personalizationRepository: PersonalizationRepository,
    connectivityObserver: ConnectivityObserver
) : ViewModel() {
    companion object {
        private const val TAG = "PersonalizationViewModel"
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
            combine(isConnected, genAiModule.currentModel) { connected, model ->
                _uiState.value.copy(isConnectedToNetwork = connected, currentModel = model)
            }.collect { newState ->
                updateUiState(newState)
            }
        }
    }

    fun onAction(action: MainAction.PersonalizationAction) {
        when (action) {
            is MainAction.PersonalizationAction.GenerateBook -> generateBook(action.scannedText)
            MainAction.PersonalizationAction.GenerateChapter -> generateChapter()
            MainAction.PersonalizationAction.GeneratePage -> generatePage()
            is MainAction.PersonalizationAction.ChangeModel -> changeModel(action.model)
            MainAction.PersonalizationAction.ToggleDebugMode -> toggleDebugMode()
            is MainAction.PersonalizationAction.GenerateImage -> generateImage(action.context)
        }
    }

    private fun generateBook(scannedText: String) {
        viewModelScope.launch {
            updateUiState(
                _uiState.value.copy(
                    isLoading = true,
                    isError = false,
                    isSuccess = false
                )
            )
            val startTime = System.currentTimeMillis()

            val generatedBook = personalizationRepository.generateBook(getPersonalizationAspects())

            generatedBook
                .onFailure {
                    errorHandling(it)
                    return@launch
                }.onSuccess { book ->
                    bookViewModel.onAction(
                        MainAction.BookAction.AddBook(
                            bookJson = Json.encodeToString(book),
                            author = _uiState.value.currentModel.provider
                        )
                    )

                    updateUiState(
                        _uiState.value.copy(
                            isLoading = false,
                            isSuccess = true,
                            lastResponseTime = System.currentTimeMillis() - startTime
                        )
                    )
                }
        }
    }

    private fun generateChapter() {
        viewModelScope.launch {
            updateUiState(
                _uiState.value.copy(
                    isLoading = true,
                    isError = false,
                    isSuccess = false
                )
            )
            val startTime = System.currentTimeMillis()

            val currentBook = bookViewModel.getCurrentBook()
            val generatedChapter =
                personalizationRepository.generateChapter(getPersonalizationAspects(), currentBook)

            generatedChapter
                .onFailure {
                    errorHandling(it)
                    return@launch
                }.onSuccess { chapter ->
                    bookViewModel.onAction(
                        MainAction.BookAction.AddChapter(
                            chapterJson = Json.encodeToString(chapter),
                            author = _uiState.value.currentModel.provider
                        )
                    )

                    updateUiState(
                        _uiState.value.copy(
                            isLoading = false,
                            isSuccess = true,
                            lastResponseTime = System.currentTimeMillis() - startTime
                        )
                    )
                }
        }
    }

    private fun generatePage() {
        viewModelScope.launch {
            updateUiState(
                _uiState.value.copy(
                    isLoading = true,
                    isError = false,
                    isSuccess = false
                )
            )
            val startTime = System.currentTimeMillis()

            val currentChapter = bookViewModel.getCurrentChapter()
            val generatedPage =
                personalizationRepository.generatePage(getPersonalizationAspects(), currentChapter)

            generatedPage
                .onFailure {
                    errorHandling(it)
                    return@launch
                }.onSuccess { page ->
                    bookViewModel.onAction(
                        MainAction.BookAction.AddPage(
                            pageJson = Json.encodeToString(page),
                            author = _uiState.value.currentModel.provider
                        )
                    )

                    updateUiState(
                        _uiState.value.copy(
                            isLoading = false,
                            isSuccess = true,
                            lastResponseTime = System.currentTimeMillis() - startTime
                        )
                    )
                }
        }
    }

    private fun generateImage(context: String) {
        viewModelScope.launch {
            updateUiState(
                _uiState.value.copy(
                    isLoading = true,
                    isError = false
                )
            )
            val startTime = System.currentTimeMillis()

            val generatedImage = personalizationRepository.generateImage()

            generatedImage
                .onFailure {
                    errorHandling(it)
                    return@launch
                }.onSuccess { image ->
                    //TODO

                    updateUiState(
                        _uiState.value.copy(
                            isLoading = false,
                            isSuccess = true,
                            lastResponseTime = System.currentTimeMillis() - startTime
                        )
                    )
                }
        }
    }

    private fun changeModel(model: GenAiModel) {
        genAiModule.setModelRepository(model)
    }

    private fun errorHandling(error: Throwable) {
        Log.e(TAG, "Error: ${error.message}")
        updateUiState(
            _uiState.value.copy(
                isLoading = false,
                isError = true,
                isSuccess = false
            )
        )
        viewModelScope.launch {
            delay(3000)
            updateUiState(_uiState.value.copy(isError = false))
        }
    }

    private fun toggleDebugMode() {
        val currentState = _uiState.value.isDebug
        updateUiState(_uiState.value.copy(isDebug = !currentState))
    }

    private fun getPersonalizationAspects(): PersonalizationAspects {
        return PersonalizationAspects(
            age = userViewModel.uiState.value.age,
            interests = userViewModel.uiState.value.selectedInterests,
            name = userViewModel.uiState.value.name
        )
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

data class PersonalizationAspects(
    val age: Int,
    val name: String,
    val interests: List<String>
)