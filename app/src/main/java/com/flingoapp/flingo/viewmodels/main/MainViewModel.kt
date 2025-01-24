package com.flingoapp.flingo.viewmodels.main

import android.util.Log
import androidx.lifecycle.ViewModel
import com.flingoapp.flingo.data.models.User
import com.flingoapp.flingo.data.models.book.Book
import com.flingoapp.flingo.data.models.book.Chapter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json

/**
 * Main view model
 *
 * @constructor Create empty Main view model
 */
class MainViewModel : ViewModel() {
    private val TAG = "MainViewModel"

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    /**
     * On action
     *
     * @param action
     */
    fun onAction(action: MainIntent) {
        when (action) {
            is MainIntent.OnLoading -> {
                updateUiState(_uiState.value.copy(isLoading = true))
            }

            is MainIntent.OnMockFetchData -> mockFetchBookData(action.json)
            is MainIntent.OnBookSelect -> selectBook(action.bookIndex)
            is MainIntent.OnChapterSelect -> selectChapter(action.chapterIndex)
            is MainIntent.OnCurrentChapterComplete -> completeCurrentChapter()
            is MainIntent.OnUserLiveIncrease -> increaseUserLives()
            is MainIntent.OnUserLiveDecrease -> decreaseUserLives()
            is MainIntent.OnInterestSelect -> selectInterest(action.selectedInterest)
        }
    }

    /**
     * Mock fetch book data
     *
     * @param json
     */
    private fun mockFetchBookData(json: String) {
        updateUiState(_uiState.value.copy(isLoading = true))

        val deserializer = Json {
            ignoreUnknownKeys = true
        }
        val user = deserializer.decodeFromString<User>(json)

        updateUiState(
            _uiState.value.copy(
                isLoading = false,
                userData = user
            )
        )
    }

    /**
     * Select book
     *
     * @param bookIndex
     */
    private fun selectBook(bookIndex: Int) {
        updateUiState(
            _uiState.value.copy(
                currentBookId = bookIndex,
                isError = false
            )
        )
    }

    fun getCurrentBook(): Book? {
        val currentBookId = _uiState.value.currentBookId
        if (currentBookId == null) {
            Log.e(TAG, "currentBookId is null")
            updateUiState(_uiState.value.copy(isError = true))
            return null
        }
        return _uiState.value.userData?.books?.get(currentBookId)
    }

    /**
     * Select chapter
     *
     * @param chapterIndex
     */
    private fun selectChapter(chapterIndex: Int) {
        updateUiState(
            _uiState.value.copy(
                currentChapterId = chapterIndex,
                isError = false
            )
        )
    }

    fun getCurrentChapter(): Chapter? {
        val currentBook = getCurrentBook()
        val currentChapterId = _uiState.value.currentChapterId
        if (currentBook == null || currentChapterId == null) {
            Log.e(TAG, "${if (currentBook == null) "currentBook" else "currentChapterId"} is null")
            updateUiState(_uiState.value.copy(isError = true))
            return null
        }
        return currentBook.chapters[currentChapterId]
    }

    private fun completeCurrentChapter() {
        val currentChapter = getCurrentChapter()
        if (currentChapter != null) {
            currentChapter.isCompleted = true
            Log.i(TAG, "current chapter completed")
        }
    }

    private fun increaseUserLives() {
        val userData = _uiState.value.userData
        if (userData != null) {
            updateUiState(_uiState.value.copy(userData = userData.copy(currentLives = userData.currentLives + 1)))
        } else {
            updateUiState(_uiState.value.copy(isError = true))
        }
    }

    private fun decreaseUserLives() {
        val userData = _uiState.value.userData
        if (userData != null) {
            if (userData.currentLives > 0) {
                updateUiState(_uiState.value.copy(userData = userData.copy(currentLives = userData.currentLives - 1)))
            }
        } else {
            updateUiState(_uiState.value.copy(isError = true))
        }
    }

    //for now only supports 1 interest at a time
    private fun selectInterest(selectedInterest: String) {
        val userData = _uiState.value.userData
        if (userData != null) {
            val selectedInterests = arrayListOf(selectedInterest)
            updateUiState(_uiState.value.copy(userData = userData.copy(selectedInterests = selectedInterests)))
        } else {
            updateUiState(_uiState.value.copy(isError = true))
        }
    }

    /**
     * Update ui state
     *
     * @param newUiState
     */
    private fun updateUiState(newUiState: MainUiState) {
        _uiState.update { newUiState }
    }
}