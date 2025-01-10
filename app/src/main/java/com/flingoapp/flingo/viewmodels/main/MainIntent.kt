package com.flingoapp.flingo.viewmodels.main

/**
 * Main intent used to handle any actions which are accessible and executable from the composables
 *
 * @constructor Create empty Main intent
 */
sealed class MainIntent {
    data object OnLoading : MainIntent()

    /**
     * Intent to fetch mock data from JSON
     *
     * @property json
     * @constructor Create empty On mock fetch data
     */
    data class OnMockFetchData(val json: String) : MainIntent()

    /**
     * Intent for when a book is selected
     *
     * @property bookIndex
     * @constructor Create empty On book selected
     */
    data class OnBookSelected(val bookIndex: Int) : MainIntent()

    /**
     * Intent for when a chapter is selected
     *
     * @property chapterIndex
     * @constructor Create empty On chapter selected
     */
    data class OnChapterSelected(val chapterIndex: Int) : MainIntent()

    data object OnCurrentChapterCompleted : MainIntent()
}
