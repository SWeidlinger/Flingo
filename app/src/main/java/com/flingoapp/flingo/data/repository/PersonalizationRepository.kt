package com.flingoapp.flingo.data.repository

import android.util.Log
import com.flingoapp.flingo.data.datasource.PersonalizationDataSource
import com.flingoapp.flingo.data.model.Book
import com.flingoapp.flingo.data.model.Chapter
import com.flingoapp.flingo.data.model.page.Page
import com.flingoapp.flingo.di.GenAiModule
import com.flingoapp.flingo.viewmodel.PersonalizationAspects
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface PersonalizationRepository {
    val usedPrompts: StateFlow<List<String>>
    val generatedResults: StateFlow<List<String>>
    suspend fun generateBook(personalizationAspects: PersonalizationAspects): Result<Book>
    suspend fun generateChapter(
        personalizationAspects: PersonalizationAspects,
        sourceBook: Book
    ): Result<Chapter>

    suspend fun generatePage(
        personalizationAspects: PersonalizationAspects,
        sourceChapter: Chapter
    ): Result<Page>

    suspend fun generateImage(): Result<Any>
}

class PersonalizationRepositoryImpl(
    private val genAiModule: GenAiModule,
    private val personalizationDataSource: PersonalizationDataSource,
    private val bookRepository: BookRepository
) : PersonalizationRepository {
    companion object {
        const val TAG = "PersonalizationRepositoryImpl"
    }

    private val _usedPrompts = MutableStateFlow<List<String>>(emptyList())
    override val usedPrompts = _usedPrompts.asStateFlow()

    private val _generatedResults = MutableStateFlow<List<String>>(emptyList())
    override val generatedResults = _generatedResults.asStateFlow()

    override suspend fun generateBook(personalizationAspects: PersonalizationAspects): Result<Book> {
        val prompt = personalizationDataSource.getPersonalizedBookPrompt(personalizationAspects)
        _usedPrompts.update {
            it + prompt
        }

        val response = genAiModule.repository.getTextResponse(prompt).getOrThrow()

        return try {
            val book = bookRepository.fetchBook(response).getOrThrow()
            Result.success(book)
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun generateChapter(
        personalizationAspects: PersonalizationAspects,
        sourceBook: Book
    ): Result<Chapter> {
        val prompt = personalizationDataSource.getPersonalizedChapterPrompt(
            personalizationAspects,
            sourceBook
        )
        _usedPrompts.update {
            it + prompt
        }

        val response = genAiModule.repository.getTextResponse(prompt).getOrThrow()

        return try {
            val chapter = bookRepository.fetchChapter(response).getOrThrow()
            Result.success(chapter)
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun generatePage(
        personalizationAspects: PersonalizationAspects,
        sourceChapter: Chapter
    ): Result<Page> {
        val prompt = personalizationDataSource.getPersonalizedPagePrompt(
            personalizationAspects,
            sourceChapter
        )
        _usedPrompts.update {
            it + prompt
        }

        val response = genAiModule.repository.getTextResponse(prompt).getOrThrow()

        return try {
            val page = bookRepository.fetchPage(response).getOrThrow()
            Result.success(page)
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun generateImage(): Result<Any> {
        return Result.failure(Exception("Not yet implemented"))
    }
}