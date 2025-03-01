package com.flingoapp.flingo.viewmodel

import PageDetails
import PageDetailsType
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flingoapp.flingo.data.model.Book
import com.flingoapp.flingo.data.model.Chapter
import com.flingoapp.flingo.data.model.ChapterType
import com.flingoapp.flingo.data.model.genAi.GenAiModelPerformance
import com.flingoapp.flingo.data.model.genAi.GenAiProvider
import com.flingoapp.flingo.data.network.ConnectivityObserver
import com.flingoapp.flingo.data.repository.PersonalizationRepository
import com.flingoapp.flingo.di.GenAiModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

data class PersonalizationUiState(
    val isConnectedToNetwork: Boolean = false,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isSuccess: Boolean = false,
    val currentModel: GenAiProvider = GenAiProvider.OPEN_AI,
    //debug specific
    val isDebug: Boolean = false,
    val lastResponseTime: Long? = null,
    val usedPrompt: String? = null,
    val childName: String? = null,
    val childAge: Int? = null,
    val generateImages: Boolean = false
)

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
            combine(isConnected, genAiModule.currentModelProvider) { connected, model ->
                _uiState.value.copy(isConnectedToNetwork = connected, currentModel = model)
            }.collect { newState ->
                updateUiState(newState)
            }
        }
    }

    fun onAction(action: MainAction.PersonalizationAction) {
        when (action) {
            is MainAction.PersonalizationAction.GenerateBookFromText -> generateBookFromText(action.scannedText)
            is MainAction.PersonalizationAction.GenerateBook -> generateBook(action.book)
            is MainAction.PersonalizationAction.GenerateChapter -> generateChapter(action.sourceChapter)
            is MainAction.PersonalizationAction.GeneratePage -> generatePage(action.sourceChapter, action.sourceBook)
            is MainAction.PersonalizationAction.ChangeModel -> changeModel(action.model)
            MainAction.PersonalizationAction.ToggleDebugMode -> toggleDebugMode()
            is MainAction.PersonalizationAction.GenerateImage -> generateImage(action.context)
            MainAction.PersonalizationAction.ToggleGenerateImages -> toggleGenerateImages()
            is MainAction.PersonalizationAction.GenerateChapterFromText -> generateChapterFromText(action.pageDetailsType)
            is MainAction.PersonalizationAction.ChangeModelPerformance -> changeModelPerformance(action.modelPerformance)
        }
    }

    private fun generateBookFromText(scannedText: String) {
        viewModelScope.launch {
            updateUiState(
                _uiState.value.copy(
                    isLoading = true,
                    isError = false,
                    isSuccess = false
                )
            )
            val startTime = System.currentTimeMillis()

            val generatedResult = personalizationRepository.generateBookFromText(
                scannedText = scannedText,
                generateImages = uiState.value.generateImages,
                personalizationAspects = getPersonalizationAspects()
            )

            generatedResult
                .onFailure {
                    errorHandling(it)
                    return@launch
                }.onSuccess { (personalizedText, book) ->
                    bookViewModel.onAction(
                        MainAction.BookAction.AddBook(
                            book = book,
                            author = _uiState.value.currentModel.provider
                        )
                    )

                    updateUiState(
                        _uiState.value.copy(
                            isSuccess = true
                        )
                    )

//                    generate 3 chapters for assessing reading
                    val chapter1Job = buildAndAddChapterToBook(
                        personalizedText = personalizedText,
                        type = PageDetailsType.REMOVE_WORD,
                        quizType = null,
                        pageAmount = 3,
                        bookId = book.id
                    )
                    val chapter2Job = buildAndAddChapterToBook(
                        personalizedText = personalizedText,
                        type = PageDetailsType.QUIZ,
                        quizType = PageDetails.Quiz.QuizType.TRUE_OR_FALSE,
                        pageAmount = 2,
                        bookId = book.id
                    )
                    val chapter3Job = buildAndAddChapterToBook(
                        personalizedText = personalizedText,
                        type = PageDetailsType.ORDER_STORY,
                        quizType = null,
                        pageAmount = 3,
                        bookId = book.id
                    )

                    //TODO: refactor, messy
                    listOf(chapter1Job, chapter2Job, chapter3Job).joinAll()
                    updateUiState(
                        _uiState.value.copy(
                            isLoading = false,
                            lastResponseTime = System.currentTimeMillis() - startTime
                        )
                    )
                }
        }
    }

    private fun buildAndAddChapterToBook(
        personalizedText: String,
        type: PageDetailsType,
        quizType: PageDetails.Quiz.QuizType?,
        pageAmount: Int,
        bookId: String
    ) = viewModelScope.launch(Dispatchers.IO) {
        val pagesResult = if (type == PageDetailsType.QUIZ || quizType != null) {
            //TODO: quick and dirty special handling to create both types of quizzes - refactor
            val singleChoicePages = personalizationRepository.generatePagesFromText(
                personalizedText = personalizedText,
                type = type,
                quizType = PageDetails.Quiz.QuizType.SINGLE_CHOICE,
                pageAmount = pageAmount
            )
            val trueOrFalsePages = personalizationRepository.generatePagesFromText(
                personalizedText = personalizedText,
                type = type,
                quizType = PageDetails.Quiz.QuizType.TRUE_OR_FALSE,
                pageAmount = pageAmount
            )

            val title = singleChoicePages.getOrThrow().first

            //TODO: very quick and dirty
            Result.success(
                Pair(
                    title,
                    singleChoicePages.getOrThrow().second + trueOrFalsePages.getOrThrow().second
                )
            )
        } else {
            personalizationRepository.generatePagesFromText(
                personalizedText = personalizedText,
                type = type,
                quizType = null,
                pageAmount = pageAmount
            )
        }

        pagesResult.onFailure {
            errorHandling(it)
            return@launch
        }.onSuccess { (title, pages) ->
            val chapter = Chapter(
                author = genAiModule.currentModelProvider.value.provider,
                title = title,
                type = ChapterType.CHALLENGE,
                description = "",
                coverImage = "",
                pages = pages
            )

            bookViewModel.onAction(
                MainAction.BookAction.AddChapter(
                    chapter = chapter,
                    author = _uiState.value.currentModel.provider,
                    bookId = bookId
                )
            )
        }
    }

    private fun generateBook(sourceBook: Book) {
        viewModelScope.launch {
            updateUiState(
                _uiState.value.copy(
                    isLoading = true,
                    isError = false,
                    isSuccess = false
                )
            )

            val startTime = System.currentTimeMillis()
            personalizationRepository.generateBook(
                sourceBook = sourceBook,
                generateImages = uiState.value.generateImages,
                personalizationAspects = getPersonalizationAspects()
            ).onFailure {
                errorHandling(it)
                return@launch
            }.onSuccess { book ->
                bookViewModel.onAction(
                    MainAction.BookAction.AddBook(
                        book = book,
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

    private fun generateChapterFromText(pageDetailsType: PageDetailsType) {
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
            val readingPages = bookViewModel.getReadingPages(currentBook.id)

            if (readingPages == null) {
                errorHandling(Exception("No reading pages found"))
                return@launch
            } else {
                val readingDetails = readingPages.map { it.details as PageDetails.Read }
                val readingText = readingDetails.joinToString { it.content }

                val generatedChapter = buildAndAddChapterToBook(
                    personalizedText = readingText,
                    type = pageDetailsType,
                    quizType = null,
                    pageAmount = 3,
                    bookId = currentBook.id
                )

                generatedChapter.join()
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

    private fun generateChapter(sourceChapter: Chapter) {
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
            val generatedChapter = personalizationRepository.generateChapter(getPersonalizationAspects(), sourceChapter)

            generatedChapter
                .onFailure {
                    errorHandling(it)
                    return@launch
                }.onSuccess { chapter ->
                    bookViewModel.onAction(
                        MainAction.BookAction.AddChapterJson(
                            chapterJson = Json.encodeToString(chapter),
                            author = _uiState.value.currentModel.provider,
                            bookId = currentBook.id
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

    private fun generatePage(sourceChapter: Chapter, sourceBook: Book) {
        viewModelScope.launch {
            updateUiState(
                _uiState.value.copy(
                    isLoading = true,
                    isError = false,
                    isSuccess = false
                )
            )
            val startTime = System.currentTimeMillis()
            val generatedPage =
                personalizationRepository.generatePage(getPersonalizationAspects(), sourceChapter)

            generatedPage
                .onFailure {
                    errorHandling(it)
                    return@launch
                }.onSuccess { page ->
                    bookViewModel.onAction(
                        MainAction.BookAction.AddPage(
                            page = page,
                            author = _uiState.value.currentModel.provider,
                            chapterId = sourceChapter.id,
                            bookId = sourceBook.id
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

            //TODO: implement
        }
    }

    private fun changeModel(model: GenAiProvider) {
        genAiModule.setModelRepository(model)
    }

    private fun changeModelPerformance(modelPerformance: GenAiModelPerformance) {
        genAiModule.setModelPerformance(modelPerformance)
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

    private fun toggleGenerateImages() {
        val currentState = _uiState.value.generateImages
        updateUiState(_uiState.value.copy(generateImages = !currentState))
    }

    private fun getPersonalizationAspects(): PersonalizationAspects {
        return PersonalizationAspects(
            age = userViewModel.uiState.value.age,
            interests = userViewModel.uiState.value.selectedInterests,
            name = userViewModel.uiState.value.name,
            imageStyle = userViewModel.uiState.value.selectedImageStyle
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

//TODO: add specific prompts to interests and image styles
data class PersonalizationAspects(
    val age: Int,
    val name: String,
    val interests: List<String>,
    val imageStyle: String
)