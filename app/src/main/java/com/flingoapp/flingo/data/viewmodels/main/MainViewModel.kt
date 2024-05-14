package com.flingoapp.flingo.data.viewmodels.main

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {
    private val TAG = "MainViewModel"

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: MainIntent) {
        when (action) {
            is MainIntent.OnLoading -> {
                updateUiState(_uiState.value.copy(isLoading = true))
            }

            is MainIntent.Navigate -> {
                Log.e(TAG, "This should never be displayed, handle navigation in navigation composable!")
            }

            is MainIntent.NavigateUp -> {
                Log.e(TAG, "This should never be displayed, handle navigation in navigation composable!")
            }
        }
    }

    private fun updateUiState(newUiState: MainUiState) {
        _uiState.update { newUiState }
    }
}