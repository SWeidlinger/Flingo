package com.flingoapp.flingo.viewmodels.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flingoapp.flingo.data.models.User
import com.flingoapp.flingo.viewmodels.MainAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor() : ViewModel() {
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
            is MainAction.UserAction.LoadUser -> loadUser(action.user)
            is MainAction.UserAction.FetchUser -> fetchUser(action.userJson)
        }
    }

    //TODO: remove after mock_user data is definitely not needed anymore
    private fun loadUser(user: User) {
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