package com.flingoapp.flingo.data.repository

import PageDetails
import PageDetailsType
import com.flingoapp.flingo.data.datasource.BookDataSource
import com.flingoapp.flingo.data.model.Book
import com.flingoapp.flingo.data.model.Chapter
import com.flingoapp.flingo.data.model.page.Page
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Book repository
 *
 * @constructor Create empty Book repository
 */
interface BookRepository {
    val books: StateFlow<List<Book>>

    /**
     * Fetch book
     *
     * @param data
     * @return
     */
    suspend fun fetchBook(data: String): Result<Book>

    /**
     * Fetch chapter
     *
     * @param data
     * @return
     */
    suspend fun fetchChapter(data: String): Result<Chapter>

    /**
     * Fetch page
     *
     * @param data
     * @return
     */
    suspend fun fetchPage(data: String): Result<Page>

    /**
     * Fetch page details
     *
     * @param type
     * @param data
     * @return
     */
    suspend fun fetchPageDetails(type: PageDetailsType, data: String): Result<PageDetails>

    /**
     * Get book
     *
     * @param id
     * @return
     */
    fun getBook(id: String): Result<Book>

    /**
     * Get chapter
     *
     * @param chapterId
     * @param bookId
     * @return
     */
    fun getChapter(chapterId: String, bookId: String): Result<Chapter>

    /**
     * Get page
     *
     * @param pageId
     * @param chapterId
     * @param bookId
     * @return
     */
    fun getPage(pageId: String, chapterId: String, bookId: String): Result<Page>

    /**
     * Update book
     *
     * @param book
     * @return
     */
    suspend fun updateBook(book: Book): Result<Unit>

    /**
     * Update chapter
     *
     * @param chapter
     * @param bookId
     * @return
     */
    suspend fun updateChapter(chapter: Chapter, bookId: String): Result<Unit>

    /**
     * Update page
     *
     * @param page
     * @param bookId
     * @param chapterId
     * @return
     */
    suspend fun updatePage(page: Page, bookId: String, chapterId: String): Result<Unit>

    /**
     * Delete book
     *
     * @param id
     * @return
     */
    suspend fun deleteBook(id: String): Result<Unit>

    /**
     * Add book
     *
     * @param book
     * @return
     */
    suspend fun addBook(book: Book): Result<Unit>

    /**
     * Add chapter
     *
     * @param chapter
     * @param bookId
     * @return
     */
    suspend fun addChapter(chapter: Chapter, bookId: String): Result<Unit>

    /**
     * Add page
     *
     * @param page
     * @param chapterId
     * @param bookId
     * @return
     */
    suspend fun addPage(page: Page, chapterId: String, bookId: String): Result<Unit>
}

/**
 * Book repository impl
 *
 * @property bookDataSource
 * @constructor Create empty Book repository impl
 */
class BookRepositoryImpl(
    private val bookDataSource: BookDataSource
) : BookRepository {
    companion object {
        private const val TAG = "BookRepositoryImpl"
    }

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    override val books = _books.asStateFlow()

    override suspend fun fetchBook(data: String): Result<Book> {
        return try {
            val book = bookDataSource.parseBook(data).getOrThrow()
            Result.success(book)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchChapter(data: String): Result<Chapter> {
        return try {
            val chapter = bookDataSource.parseChapter(data).getOrThrow()
            Result.success(chapter)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //TODO: rework this does not need to have a fetchPage here as well
    override suspend fun fetchPage(data: String): Result<Page> {
        return try {
            val page = bookDataSource.parsePage(data).getOrThrow()
            Result.success(page)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchPageDetails(type: PageDetailsType, data: String): Result<PageDetails> {
        return try {
            val pageDetails = bookDataSource.parsePageDetails(type, data).getOrThrow()
            Result.success(pageDetails)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getBook(id: String): Result<Book> {
        return _books.value.find { it.id == id }?.let {
            Result.success(it)
        } ?: Result.failure(Exception("Book not found"))
    }

    override suspend fun updateBook(book: Book): Result<Unit> {
        return try {
            val newBooks = _books.value.toMutableList()
            val index = newBooks.indexOfFirst { it.id == book.id }
            newBooks[index] = book
            _books.update { newBooks }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateChapter(chapter: Chapter, bookId: String): Result<Unit> {
        return try {
            val book = getBook(bookId).getOrThrow()
            val newChapters = book.chapters.toMutableList()
            val index = newChapters.indexOfFirst { it.id == chapter.id }
            newChapters[index] = chapter
            val newBook = book.copy(chapters = newChapters)
            updateBook(newBook)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePage(page: Page, bookId: String, chapterId: String): Result<Unit> {
        return try {
            val chapter = getChapter(chapterId, bookId).getOrThrow()
            val newPages = chapter.pages?.toMutableList()
            val index = newPages?.indexOfFirst { it.id == page.id } ?: return Result.failure(
                Exception("Page not found")
            )
            newPages[index] = page
            val newChapter = chapter.copy(pages = newPages)
            updateChapter(newChapter, bookId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteBook(id: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun addBook(book: Book): Result<Unit> {
        return try {
            _books.update { currentBooks ->
                currentBooks + book
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getChapter(chapterId: String, bookId: String): Result<Chapter> {
        val book = getBook(bookId).getOrThrow()

        return book.chapters.find { it.id == chapterId }?.let {
            Result.success(it)
        } ?: Result.failure(Exception("Chapter not found"))
    }

    override suspend fun addChapter(chapter: Chapter, bookId: String): Result<Unit> {
        return try {
            val book = getBook(bookId).getOrThrow()
            val newChapters = book.chapters.toMutableList()
            newChapters.add(chapter)
            val newBook = book.copy(chapters = newChapters)
            updateBook(newBook)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getPage(pageId: String, chapterId: String, bookId: String): Result<Page> {
        val chapter = getChapter(chapterId, bookId).getOrThrow()

        return chapter.pages?.find { it.id == pageId }?.let {
            Result.success(it)
        } ?: Result.failure(Exception("Page not found"))
    }

    override suspend fun addPage(page: Page, chapterId: String, bookId: String): Result<Unit> {
        return try {
            val chapter = getChapter(chapterId, bookId).getOrThrow()

            val newPages = chapter.pages?.toMutableList()
            newPages?.add(page)
            val newChapter = chapter.copy(pages = newPages)
            updateChapter(newChapter, bookId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}