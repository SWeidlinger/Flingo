package com.flingoapp.flingo.ui.navigation

/**
 * Navigation intent used to handle navigation requests
 *
 * @constructor Create empty Navigation intent
 */
sealed class NavigationIntent {
    /**
     * Navigate to home
     *
     * @property home
     * @constructor Create empty Navigate to home
     */
    data class NavigateToHome(val home: NavigationDestination.Home) : NavigationIntent()

    /**
     * Navigate to chapter selection
     *
     * @property chapterSelection
     * @property bookIndex
     * @constructor Create empty Navigate to chapter selection
     */
    data class NavigateToChapterSelection(
        val chapterSelection: NavigationDestination.ChapterSelection = NavigationDestination.ChapterSelection,
        val bookIndex: Int
    ) : NavigationIntent()

    /**
     * Navigate to chapter
     *
     * @property chapter
     * @property chapterIndex
     * @constructor Create empty Navigate to chapter
     */
    data class NavigateToChapter(
        val chapter: NavigationDestination.Chapter = NavigationDestination.Chapter,
        val chapterIndex: Int
    ) : NavigationIntent()

    /**
     * Navigate up
     *
     * @property customBackAction
     * @constructor Create empty Navigate up
     */
    data class NavigateUp(val customBackAction: () -> Unit = {}) : NavigationIntent()
}