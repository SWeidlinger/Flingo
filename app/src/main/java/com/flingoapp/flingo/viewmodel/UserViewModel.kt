package com.flingoapp.flingo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flingoapp.flingo.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

data class UserUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val name: String = "User",
    val profileImage: String? = null,
    val currentLives: Int = 5,
    val currentReadingStreak: Int = 0,
    val selectedInterests: ArrayList<String> = arrayListOf()
)

class UserViewModel : ViewModel() {
    companion object {
        private const val TAG = "UserViewModel"
    }

    private val _uiState = MutableStateFlow(UserUiState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: MainAction.UserAction) {
        when (action) {
            MainAction.UserAction.IncreaseLives -> increaseLives()
            MainAction.UserAction.DecreaseLives -> decreaseLives()
            is MainAction.UserAction.SelectInterest -> selectInterest(action.interest)
            is MainAction.UserAction.FetchUser -> fetchUser(action.userJson)
        }
    }

    private fun fetchUser(userJson: String) {
        viewModelScope.launch {
            updateUiState(_uiState.value.copy(isLoading = true))

            val deserializer = Json {
                ignoreUnknownKeys = true
            }

            val user = deserializer.decodeFromString<User>(userJson)

            updateUiState(
                _uiState.value.copy(
                    isLoading = false,
                    isError = false,
                    name = user.name,
                    profileImage = user.profileImage,
                    currentLives = user.currentLives,
                    currentReadingStreak = user.currentReadingStreak,
                    selectedInterests = user.selectedInterests
                )
            )
        }
    }

    private fun increaseLives() {
        val currentLives = _uiState.value.currentLives
        updateUiState(_uiState.value.copy(currentLives = currentLives + 1))
    }

    private fun decreaseLives() {
        val currentLives = _uiState.value.currentLives
        if (currentLives > 0) {
            updateUiState(_uiState.value.copy(currentLives = currentLives - 1))
        }
    }

    private fun selectInterest(interest: String) {
        updateUiState(_uiState.value.copy(selectedInterests = arrayListOf(interest)))
    }

    private fun updateUiState(newState: UserUiState) {
        _uiState.update { newState }
    }
}