package com.flingoapp.flingo.ui.navigation

sealed class NavigationIntent {
    data class NavigateToHome(val home: NavigationDestination.Home) : NavigationIntent()
    data class NavigateToChapterSelection(
        val chapterSelection: NavigationDestination.ChapterSelection,
        val bookIndex: Int
    ) : NavigationIntent()

    data class NavigateUp(val customBackAction: () -> Unit = {}) : NavigationIntent()
}