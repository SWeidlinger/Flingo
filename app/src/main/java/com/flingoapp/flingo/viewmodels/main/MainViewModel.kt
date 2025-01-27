package com.flingoapp.flingo.viewmodels.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flingoapp.flingo.data.models.User
import com.flingoapp.flingo.viewmodels.MainAction
import com.flingoapp.flingo.viewmodels.book.BookViewModel
import com.flingoapp.flingo.viewmodels.user.UserViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
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

    //TODO: improve, this is not the best approach, but since I would like only one onAction function I don't see a better way of handling this
    lateinit var bookViewModel: BookViewModel
    lateinit var userViewModel: UserViewModel

    fun initializeViewModels(bookViewModel: BookViewModel, userViewModel: UserViewModel) {
        this.bookViewModel = bookViewModel
        this.userViewModel = userViewModel
    }

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    /**
     * On action
     *
     * @param action
     */
    fun onAction(action: MainAction) {
        when (action) {
            is MainAction.FetchMockData -> {
                fetchMockUserData(action.json)
            }

            is MainAction.BookAction.SelectBook -> bookViewModel.onAction(action)
            is MainAction.BookAction.SelectChapter -> bookViewModel.onAction(action)
            is MainAction.BookAction.CompleteChapter -> bookViewModel.onAction(action)
            is MainAction.BookAction.LoadBooks -> bookViewModel.onAction(action)
            is MainAction.BookAction.FetchBooks -> bookViewModel.onAction(action)

            is MainAction.UserAction.DecreaseLives -> userViewModel.onAction(action)
            is MainAction.UserAction.IncreaseLives -> userViewModel.onAction(action)
            is MainAction.UserAction.SelectInterest -> userViewModel.onAction(action)
            is MainAction.UserAction.LoadUser -> userViewModel.onAction(action)
            is MainAction.UserAction.FetchUser -> userViewModel.onAction(action)
        }
    }

    /**
     * Mock fetch book data
     *
     * @param json
     */
    //TODO: remove after mock_user data is definitely not needed anymore
    private fun fetchMockUserData(json: String) {
        viewModelScope.launch {
            updateUiState(_uiState.value.copy(isLoading = true))

            val deserializer = Json {
                ignoreUnknownKeys = true
            }

            val user = deserializer.decodeFromString<User>(json)

            userViewModel.onAction(MainAction.UserAction.LoadUser(user))

            if (user.books.isNullOrEmpty()) {
                updateUiState(
                    _uiState.value.copy(
                        isLoading = false,
                        isError = true
                    )
                )
                return@launch
            }
            bookViewModel.onAction(MainAction.BookAction.LoadBooks(user.books))

            updateUiState(
                _uiState.value.copy(
                    isLoading = false,
                    isError = false
                )
            )
        }
    }

    /**
     * Update ui state
     *
     * @param newUiState
     */
    private fun updateUiState(newUiState: MainUiState) {
        _uiState.update { newUiState }
    }
}