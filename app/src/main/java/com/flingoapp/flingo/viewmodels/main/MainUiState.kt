package com.flingoapp.flingo.viewmodels.main

import com.flingoapp.flingo.data.models.User

/**
 * Main ui state, used to reflect the uiState of a screen
 *
 * @property isLoading
 * @property isError
 * @property userData
 * @property currentBookId
 * @property currentChapterId
 * @constructor Create empty Main ui state
 */
data class MainUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val userData: User? = null,
    val currentBookId: Int? = null,
    val currentChapterId: Int? = null
)