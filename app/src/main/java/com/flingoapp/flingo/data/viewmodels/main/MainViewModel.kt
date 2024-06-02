package com.flingoapp.flingo.data.viewmodels.main

import androidx.lifecycle.ViewModel
import com.flingoapp.flingo.data.models.User
import com.google.gson.Gson
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

            is MainIntent.OnMockFetchData -> mockFetchBockData(action.json)
        }
    }

    private fun mockFetchBockData(json: String) {
        updateUiState(_uiState.value.copy(isLoading = true))

        val user = Gson().fromJson(json, User::class.java)

        updateUiState(
            _uiState.value.copy(
                isLoading = false,
                userData = user
            )
        )
    }


    private fun updateUiState(newUiState: MainUiState) {
        _uiState.update { newUiState }
    }
}