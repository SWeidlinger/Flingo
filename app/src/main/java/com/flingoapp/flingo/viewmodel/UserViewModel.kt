package com.flingoapp.flingo.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flingoapp.flingo.data.model.User
import com.flingoapp.flingo.ui.screen.UserImageStyle
import com.flingoapp.flingo.ui.screen.UserInterest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

data class UserUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val name: String = "User",
    val age: Int = 7,
    val profileImage: String? = null,
    val language: String = "de",
    val currentLives: Int = 5,
    val currentReadingStreak: Int = 0,
    val selectedInterests: List<String> = listOf(),
    val selectedImageStyle: String = UserImageStyle.COMIC.displayName,
)

class UserViewModel : ViewModel() {
    companion object {
        private const val TAG = "UserViewModel"
    }

    private val _uiState = MutableStateFlow(UserUiState())
    val uiState = _uiState.asStateFlow()

    private var previousUser: UserUiState? = null

    fun onAction(action: MainAction.UserAction) {
        when (action) {
            MainAction.UserAction.IncreaseLives -> increaseLives()
            MainAction.UserAction.DecreaseLives -> decreaseLives()
            is MainAction.UserAction.SelectInterest -> selectInterest(action.interest)
            is MainAction.UserAction.RemoveInterest -> removeInterest(action.interest)
            is MainAction.UserAction.FetchUser -> fetchUser(action.userJson)
            is MainAction.UserAction.SwitchUser -> switchUser(action.context)
            is MainAction.UserAction.SelectImageStyle -> selectImageStyle(action.imageStyle)
            MainAction.UserAction.RefillLives -> refillLives()
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
                    age = user.age,
                    language = user.language,
                    profileImage = user.profileImage,
                    currentLives = user.currentLives,
                    currentReadingStreak = user.currentReadingStreak,
                    selectedInterests = user.selectedInterests,
                    selectedImageStyle = user.selectedImageStyle
                )
            )
        }
    }

    private fun switchUser(context: Context) {
        //currently just supporting switching between two predefined users
        if (previousUser == null) {
            val userJson = if (_uiState.value.name == "Jakob") {
                context.assets.open("user/user_lisa.json").bufferedReader().use { it.readText() }
            } else {
                context.assets.open("user/user_jakob.json").bufferedReader().use { it.readText() }
            }

            previousUser = _uiState.value
            fetchUser(userJson)
        } else {
            val temp = _uiState.value
            _uiState.update { previousUser!! }
            previousUser = temp
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

    private fun refillLives(){
        //TODO: now set to 5 lives
        updateUiState(_uiState.value.copy(currentLives = 5))
    }

    private fun selectInterest(interest: UserInterest) {
        if (_uiState.value.selectedInterests.contains(interest.displayName)) return

        //limit to two selected interests for now since, better results, remove oldest interest
        val newInterests = _uiState.value.selectedInterests.toMutableList()
        if (_uiState.value.selectedInterests.size == 2) {
            newInterests.removeAt(0)
        }

        newInterests.add(interest.displayName)
        updateUiState(_uiState.value.copy(selectedInterests = newInterests))
    }

    private fun removeInterest(interest: UserInterest) {
        if (!_uiState.value.selectedInterests.contains(interest.displayName)) return

        val newInterests = _uiState.value.selectedInterests.toMutableList()
        newInterests.remove(interest.displayName)
        updateUiState(_uiState.value.copy(selectedInterests = newInterests))
    }

    private fun selectImageStyle(imageStyle: UserImageStyle) {
        updateUiState(_uiState.value.copy(selectedImageStyle = imageStyle.displayName))
    }

    private fun updateUiState(newState: UserUiState) {
        _uiState.update { newState }
    }
}