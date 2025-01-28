package com.flingoapp.flingo.viewmodels

/**
 * Main intent used to handle any actions which are accessible and executable from the composables
 *
 * @constructor Create empty Main intent
 */
sealed class MainAction {
    sealed class UserAction : MainAction() {
        data object IncreaseLives : UserAction()
        data object DecreaseLives : UserAction()
        data class SelectInterest(val interest: String) : UserAction()
        data class FetchUser(val userJson: String) : UserAction()
    }

    sealed class BookAction : MainAction() {
        data class SelectBook(val bookIndex: Int) : BookAction()
        data class SelectChapter(val chapterIndex: Int) : BookAction()
        data object CompleteChapter : BookAction()
        data class FetchBooks(val booksJson: List<String>) : BookAction()
    }
}
