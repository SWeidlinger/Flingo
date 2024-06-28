package com.flingoapp.flingo.viewmodels.main

import com.flingoapp.flingo.data.models.User
import com.flingoapp.flingo.data.models.book.Book
import com.flingoapp.flingo.data.models.book.Chapter

data class MainUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val userData: User? = null,
    val currentBook: Book? = null,
    val currentChapter: Chapter? = null
)