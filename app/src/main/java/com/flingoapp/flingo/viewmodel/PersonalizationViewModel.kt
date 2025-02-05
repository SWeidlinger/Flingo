package com.flingoapp.flingo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flingoapp.flingo.di.GenAiModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PersonalizationUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false
)

class PersonalizationViewModel(
    private val genAiModule: GenAiModule
) : ViewModel() {
    companion object {
        private const val TAG = "PersonalizationViewModel"
    }

    private val _uiState = MutableStateFlow(PersonalizationUiState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: MainAction.PersonalizationAction) {
        when (action) {
            MainAction.PersonalizationAction.GenerateBook -> generateBook()
        }
    }

    private fun generateBook() {
        viewModelScope.launch {
            updateUiState(PersonalizationUiState(isLoading = true))

            //TODO: implement book prompt
            genAiModule.repository.getResponse("")
                .onFailure { error ->
                    errorHandling(error)
                }
                .onSuccess { message ->
                    //TODO implement
                    Log.e(TAG, message)
                    updateUiState(_uiState.value.copy(isLoading = false))
                }
        }
    }

    private fun errorHandling(error: Throwable) {
        Log.e(TAG, "Error: ${error.message}")
        updateUiState(_uiState.value.copy(isLoading = false, isError = true))
    }

    /**
     * Update ui state
     *
     * @param newUiState
     */
    private fun updateUiState(newUiState: PersonalizationUiState) {
        _uiState.update { newUiState }
    }
}