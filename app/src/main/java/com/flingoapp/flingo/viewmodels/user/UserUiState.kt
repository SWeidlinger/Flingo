package com.flingoapp.flingo.viewmodels.user

data class UserUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val name: String = "User",
    val profileImage: String? = null,
    val currentLives: Int = 5,
    val currentReadingStreak: Int = 0,
    val selectedInterests: ArrayList<String> = arrayListOf()
)