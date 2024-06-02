package com.flingoapp.flingo.data.viewmodels.main

import com.flingoapp.flingo.data.models.User

data class MainUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val userData: User? = null
)