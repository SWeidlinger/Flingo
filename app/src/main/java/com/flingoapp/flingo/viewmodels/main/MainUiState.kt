package com.flingoapp.flingo.viewmodels.main

/**
 * Main ui state, used to reflect the uiState of a screen
 *
 * @property isLoading
 * @property isError
 * @constructor Create empty Main ui state
 */
data class MainUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false
)