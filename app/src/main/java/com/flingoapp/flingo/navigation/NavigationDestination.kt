package com.flingoapp.flingo.navigation

import kotlinx.serialization.Serializable

/**
 * Navigation destination
 *
 * @constructor Create empty Navigation destination
 */
sealed interface NavigationDestination {
    @Serializable
    data object Home : NavigationDestination

    /**
     * Chapter selection
     *
     * @property bookId
     * @constructor Create empty Chapter selection
     */
    @Serializable
    data class ChapterSelection(val bookId: String) : NavigationDestination

    /**
     * Chapter
     *
     * @property chapterId
     * @constructor Create empty Chapter
     */
    @Serializable
    data class Chapter(val chapterId: String) : NavigationDestination

    /**
     * Challenge finished screen
     *
     * @constructor Create empty ChallengeFinished
     */
    @Serializable
    data object ChallengeFinished : NavigationDestination

    /**
     * Interest selection screen
     *
     * @constructor Create empty InterestSelection
     */
    @Serializable
    data object User : NavigationDestination

    @Serializable
    data object StreakAndStars : NavigationDestination

    @Serializable
    data object Settings : NavigationDestination
}