package com.flingoapp.flingo.ui.navigation

import kotlinx.serialization.Serializable

/**
 * Navigation destination
 *
 * @constructor Create empty Navigation destination
 */
sealed class NavigationDestination {
    @Serializable
    data object Home : NavigationDestination()

    /**
     * Chapter selection
     *
     * @property bookIndex
     * @constructor Create empty Chapter selection
     */
    @Serializable
    data class ChapterSelection(val bookIndex: Int) : NavigationDestination()

    /**
     * Chapter
     *
     * @property chapterIndex
     * @constructor Create empty Chapter
     */
    @Serializable
    data class Chapter(val chapterIndex: Int) : NavigationDestination()
}