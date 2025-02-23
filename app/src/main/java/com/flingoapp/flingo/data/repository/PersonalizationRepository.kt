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
    suspend fun generateBook(
        scannedText: String,
        personalizationAspects: PersonalizationAspects
    ): Result<Book>

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
//    private val personalizationDataSource: PersonalizationDataSource,
    private val bookRepository: BookRepository
) : PersonalizationRepository {
    companion object {
        const val TAG = "PersonalizationRepositoryImpl"
    }

    private val _usedPrompts = MutableStateFlow<List<String>>(emptyList())
    override val usedPrompts = _usedPrompts.asStateFlow()

    private val _generatedResults = MutableStateFlow<List<String>>(emptyList())
    override val generatedResults = _generatedResults.asStateFlow()

    override suspend fun generateBook(
        scannedText: String,
        personalizationAspects: PersonalizationAspects
    ): Result<Book> {
        //split text into parts
        val splitTextRequest = genAiModule.basePrompts.splitTextRequest(
            content = scannedText,
            personalizationAspects = null
        )

        _usedPrompts.update {
            it + splitTextRequest.toString()
        }

        val splitText = genAiModule.repository.getTextResponse(
            model = genAiModule.currentModel.value.smallTextModel,
            request = splitTextRequest
        ).getOrThrow()

        //adapt parts to user preferences


        //generate image prompts for parts

        //generate bookJSON with reading part

        //generate challenge chapters for book

        val finalBook = Book(
            author = TODO(),
            title = TODO(),
            description = TODO(),
            coverImage = TODO(),
            chapters = TODO()
        )

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
            genAiModule.basePrompts,
            personalizationAspects,
            sourceBook
        )
        _usedPrompts.update {
            it + prompt
        }

        val response = genAiModule.repository.getTextResponse(
            model = genAiModule.currentModel.value.textModel,
            prompt = prompt
        ).getOrThrow()

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
            genAiModule.basePrompts,
            personalizationAspects,
            sourceChapter
        )
        _usedPrompts.update {
            it + prompt
        }

        val response = genAiModule.repository.getTextResponse(
            prompt = prompt,
            model = genAiModule.currentModel.value.textModel
        ).getOrThrow()

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