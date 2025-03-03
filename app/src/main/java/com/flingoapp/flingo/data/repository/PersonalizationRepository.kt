package com.flingoapp.flingo.data.repository

import PageDetails
import PageDetailsType
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
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface PersonalizationRepository {
    val usedPrompts: StateFlow<List<String>>
    val generatedResults: StateFlow<List<String>>
    suspend fun generateBookFromText(
        scannedText: String,
        generateImages: Boolean,
        personalizationAspects: PersonalizationAspects
    ): Result<Pair<String, Book>>

    suspend fun generateBook(
        sourceBook: Book,
        generateImages: Boolean,
        personalizationAspects: PersonalizationAspects
    ): Result<Book>

    suspend fun generateChapterFromText(
        personalizedText: String,
        type: PageDetailsType,
        quizType: PageDetails.Quiz.QuizType? = null,
        pageAmount: Int,
    ): Result<Chapter>

    suspend fun generateChapter(
        personalizationAspects: PersonalizationAspects,
        sourceChapter: Chapter
    ): Result<Chapter>

    suspend fun generatePagesFromText(
        personalizedText: String,
        type: PageDetailsType,
        quizType: PageDetails.Quiz.QuizType? = null,
        pageAmount: Int,
    ): Result<Pair<String, List<Page>>>

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

    override suspend fun generateBookFromText(
        scannedText: String,
        generateImages: Boolean,
        personalizationAspects: PersonalizationAspects
    ): Result<Pair<String, Book>> {
        return try {
            val readPageDetailsResult = buildReadingPageDetails(
                scannedText = scannedText,
                personalizationAspects = personalizationAspects,
                generateImages = generateImages
            )
            val bookTitle = readPageDetailsResult.first
            val readPageDetails = readPageDetailsResult.second

            //create Book with generated content
            //TODO: think of difficulty adaption
            val readPages = mutableListOf<Page>()
            repeat(readPageDetails.size) { index ->
                val page = Page(
                    description = "",
                    difficulty = "easy",
                    hint = "hint",
                    timeLimit = 0,
                    score = 0,
                    feedback = null,
                    taskDefinition = "",
                    details = PageDetails.Read(
                        content = readPageDetails[index].content,
                        originalContent = readPageDetails[index].originalContent,
                        imageData = readPageDetails[index].imageData,
                        isFromVertexAi = genAiModule.currentModelProvider.value.provider == "Google"
                    )
                )

                readPages.add(page)
            }

            val readChapter = Chapter(
                author = genAiModule.currentModelProvider.value.provider,
                title = bookTitle,
                type = ChapterType.READ,
                description = "",
                coverImage = "",
                isCompleted = false,
                pages = readPages
            )

            val finalBook = Book(
                author = genAiModule.currentModelProvider.value.provider,
                title = bookTitle,
                description = "",
                coverImage = "",
                chapters = listOf(readChapter)
            )

            val personalizedText = readPageDetails.joinToString("\n") { it.content }
            Result.success(Pair(personalizedText, finalBook))
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            Result.failure(e)
        }
    }

    private suspend fun buildReadingPageDetails(
        scannedText: String,
        personalizationAspects: PersonalizationAspects,
        generateImages: Boolean
    ): Pair<String, List<PageDetails.Read>> {
        val jsonDeserializer = Json { ignoreUnknownKeys = true }
        val cleanScannedText = scannedText
            .replace("\n", " ")
            .replace("\r", " ")
            .replace("\t", " ")

        //split text into adequate parts
        val splitTextRequest =
            genAiModule.basePrompts.splitTextRequest(content = cleanScannedText)
        _usedPrompts.update {
            it + splitTextRequest.toString()
        }

        val originalSplitTextResponse = genAiModule.repository.getTextResponse(
            model = genAiModule.currentTextModel,
            request = splitTextRequest
        ).getOrThrow()

        //adapt split text parts to user preferences
        val personalizeTextPartsRequest = genAiModule.basePrompts.personalizeTextParts(
            content = originalSplitTextResponse,
            personalizationAspects = personalizationAspects
        )
        _usedPrompts.update {
            it + personalizeTextPartsRequest.toString()
        }

        val personalizedTextPartsResponse = genAiModule.repository.getTextResponse(
            model = genAiModule.currentTextModel,
            request = personalizeTextPartsRequest
        ).getOrThrow()

        //generate image prompts for parts
        val imageGenerationPromptRequest = genAiModule.basePrompts.imageGenerationPromptForText(
            content = personalizedTextPartsResponse,
            personalizationAspects = personalizationAspects
        )
        _usedPrompts.update {
            it + imageGenerationPromptRequest.toString()
        }

        val imagePromptResponse = genAiModule.repository.getTextResponse(
            model = genAiModule.currentTextModel,
            request = imageGenerationPromptRequest
        ).getOrThrow()

        val originalSplitText = jsonDeserializer.decodeFromString<GenAiResponse.SplitTextResponse>(originalSplitTextResponse)
        val personalizedSplitText =
            jsonDeserializer.decodeFromString<GenAiResponse.SplitTextResponse>(personalizedTextPartsResponse)
        val imagePrompts =
            jsonDeserializer.decodeFromString<GenAiResponse.ImagePromptsResponse>(
                imagePromptResponse
            )

        //parallel image generation
        val generatedImageUrls = if (generateImages) {
            fetchImageUrlsParallel(imagePrompts.prompts)
        } else {
            null
        }

        val minSize = listOf(
            originalSplitText.content.size,
            personalizedSplitText.content.size,
            imagePrompts.prompts.size
        ).minOrNull() ?: 0

        val pageDetailsList = mutableListOf<PageDetails.Read>()
        for (index in 0 until minSize) {
            val pageDetails = PageDetails.Read(
                content = personalizedSplitText.content[index].trim(),
                originalContent = originalSplitText.content[index].trim(),
                imagePrompt = imagePrompts.prompts[index],
                imageData = if (generatedImageUrls == null || index >= generatedImageUrls.size) ""
                else generatedImageUrls[index]
            )
            pageDetailsList.add(pageDetails)
        }

        return Pair(personalizedSplitText.title, pageDetailsList)
    }

    //TODO: refactor!!
    override suspend fun generatePagesFromText(
        personalizedText: String,
        type: PageDetailsType,
        quizType: PageDetails.Quiz.QuizType?,
        pageAmount: Int
    ): Result<Pair<String, List<Page>>> {
        return try {
            val requestPageDetails = genAiModule.basePrompts.pageDetailsRequest(
                content = personalizedText,
                requestPageAmount = pageAmount,
                type = type,
                quizType = quizType
            )
            _usedPrompts.update {
                it + requestPageDetails.toString()
            }

            val response = genAiModule.repository.getTextResponse(
                model = genAiModule.currentTextModel,
                request = requestPageDetails
            ).getOrThrow()

            var taskDefinition = ""
            var chapterTitle = ""
            val jsonDeserializer = Json { ignoreUnknownKeys = true }
            val generatedPageDetails: List<PageDetails> = when (type) {
                PageDetailsType.REMOVE_WORD -> {
                    val generatedPageDetails =
                        jsonDeserializer.decodeFromString<GenAiResponse.PageDetailsRemoveWordResponse>(response)

                    chapterTitle = generatedPageDetails.chapterTitle
                    taskDefinition = generatedPageDetails.taskDefinition

                    val sentences = mutableListOf<PageDetails.RemoveWord>()
                    generatedPageDetails.sentences.forEach { sentence ->
                        val removeWordPageDetail = PageDetails.RemoveWord(
                            content = sentence.sentence,
                            answer = sentence.answer
                        )
                        sentences.add(removeWordPageDetail)
                    }

                    sentences
                }

                PageDetailsType.ORDER_STORY -> {
                    val generatedPageDetails =
                        jsonDeserializer.decodeFromString<GenAiResponse.PageDetailsOrderStoryResponse>(response)

                    chapterTitle = generatedPageDetails.chapterTitle
                    taskDefinition = generatedPageDetails.taskDefinition

                    val tasks = mutableListOf<PageDetails.OrderStory>()
                    generatedPageDetails.content.forEach { task ->
                        val orderStoryPageDetail = PageDetails.OrderStory(
                            content = task.snippets.map { snippet ->
                                PageDetails.OrderStory.Content(
                                    id = snippet.id,
                                    text = snippet.text
                                )
                            },
                            correctOrder = task.correctOrder,
                        )
                        tasks.add(orderStoryPageDetail)
                    }

                    tasks
                }

                PageDetailsType.QUIZ -> {
                    if (quizType == null) throw IllegalArgumentException("QuizType must be provided for quiz page details")

                    val generatedPageDetails =
                        jsonDeserializer.decodeFromString<GenAiResponse.PageDetailsQuizResponse>(response)

                    chapterTitle = generatedPageDetails.chapterTitle
                    taskDefinition = generatedPageDetails.taskDefinition

                    val questions = mutableListOf<PageDetails.Quiz>()
                    generatedPageDetails.questions.forEach { question ->
                        val quizPageDetail = PageDetails.Quiz(
                            quizType = quizType,
                            question = question.question,
                            answers = question.answers.map { answer ->
                                PageDetails.Quiz.Answer(
                                    answer = answer.answer,
                                    isCorrect = answer.isCorrect
                                )
                            }
                        )
                        questions.add(quizPageDetail)
                    }

                    questions
                }

                PageDetailsType.READ -> throw IllegalArgumentException("Read Type is not supported!")
            }

            val generatedPages = mutableListOf<Page>()
            generatedPageDetails.forEach {
                val page = Page(
                    author = genAiModule.currentModelProvider.value.provider,
                    description = "",
                    difficulty = "easy",
                    hint = "",
                    timeLimit = 0,
                    score = 0,
                    feedback = null,
                    taskDefinition = taskDefinition,
                    details = it,
                )
                generatedPages.add(page)
            }

            Result.success(Pair(chapterTitle, generatedPages))
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun generateBook(
        sourceBook: Book,
        generateImages: Boolean,
        personalizationAspects: PersonalizationAspects
    ): Result<Book> {
        val sourceBookJson = Json.encodeToString<Book>(sourceBook)
        val request = genAiModule.basePrompts.bookRequest(
            content = sourceBookJson,
            personalizationAspects = personalizationAspects
        )
        _usedPrompts.update {
            it + request.toString()
        }

        val response = genAiModule.repository.getTextResponse(
            model = genAiModule.currentTextModel,
            request = request
        ).getOrThrow()

        Log.e(TAG, "Response from ${genAiModule.currentModelProvider.value.provider}: \n $response")

        return try {
            val book = bookRepository.fetchBook(response).getOrThrow()
            Result.success(book)
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun generateChapterFromText(
        personalizedText: String,
        type: PageDetailsType,
        quizType: PageDetails.Quiz.QuizType?,
        pageAmount: Int
    ): Result<Chapter> {
        return try {
            val pagesResult = generatePagesFromText(
                personalizedText = personalizedText,
                type = type,
                quizType = quizType,
                pageAmount = pageAmount
            ).getOrThrow()

            val title = pagesResult.first
            val pages = pagesResult.second

            val generatedChapter = Chapter(
                author = genAiModule.currentModelProvider.value.provider,
                title = title,
                type = ChapterType.CHALLENGE,
                description = "",
                coverImage = "",
                pages = pages
            )

            return Result.success(generatedChapter)
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun generateChapter(
        personalizationAspects: PersonalizationAspects,
        sourceChapter: Chapter,
    ): Result<Chapter> {
        val sourceChapterJson = Json.encodeToString<Chapter>(sourceChapter)

        val request = genAiModule.basePrompts.chapterRequest(
            content = sourceChapterJson,
            personalizationAspects = personalizationAspects
        )
        _usedPrompts.update {
            it + request.toString()
        }

        val response = genAiModule.repository.getTextResponse(
            model = genAiModule.currentTextModel,
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
        val sourceChapterJson = Json.encodeToString<Chapter>(sourceChapter)

        val request = genAiModule.basePrompts.pageRequest(
            content = sourceChapterJson,
            personalizationAspects = personalizationAspects
        )
        _usedPrompts.update {
            it + request.toString()
        }

        val response = genAiModule.repository.getTextResponse(
            model = genAiModule.currentTextModel,
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
                        model = genAiModule.currentImageModel,
                        request = imageGenerationRequest
                    ).getOrThrow()
                }
            }.awaitAll()
        }
}