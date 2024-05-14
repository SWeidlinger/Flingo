package com.flingoapp.flingo.ui.navigation

sealed class NavigationDestination(var route: String) {
    data object Home : NavigationDestination("home")
}