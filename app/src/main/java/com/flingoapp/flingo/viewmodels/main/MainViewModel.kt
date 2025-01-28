package com.flingoapp.flingo.viewmodels.main

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * Main view model
 *
 * @constructor Create empty Main view model
 */
@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
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