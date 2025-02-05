package com.flingoapp.flingo.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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

/**
 * Main view model
 *
 * @constructor Create empty Main view model
 */
class MainViewModel : ViewModel() {
    companion object {
        private const val TAG = "MainViewModel"
    }

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    /**
     * Update ui state
     *
     * @param newUiState
     */
    private fun updateUiState(newUiState: MainUiState) {
        _uiState.update { newUiState }
    }
}