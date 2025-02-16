package com.flingoapp.flingo.data.repository

import com.flingoapp.flingo.data.model.Book
import com.flingoapp.flingo.data.model.Chapter
import com.flingoapp.flingo.data.model.page.Page
import kotlinx.coroutines.flow.StateFlow

interface BookRepository {
    val books: StateFlow<List<Book>>
    suspend fun fetchBook(data: String): Result<Book>
    suspend fun fetchChapter(data: String): Result<Chapter>
    suspend fun fetchPage(data: String): Result<Page>
    fun getBook(id: String): Result<Book>
    fun getChapter(chapterId: String, bookId: String): Result<Chapter>
    fun getPage(pageId: String, chapterId: String, bookId: String): Result<Page>
    suspend fun updateBook(book: Book): Result<Unit>
    suspend fun updateChapter(chapter: Chapter, bookId: String): Result<Unit>
    suspend fun updatePage(page: Page, bookId: String, chapterId: String): Result<Unit>
    suspend fun deleteBook(id: String): Result<Unit>
    suspend fun addBook(book: Book): Result<Unit>
    suspend fun addChapter(chapter: Chapter, bookId: String): Result<Unit>
    suspend fun addPage(page: Page, chapterId: String, bookId: String): Result<Unit>
}

interface BookDataSource {
    suspend fun getBook(data: String): Result<Book>
    suspend fun getChapter(data: String): Result<Chapter>
    suspend fun getPage(data: String): Result<Page>
}