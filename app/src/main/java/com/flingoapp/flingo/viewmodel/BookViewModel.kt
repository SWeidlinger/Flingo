package com.flingoapp.flingo.viewmodel

import PageDetails
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flingoapp.flingo.data.model.Book
import com.flingoapp.flingo.data.model.Chapter
import com.flingoapp.flingo.data.model.page.Page
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

data class BookUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val books: List<Book> = listOf(),
    val currentBookId: Int? = null,
    val currentChapterId: Int? = null,
    val currentPageId: Int? = null
)

class BookViewModel : ViewModel() {
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
            is MainAction.BookAction.CompletePage -> completePage(action.pageIndex)
            is MainAction.BookAction.AddBook -> addBook(
                bookJson = action.bookJson,
                author = action.author
            )

            is MainAction.BookAction.AddChapter -> addChapter(
                chapterJson = action.chapterJson,
                author = action.author
            )

            is MainAction.BookAction.SelectPage -> selectPage(action.pageIndex)
            is MainAction.BookAction.AddImage -> addImage(action.imageUrl, action.author)
        }
    }

    private fun addBook(bookJson: String, author: String) {
        viewModelScope.launch {
            val book = parseJsonToBook(bookJson) ?: return@launch

            val newBookList = _uiState.value.books.toMutableList()
            newBookList.add(book.copy(author = author))
            updateUiState(
                _uiState.value.copy(
                    books = newBookList,
                    isLoading = false
                )
            )
        }
    }

    //TODO: improve, simplify, fix logic of having no chapters in uiState
    private fun addChapter(chapterJson: String, author: String) {
        //TODO: use author to show which model created chapter
        viewModelScope.launch {
            val chapter = parseJsonToChapter(chapterJson) ?: return@launch

            val currentBook = getCurrentBook() ?: return@launch
            val highestChapterId = currentBook.chapters.maxOf { it.id.toInt() }
            val newBook = currentBook.copy(
                chapters = currentBook.chapters + chapter.copy(id = (highestChapterId + 1).toString())
            )

            val newBookList = _uiState.value.books.toMutableList()
            newBookList[_uiState.value.currentBookId ?: return@launch] = newBook
            updateUiState(
                _uiState.value.copy(
                    books = newBookList,
                    isLoading = false
                )
            )
        }
    }

    //TODO: improve this, currently it is so messy
    private fun addImage(imageUrl: String, author: String) {
        viewModelScope.launch {
            val currentBook = getCurrentBook() ?: return@launch
            val currentChapter = getCurrentChapter() ?: return@launch
            val currentPage = getCurrentPage() ?: return@launch

            val newDetails = currentPage.details as PageDetails.Read
            val newPage = currentPage.copy(details = newDetails.copy(imageUrl = imageUrl))
            val newPages = currentChapter.pages?.toMutableList() ?: return@launch
            newPages[_uiState.value.currentPageId ?: return@launch] = newPage

            val newChapter = currentChapter.copy(pages = newPages)
            val newChapterList = currentBook.chapters.toMutableList()
            newChapterList[_uiState.value.currentChapterId ?: return@launch] = newChapter

            val newBook = currentBook.copy(
                chapters = newChapterList
            )

            val newBookList = _uiState.value.books.toMutableList()
            newBookList[_uiState.value.currentBookId ?: return@launch] = newBook
            updateUiState(
                _uiState.value.copy(
                    books = newBookList,
                    isLoading = false
                )
            )
        }
    }

    private fun fetchBooks(bookJsonList: List<String>) {
        viewModelScope.launch {
            updateUiState(_uiState.value.copy(isLoading = true))

            val books = bookJsonList.mapNotNull { parseJsonToBook(it) }

            updateUiState(
                _uiState.value.copy(
                    isLoading = false,
                    isError = false,
                    books = books
                )
            )
        }
    }

    private fun parseJsonToBook(json: String): Book? {
        updateUiState(_uiState.value.copy(isLoading = true, isError = false))
        return try {
            val deserializer = Json { ignoreUnknownKeys = true }
            val book = deserializer.decodeFromString<Book>(json)
            updateUiState(_uiState.value.copy(isLoading = false))

            book
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            updateUiState(_uiState.value.copy(isLoading = false, isError = true))

            null
        }
    }

    private fun parseJsonToChapter(json: String): Chapter? {
        updateUiState(_uiState.value.copy(isLoading = true, isError = false))
        return try {
            val deserializer = Json { ignoreUnknownKeys = true }
            val chapter = deserializer.decodeFromString<Chapter>(json)
            updateUiState(_uiState.value.copy(isLoading = false))

            chapter
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            updateUiState(_uiState.value.copy(isLoading = false, isError = true))

            null
        }
    }

    private fun selectBook(bookIndex: Int) {
        updateUiState(_uiState.value.copy(currentBookId = bookIndex))
    }

    private fun selectChapter(chapterIndex: Int) {
        updateUiState(_uiState.value.copy(currentChapterId = chapterIndex))
    }

    private fun selectPage(pageIndex: Int) {
        updateUiState(_uiState.value.copy(currentPageId = pageIndex))
    }

    private fun completeChapter() {
        val currentChapter = getCurrentChapter()
        currentChapter?.let { it.isCompleted = true }
    }

    private fun completePage(pageIndex: Int) {
        val currentPage = getCurrentChapter()?.pages?.get(pageIndex)
        currentPage?.isCompleted = true
    }

    fun getCurrentPage(): Page? {
        val currentChapter = getCurrentChapter()
        return currentChapter?.pages?.get(_uiState.value.currentPageId ?: return null)
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