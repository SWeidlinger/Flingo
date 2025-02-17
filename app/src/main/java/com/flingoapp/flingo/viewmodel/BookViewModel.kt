package com.flingoapp.flingo.viewmodel

import PageDetails
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flingoapp.flingo.data.model.Book
import com.flingoapp.flingo.data.model.Chapter
import com.flingoapp.flingo.data.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BookUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val books: List<Book> = listOf(),
    val currentBookId: String? = null,
    val currentChapterId: String? = null,
    val currentPageId: Int? = null
)

class BookViewModel(
    private val bookRepository: BookRepository
) : ViewModel() {
    companion object {
        private const val TAG = "BookViewModel"
    }

    private val _uiState = MutableStateFlow(BookUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            bookRepository.books.collect {
                updateUiState(_uiState.value.copy(books = it))
            }
        }
    }

    fun onAction(action: MainAction.BookAction) {
        when (action) {
            is MainAction.BookAction.SelectBook -> selectBook(action.bookId)
            is MainAction.BookAction.SelectChapter -> selectChapter(action.chapterId)
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
            updateUiState(_uiState.value.copy(isLoading = true))
            bookRepository.fetchBook(bookJson)
                .onFailure {
                    updateUiState(_uiState.value.copy(isLoading = false, isError = true))
                }.onSuccess { book ->
                    val bookWithAuthor = book.copy(author = author)
                    bookRepository.addBook(bookWithAuthor)
                        .onFailure {
                            updateUiState(_uiState.value.copy(isLoading = false, isError = true))
                        }.onSuccess {
                            updateUiState(_uiState.value.copy(isLoading = false, isError = false))
                        }
                }
        }
    }

    private fun addChapter(chapterJson: String, author: String) {
        viewModelScope.launch {
            updateUiState(_uiState.value.copy(isLoading = true))
            bookRepository.fetchChapter(chapterJson)
                .onFailure {
                    updateUiState(_uiState.value.copy(isLoading = false, isError = true))
                }.onSuccess { chapter ->
                    val chapterWithAuthor = chapter.copy(author = author)
                    //TODO: needs to be evaluated how to best handle not having a corresponding book
                    bookRepository.addChapter(
                        chapter = chapterWithAuthor,
                        bookId = _uiState.value.currentBookId.toString()
                    )
                        .onFailure {
                            updateUiState(_uiState.value.copy(isLoading = false, isError = true))
                        }.onSuccess {
                            updateUiState(_uiState.value.copy(isLoading = false, isError = false))
                        }
                }
        }
    }

    //TODO: rework functionality and add image author
    private fun addImage(imageUrl: String, author: String) {
        viewModelScope.launch {
            val currentPage = bookRepository.getPage(
                chapterId = _uiState.value.currentChapterId.toString(),
                bookId = _uiState.value.currentBookId.toString(),
                pageId = _uiState.value.currentPageId.toString()
            ).getOrThrow()

            val newDetails = currentPage.details as PageDetails.Read
            val newPage = currentPage.copy(details = newDetails.copy(imageUrl = imageUrl))

            bookRepository.updatePage(
                page = newPage,
                bookId = _uiState.value.currentBookId.toString(),
                chapterId = _uiState.value.currentChapterId.toString()
            )
                .onFailure {
                    updateUiState(_uiState.value.copy(isError = true))
                }
        }
    }

    private fun fetchBooks(bookJsonList: List<String>) {
        viewModelScope.launch {
            updateUiState(_uiState.value.copy(isLoading = true))

            val books = bookJsonList.map { bookRepository.fetchBook(it) }
            books.forEach { book ->
                book.onFailure {
                    updateUiState(_uiState.value.copy(isLoading = false, isError = true))
                }
                    .onSuccess { safeBook ->
                        bookRepository.addBook(safeBook).onFailure {
                            updateUiState(
                                _uiState.value.copy(isLoading = false, isError = true)
                            )
                        }
                    }
            }
            updateUiState(_uiState.value.copy(isLoading = false))
        }
    }

    private fun selectBook(bookId: String) {
        updateUiState(_uiState.value.copy(currentBookId = bookId))
    }

    private fun selectChapter(chapterId: String) {
        updateUiState(_uiState.value.copy(currentChapterId = chapterId))
    }

    private fun selectPage(pageIndex: Int) {
        updateUiState(_uiState.value.copy(currentPageId = pageIndex))
    }

    private fun completeChapter() {
        viewModelScope.launch {
            val currentChapter = bookRepository.getChapter(
                chapterId = _uiState.value.currentChapterId.toString(),
                bookId = _uiState.value.currentBookId.toString()
            )

            currentChapter.onSuccess {
                val newChapter = it.copy(isCompleted = true)
                bookRepository.updateChapter(
                    chapter = newChapter,
                    bookId = _uiState.value.currentBookId.toString()
                )
                    .onFailure {
                        updateUiState(_uiState.value.copy(isError = true))
                    }
            }
        }
    }

    private fun completePage(pageIndex: Int) {
        viewModelScope.launch {
            val currentPage = bookRepository.getPage(
                chapterId = _uiState.value.currentChapterId.toString(),
                bookId = _uiState.value.currentBookId.toString(),
                pageId = pageIndex.toString()
            )

            currentPage.onSuccess {
                val newPage = it.copy(isCompleted = true)
                bookRepository.updatePage(
                    page = newPage,
                    bookId = _uiState.value.currentBookId.toString(),
                    chapterId = _uiState.value.currentChapterId.toString()
                )
                    .onFailure {
                        updateUiState(_uiState.value.copy(isError = true))
                    }
            }
        }
    }

    //TODO: getting the book should be handled differently, and these functions should probably be removed
    fun getCurrentBook(): Book {
        return bookRepository.getBook(_uiState.value.currentBookId.toString()).getOrThrow()
    }

    fun getCurrentChapter(): Chapter {
        return bookRepository.getChapter(
            bookId = _uiState.value.currentBookId.toString(),
            chapterId = _uiState.value.currentChapterId.toString()
        ).getOrThrow()
    }

    private fun updateUiState(newState: BookUiState) {
        _uiState.update { newState }
    }
}