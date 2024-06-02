package com.flingoapp.flingo.data.viewmodels.main

sealed class MainIntent {
    data object OnLoading : MainIntent()
    data class OnMockFetchData(val json: String) : MainIntent()
}
