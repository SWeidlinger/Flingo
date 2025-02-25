package com.flingoapp.flingo.data.repository

import PageDetails
import android.util.Log
import com.flingoapp.flingo.data.model.Book
import com.flingoapp.flingo.data.model.Chapter
import com.flingoapp.flingo.data.model.ChapterType
import com.flingoapp.flingo.data.model.genAi.GenAiResponse
import com.flingoapp.flingo.data.model.page.Page
import com.flingoapp.flingo.di.GenAiModule
import com.flingoapp.flingo.viewmodel.PersonalizationAspects
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json

interface PersonalizationRepository {
    val usedPrompts: StateFlow<List<String>>
    val generatedResults: StateFlow<List<String>>
    suspend fun generateBook(
        scannedText: String,
        generateImages: Boolean,
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
        generateImages: Boolean,
        personalizationAspects: PersonalizationAspects
    ): Result<Book> {
        return try {
            val jsonDeserializer = Json { ignoreUnknownKeys = true }
            val cleanScannedText =
                scannedText.replace("\n", " ").replace("\r", " ").replace("\t", " ")

            //split text into parts
            val splitTextRequest =
                genAiModule.basePrompts.splitTextRequest(content = cleanScannedText)
            _usedPrompts.update {
                it + splitTextRequest.toString()
            }

            val splitText = genAiModule.repository.getTextResponse(
                model = genAiModule.currentModel.value.smallTextModel,
                request = splitTextRequest
            ).getOrThrow()
            val originalSplitText =
                jsonDeserializer.decodeFromString<GenAiResponse.SplitTextResponse>(splitText)

            //adapt parts to user preferences
            val personalizeTextPartsRequest = genAiModule.basePrompts.personalizeTextParts(
                content = splitText,
                personalizationAspects = personalizationAspects
            )
            _usedPrompts.update {
                it + personalizeTextPartsRequest.toString()
            }

            val personalizedTextParts = genAiModule.repository.getTextResponse(
                model = genAiModule.currentModel.value.textModel,
                request = personalizeTextPartsRequest
            ).getOrThrow()
            val personalizedSplitText =
                jsonDeserializer.decodeFromString<GenAiResponse.SplitTextResponse>(
                    personalizedTextParts
                )

            //generate image prompts for parts
            val imageGenerationPromptRequest = genAiModule.basePrompts.imageGenerationPromptForText(
                content = personalizedTextParts
            )
            _usedPrompts.update {
                it + imageGenerationPromptRequest.toString()
            }

            val imagePromptResponse = genAiModule.repository.getTextResponse(
                model = genAiModule.currentModel.value.textModel,
                request = imageGenerationPromptRequest
            ).getOrThrow()

            val imagePrompts =
                jsonDeserializer.decodeFromString<GenAiResponse.ImagePromptsResponse>(
                    imagePromptResponse
                )

            //parallel image generation
            //TODO: improve logic for this
            val generatedImageUrls = if (generateImages) {
                fetchImageUrlsParallel(imagePrompts.prompts)
            } else {
                List(originalSplitText.content.size) { "" }
            }

            //generate bookJSON with reading part
            val readPages = mutableListOf<Page>()
            repeat(originalSplitText.content.size) { index ->
                val page = Page(
                    description = "",
                    difficulty = "easy",
                    hint = "hint",
                    timeLimit = 0,
                    score = 0,
                    feedback = null,
                    taskDefinition = "",
                    details = PageDetails.Read(
                        content = personalizedSplitText.content[index].trim(),
                        originalContent = originalSplitText.content[index].trim(),
                        imageUrl = generatedImageUrls[index]
                    )
                )

                readPages.add(page)
            }

            val readChapter = Chapter(
                author = genAiModule.currentModel.value.provider,
                title = personalizedSplitText.title,
                type = ChapterType.READ,
                description = "",
                coverImage = "",
                isCompleted = false,
                pages = readPages
            )

            //generate challenge chapters for book
            //TODO

            val finalBook = Book(
                author = genAiModule.currentModel.value.provider,
                title = personalizedSplitText.title,
                description = "",
                coverImage = "",
                chapters = listOf(readChapter)
            )
            Result.success(finalBook)

        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun generateChapter(
        personalizationAspects: PersonalizationAspects,
        sourceBook: Book
    ): Result<Chapter> {
        //TODO
        val request = genAiModule.basePrompts.chapterRequest(
            content = sourceBook.toString(),
            personalizationAspects = personalizationAspects
        )
        _usedPrompts.update {
            it + request.toString()
        }

        val response = genAiModule.repository.getTextResponse(
            model = genAiModule.currentModel.value.textModel,
            request = request
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
        //TODO
        val request = genAiModule.basePrompts.pageRequest(
            content = sourceChapter.toString(),
            personalizationAspects = personalizationAspects
        )
        _usedPrompts.update {
            it + request.toString()
        }

        val response = genAiModule.repository.getTextResponse(
            model = genAiModule.currentModel.value.textModel,
            request = request
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

    private suspend fun fetchImageUrlsParallel(imagePrompts: List<String>): List<String> =
        coroutineScope {
            imagePrompts.map { prompt ->
                async(Dispatchers.IO) {
                    val imageGenerationRequest =
                        genAiModule.basePrompts.imageRequest(prompt = prompt)
                    _usedPrompts.update { it + imageGenerationRequest.toString() }

                    // Call the suspend function concurrently
                    genAiModule.repository.getImageResponse(
                        model = genAiModule.currentModel.value.imageModel,
                        request = imageGenerationRequest
                    ).getOrThrow()
                }
            }.awaitAll()
        }
}