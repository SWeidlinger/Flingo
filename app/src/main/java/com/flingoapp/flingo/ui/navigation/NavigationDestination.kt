package com.flingoapp.flingo.ui.navigation

/**
 * Navigation destination for the app
 *
 * @property route
 * @constructor Create new NavigationDestination object
 */
sealed class NavigationDestination(var route: String) {
    data object Home : NavigationDestination("home")
    data object ChapterSelection : NavigationDestination("chapter_selection")
    data object Chapter : NavigationDestination("chapter")
}

//TODO: use once compose navigation side effect for weird pager scrolling behavior is fixed
//sealed class NavigationDestination {
//    @Serializable
//    data object Home : NavigationDestination()
//
//    @Serializable
//    data class LevelSelection(val bookIndex: Int) : NavigationDestination()
//}