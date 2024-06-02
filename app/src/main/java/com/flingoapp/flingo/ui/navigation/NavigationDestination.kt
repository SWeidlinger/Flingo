package com.flingoapp.flingo.ui.navigation

sealed class NavigationDestination(var route: String) {
    data object Home : NavigationDestination("home")
    data object LevelSelection : NavigationDestination("level_selection")
}

//TODO: use once compose navigation side effect for weird pager scrolling behavior is fixed
//sealed class NavigationDestination {
//    @Serializable
//    data object Home : NavigationDestination()
//
//    @Serializable
//    data class LevelSelection(val bookIndex: Int) : NavigationDestination()
//}