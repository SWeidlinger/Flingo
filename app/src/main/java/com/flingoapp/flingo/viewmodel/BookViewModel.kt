package com.flingoapp.flingo.viewmodel

import PageDetails
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flingoapp.flingo.data.model.Book
import com.flingoapp.flingo.data.model.Chapter
import com.flingoapp.flingo.data.model.ChapterType
import com.flingoapp.flingo.data.model.page.Page
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
            is MainAction.BookAction.AddBookJson -> addBookJson(
                bookJson = action.bookJson,
                author = action.author
            )

            is MainAction.BookAction.AddChapterJson -> addChapterJson(
                chapterJson = action.chapterJson,
                author = action.author,
                bookId = action.bookId
            )

            is MainAction.BookAction.SelectPage -> selectPage(action.pageIndex)
            is MainAction.BookAction.AddImage -> addImage(action.imageUrl, action.author)
            is MainAction.BookAction.AddPageJson -> addPageJson(
                pageJson = action.pageJson,
                author = action.author,
                chapterId = action.chapterId,
                bookId = action.bookId
            )

            is MainAction.BookAction.AddBook -> addBook(
                book = action.book,
                author = action.author
            )

            is MainAction.BookAction.AddChapter -> addChapter(
                chapter = action.chapter,
                author = action.author,
                bookId = action.bookId
            )

            is MainAction.BookAction.AddPage -> addPage(
                page = action.page,
                author = action.author,
                chapterId = action.chapterId,
                bookId = action.bookId
            )
        }
    }

    private fun addBook(book: Book, author: String) {
        viewModelScope.launch {
            updateUiState(_uiState.value.copy(isLoading = true))
            val bookWithAuthor = book.copy(author = author)
            bookRepository.addBook(bookWithAuthor)
                .onFailure {
                    updateUiState(_uiState.value.copy(isLoading = false, isError = true))
                }.onSuccess {
                    updateUiState(_uiState.value.copy(isLoading = false, isError = false))
                }
        }
    }

    private fun addBookJson(bookJson: String, author: String) {
        viewModelScope.launch {
            updateUiState(_uiState.value.copy(isLoading = true))
            bookRepository.fetchBook(bookJson)
                .onFailure {
                    updateUiState(_uiState.value.copy(isLoading = false, isError = true))
                }.onSuccess { book ->
                    addBook(book, author)
                }
        }
    }

    private fun addChapter(chapter: Chapter, author: String, bookId: String) {
        viewModelScope.launch {
            updateUiState(_uiState.value.copy(isLoading = true))
            val chapterWithAuthor = chapter.copy(author = author)
            bookRepository.addChapter(
                chapter = chapterWithAuthor,
                bookId = bookId
            ).onFailure {
                updateUiState(_uiState.value.copy(isLoading = false, isError = true))
            }.onSuccess {
                updateUiState(_uiState.value.copy(isLoading = false, isError = false))
            }
        }
    }

    private fun addChapterJson(chapterJson: String, author: String, bookId: String) {
        viewModelScope.launch {
            updateUiState(_uiState.value.copy(isLoading = true))
            bookRepository.fetchChapter(chapterJson)
                .onFailure {
                    updateUiState(_uiState.value.copy(isLoading = false, isError = true))
                }.onSuccess { chapter ->
                    addChapter(chapter, author, bookId)
                }
        }
    }

    private fun addPage(page: Page, author: String, chapterId: String, bookId: String) {
        viewModelScope.launch {
            updateUiState(_uiState.value.copy(isLoading = true))
            val pageWithAuthor = page.copy(author = author)

            bookRepository.addPage(
                page = pageWithAuthor,
                chapterId = chapterId,
                bookId = bookId
            ).onFailure {
                updateUiState(_uiState.value.copy(isLoading = false, isError = true))
            }.onSuccess {
                updateUiState(_uiState.value.copy(isLoading = false, isError = false))
            }
        }
    }

    private fun addPageJson(pageJson: String, author: String, chapterId: String, bookId: String) {
        viewModelScope.launch {
            updateUiState(_uiState.value.copy(isLoading = true))
            bookRepository.fetchPage(pageJson)
                .onFailure {
                    updateUiState(_uiState.value.copy(isLoading = false, isError = true))
                }.onSuccess { page ->
                    addPage(page, author, chapterId, bookId)
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
            val newPage = currentPage.copy(details = newDetails.copy(imageData = imageUrl))

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

    fun getReadingPages(bookId: String): List<Page>? {
        val book = bookRepository.getBook(bookId).getOrThrow()

        val readChapter: Chapter? = book.chapters.find { it.type == ChapterType.READ }

        return readChapter?.pages
    }

    private fun updateUiState(newState: BookUiState) {
        _uiState.update { newState }
    }
}