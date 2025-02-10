package com.flingoapp.flingo.viewmodel

import com.flingoapp.flingo.data.network.GenAiModel

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
        data class SelectBook(val bookIndex: Int) : BookAction
        data class SelectChapter(val chapterIndex: Int) : BookAction
        data object CompleteChapter : BookAction
        data class CompletePage(val pageIndex: Int) : BookAction
        data class FetchBooks(val booksJson: List<String>) : BookAction
        data class AddBook(val bookJson: String, val author: String) : BookAction
        data class AddChapter(val chapterJson: String, val author: String) : BookAction
    }

    sealed interface PersonalizationAction: MainAction{
        data object GenerateBook : PersonalizationAction
        data object GenerateChapter : PersonalizationAction
        data class ChangeModel(val model: GenAiModel) : PersonalizationAction
        data object ToggleDebugMode: PersonalizationAction
    }
}
