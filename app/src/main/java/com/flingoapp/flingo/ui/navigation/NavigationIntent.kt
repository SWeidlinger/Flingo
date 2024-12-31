package com.flingoapp.flingo.ui.navigation

/**
 * Navigation intent used to handle navigation requests
 *
 * @constructor Create empty Navigation intent
 */
sealed class NavigationIntent {
    /**
     * Navigate to screen
     *
     * @property destination
     * @constructor Create empty Navigate to home
     */
    data class Screen(val destination: NavigationDestination) : NavigationIntent()

    /**
     * Navigate up
     *
     * @property customBackAction
     * @constructor Create empty Navigate up
     */
    data class Up(val customBackAction: () -> Unit = {}) : NavigationIntent()
}