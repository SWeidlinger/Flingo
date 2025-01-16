package com.flingoapp.flingo.viewmodels.main

import android.util.Log
import androidx.lifecycle.ViewModel
import com.flingoapp.flingo.data.models.User
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
            is MainIntent.OnBookSelected -> selectBook(action.bookIndex)
            is MainIntent.OnChapterSelected -> selectChapter(action.chapterIndex)
            is MainIntent.OnCurrentChapterCompleted -> completeCurrentChapter()
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
        val currentBook = _uiState.value.userData?.books?.get(bookIndex)

        if (currentBook == null) {
            updateUiState(_uiState.value.copy(isError = true))
        } else {
            updateUiState(_uiState.value.copy(isError = false, currentBook = currentBook))
        }
    }

    /**
     * Select chapter
     *
     * @param chapterIndex
     */
    private fun selectChapter(chapterIndex: Int) {
        val currentBook = _uiState.value.currentBook
        if (currentBook != null) {
            updateUiState(_uiState.value.copy(currentChapter = currentBook.chapters[chapterIndex]))
        } else {
            updateUiState(_uiState.value.copy(isError = true))
        }
    }

    //TODO: fix uiState lists before usage, so it can be tracked by compose
    // current problem on update of the uiState list changes do not propagate
    // probably the case since of the custom classes in the uiState
    private fun completeCurrentChapter() {
        Log.e(TAG, "in completeCurrentChapter")
        val currentChapter = _uiState.value.currentChapter
        if (currentChapter != null) {
            updateUiState(_uiState.value.copy(currentChapter = currentChapter.copy(isCompleted = true)))
            Log.e(TAG, "current chapter completed")
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