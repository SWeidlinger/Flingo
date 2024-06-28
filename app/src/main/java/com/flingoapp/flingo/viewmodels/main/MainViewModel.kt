package com.flingoapp.flingo.viewmodels.main

import androidx.lifecycle.ViewModel
import com.flingoapp.flingo.data.models.User
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {
    private val TAG = "MainViewModel"

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: MainIntent) {
        when (action) {
            is MainIntent.OnLoading -> {
                updateUiState(_uiState.value.copy(isLoading = true))
            }

            is MainIntent.OnMockFetchData -> mockFetchBockData(action.json)
            is MainIntent.OnBookSelected -> selectBook(action.bookIndex)
            is MainIntent.OnChapterSelected -> selectChapter(action.chapterIndex)
        }
    }

    private fun mockFetchBockData(json: String) {
        updateUiState(_uiState.value.copy(isLoading = true))

        val user = Gson().fromJson(json, User::class.java)

        updateUiState(
            _uiState.value.copy(
                isLoading = false,
                userData = user
            )
        )
    }

    private fun selectBook(bookIndex: Int) {
        val currentBook = _uiState.value.userData?.books?.get(bookIndex)

        if (currentBook == null) {
            updateUiState(_uiState.value.copy(isError = true))
        } else {
            updateUiState(_uiState.value.copy(isError = false, currentBook = currentBook))
        }
    }

    private fun selectChapter(chapterIndex: Int) {
        val currentBook = _uiState.value.currentBook
        if (currentBook != null) {
            updateUiState(_uiState.value.copy(currentChapter = currentBook.chapters[chapterIndex]))
        } else {
            updateUiState(_uiState.value.copy(isError = true))
        }
    }

    private fun updateUiState(newUiState: MainUiState) {
        _uiState.update { newUiState }
    }
}