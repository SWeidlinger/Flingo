package com.flingoapp.flingo.viewmodels.book

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flingoapp.flingo.data.models.book.Book
import com.flingoapp.flingo.data.models.book.Chapter
import com.flingoapp.flingo.viewmodels.MainAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor() : ViewModel() {
    companion object {
        private const val TAG = "BookViewModel"
    }

    private val _uiState = MutableStateFlow(BookUiState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: MainAction.BookAction) {
        when (action) {
            is MainAction.BookAction.SelectBook -> selectBook(action.bookIndex)
            is MainAction.BookAction.SelectChapter -> selectChapter(action.chapterIndex)
            MainAction.BookAction.CompleteChapter -> completeChapter()
            is MainAction.BookAction.FetchBooks -> fetchBooks(action.booksJson)
        }
    }

    private fun fetchBooks(bookJsonList: List<String>) {
        viewModelScope.launch {
            updateUiState(_uiState.value.copy(isLoading = true))

            val deserializer = Json {
                ignoreUnknownKeys = true
            }

            val books =
                bookJsonList.map { deserializer.decodeFromString<Book>(it) } as ArrayList<Book>

            updateUiState(
                _uiState.value.copy(
                    isLoading = false,
                    isError = false,
                    books = books
                )
            )
        }
    }

    private fun selectBook(bookIndex: Int) {
        updateUiState(_uiState.value.copy(currentBookId = bookIndex))
    }

    private fun selectChapter(chapterIndex: Int) {
        updateUiState(_uiState.value.copy(currentChapterId = chapterIndex))
    }

    private fun completeChapter() {
        val currentChapter = getCurrentChapter()
        currentChapter?.let { it.isCompleted = true }
    }

    fun getCurrentChapter(): Chapter? {
        val currentBook = getCurrentBook()
        return currentBook?.chapters?.get(_uiState.value.currentChapterId ?: return null)
    }

    fun getCurrentBook(): Book? {
        val currentBookId = _uiState.value.currentBookId
        return _uiState.value.books[currentBookId ?: return null]
    }

    private fun updateUiState(newState: BookUiState) {
        _uiState.update { newState }
    }
}