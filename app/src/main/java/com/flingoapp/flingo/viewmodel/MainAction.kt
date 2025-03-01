package com.flingoapp.flingo.viewmodel

import PageDetailsType
import com.flingoapp.flingo.data.model.Book
import com.flingoapp.flingo.data.model.Chapter
import com.flingoapp.flingo.data.model.genAi.GenAiModelPerformance
import com.flingoapp.flingo.data.model.genAi.GenAiProvider
import com.flingoapp.flingo.data.model.page.Page

/**
 * Main intent used to handle any actions which are accessible and executable from the composables
 *
 * @constructor Create empty Main intent
 */
sealed interface MainAction {
    sealed interface UserAction : MainAction {
        data object IncreaseLives : UserAction
        data object DecreaseLives : UserAction
        data class SelectInterest(val interest: String) : UserAction
        data class FetchUser(val userJson: String) : UserAction
    }

    sealed interface BookAction : MainAction {
        data class SelectBook(val bookId: String) : BookAction
        data class SelectChapter(val chapterId: String) : BookAction
        data class SelectPage(val pageIndex: Int) : BookAction
        data object CompleteChapter : BookAction
        data class CompletePage(val pageIndex: Int) : BookAction
        data class FetchBooks(val booksJson: List<String>) : BookAction
        data class AddBookJson(val bookJson: String, val author: String) : BookAction
        data class AddBook(val book: Book, val author: String) : BookAction
        data class AddChapterJson(val chapterJson: String, val author: String, val bookId: String) : BookAction
        data class AddChapter(val chapter: Chapter, val author: String, val bookId: String) : BookAction
        data class AddPageJson(val pageJson: String, val author: String, val chapterId: String, val bookId: String) : BookAction
        data class AddPage(val page: Page, val author: String, val chapterId: String, val bookId: String) : BookAction
        data class AddImage(val imageUrl: String, val author: String) : BookAction
    }

    sealed interface PersonalizationAction : MainAction {
        data class GenerateBookFromText(val scannedText: String) : PersonalizationAction
        data class GenerateChapter(val sourceChapter: Chapter) : PersonalizationAction
        data class GenerateChapterFromText(val pageDetailsType: PageDetailsType) : PersonalizationAction
        data class GeneratePage(val sourceChapter: Chapter, val sourceBook: Book) : PersonalizationAction
        data class ChangeModel(val model: GenAiProvider) : PersonalizationAction
        data class ChangeModelPerformance(val modelPerformance: GenAiModelPerformance) : PersonalizationAction
        data object ToggleDebugMode : PersonalizationAction
        data object ToggleGenerateImages : PersonalizationAction
        data class GenerateImage(val context: String) : PersonalizationAction
        data class GenerateBook(val book: Book) : PersonalizationAction
    }
}
