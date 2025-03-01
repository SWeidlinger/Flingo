package com.flingoapp.flingo.data.datasource

import PageDetails
import PageDetailsType
import android.content.Context
import android.util.Log
import com.flingoapp.flingo.data.model.Book
import com.flingoapp.flingo.data.model.Chapter
import com.flingoapp.flingo.data.model.page.Page
import kotlinx.serialization.json.Json

interface BookDataSource {
    suspend fun parseBook(data: String): Result<Book>
    suspend fun parseChapter(data: String): Result<Chapter>
    suspend fun parsePage(data: String): Result<Page>
    suspend fun parsePageDetails(type: PageDetailsType, data: String): Result<PageDetails>
}

class BookDataSourceJsonImpl(
    val context: Context
) : BookDataSource {
    companion object {
        private const val TAG = "BookDataSourceJsonImpl"
    }

    override suspend fun parseBook(data: String): Result<Book> {
        return try {
            val book = getJsonDeserializer().decodeFromString<Book>(data)
            Result.success(book)
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun parseChapter(data: String): Result<Chapter> {
        return try {
            val chapter = getJsonDeserializer().decodeFromString<Chapter>(data)
            Result.success(chapter)
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun parsePage(data: String): Result<Page> {
        return try {
            //TODO: remove special cases
            val page = if (data.startsWith("[")) {
                //special handling since GEMINI decides to return a list instead of a object
                val pages = getJsonDeserializer().decodeFromString<List<Page>>(data)
                pages.first()
            } else if (data.contains("chapterTitle")) {
                //special handling for GEMINI again...
                val chapter = getJsonDeserializer().decodeFromString<Chapter>(data)
                chapter.pages?.first()
            } else {
                getJsonDeserializer().decodeFromString<Page>(data)
            }

            if (page == null) {
                throw Exception("Page is null - Gemini is annoying")
            }
            Result.success(page)
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun parsePageDetails(type: PageDetailsType, data: String): Result<PageDetails> {
        return try {
            val pageDetails = when (type) {
                PageDetailsType.READ -> getJsonDeserializer().decodeFromString<PageDetails.Read>(data)
                PageDetailsType.REMOVE_WORD -> getJsonDeserializer().decodeFromString<PageDetails.RemoveWord>(data)
                PageDetailsType.QUIZ -> getJsonDeserializer().decodeFromString<PageDetails.Quiz>(data)
                PageDetailsType.ORDER_STORY -> getJsonDeserializer().decodeFromString<PageDetails.OrderStory>(data)
            }

            Result.success(pageDetails)
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