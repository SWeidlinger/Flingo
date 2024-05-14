package com.flingoapp.flingo.data.viewmodels.main

import com.flingoapp.flingo.ui.navigation.NavigationDestination

sealed class MainIntent {
    data object OnLoading: MainIntent()
    data class Navigate(val destination: NavigationDestination, val optionalParameters: String? = null) : MainIntent()
    data class NavigateUp(val customBackAction: () -> Unit = {}) : MainIntent()
}
