package com.flingoapp.flingo.viewmodel

import com.flingoapp.flingo.data.model.genAi.GenAiModel

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
        data class AddBook(val bookJson: String, val author: String) : BookAction
        data class AddChapter(val chapterJson: String, val author: String) : BookAction
        data class AddPage(val pageJson: String, val author: String) : BookAction
        data class AddImage(val imageUrl: String, val author: String) : BookAction
    }

    sealed interface PersonalizationAction: MainAction{
        data class GenerateBook(val scannedText: String) : PersonalizationAction
        data object GenerateChapter : PersonalizationAction
        data object GeneratePage : PersonalizationAction
        data class ChangeModel(val model: GenAiModel) : PersonalizationAction
        data object ToggleDebugMode: PersonalizationAction
        data class GenerateImage(val context: String): PersonalizationAction
    }
}
