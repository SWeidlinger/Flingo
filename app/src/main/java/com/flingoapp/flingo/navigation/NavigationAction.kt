package com.flingoapp.flingo.navigation

/**
 * Navigation intent used to handle navigation requests
 *
 * @constructor Create empty Navigation intent
 */
sealed interface NavigationAction {
    /**
     * Navigate to screen
     *
     * @property destination
     * @constructor Create empty Navigate to home
     */
    data class Screen(val destination: NavigationDestination) : NavigationAction

    /**
     * Navigate up
     *
     * @property customBackAction
     * @constructor Create empty Navigate up
     */
    data class Up(val customBackAction: () -> Unit = {}) : NavigationAction
}