package com.flingoapp.flingo.viewmodels.main

import com.flingoapp.flingo.data.models.User
import com.flingoapp.flingo.data.models.book.Book
import com.flingoapp.flingo.data.models.book.Chapter

/**
 * Main ui state, used to reflect the uiState of a screen
 *
 * @property isLoading
 * @property isError
 * @property userData
 * @property currentBook
 * @property currentChapter
 * @constructor Create empty Main ui state
 */
data class MainUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val userData: User? = null,
    val currentBook: Book? = null,
    val currentChapter: Chapter? = null
)