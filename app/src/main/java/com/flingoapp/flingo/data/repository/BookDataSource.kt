package com.flingoapp.flingo.data.repository

import android.content.Context
import android.util.Log
import com.flingoapp.flingo.data.model.Book
import com.flingoapp.flingo.data.model.Chapter
import com.flingoapp.flingo.data.model.page.Page
import kotlinx.serialization.json.Json

interface BookDataSource {
    suspend fun getBook(data: String): Result<Book>
    suspend fun getChapter(data: String): Result<Chapter>
    suspend fun getPage(data: String): Result<Page>
}

class BookDataSourceJsonImpl(
    val context: Context
) : BookDataSource {
    companion object {
        private const val TAG = "BookDataSourceJsonImpl"
    }

    override suspend fun getBook(data: String): Result<Book> {
        return try {
            val book = getJsonDeserializer().decodeFromString<Book>(data)
            Result.success(book)
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getChapter(data: String): Result<Chapter> {
        return try {
            val chapter = getJsonDeserializer().decodeFromString<Chapter>(data)
            Result.success(chapter)
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getPage(data: String): Result<Page> {
        return try {
            val page = getJsonDeserializer().decodeFromString<Page>(data)
            Result.success(page)
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            Result.failure(e)
        }
    }

    private fun getJsonDeserializer(): Json {
        val jsonDeserializer = Json { ignoreUnknownKeys = true }
        return jsonDeserializer
    }
}