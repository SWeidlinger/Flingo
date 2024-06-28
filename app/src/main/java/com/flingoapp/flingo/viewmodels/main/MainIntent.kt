package com.flingoapp.flingo.viewmodels.main

sealed class MainIntent {
    data object OnLoading : MainIntent()
    data class OnMockFetchData(val json: String) : MainIntent()
    data class OnBookSelected(val bookIndex: Int): MainIntent()
    data class OnChapterSelected(val chapterIndex: Int): MainIntent()
}
