package com.flingoapp.flingo.viewmodels.book

import com.flingoapp.flingo.data.models.book.Book

data class BookUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val books: ArrayList<Book> = arrayListOf(),
    val currentBookId: Int? = null,
    val currentChapterId: Int? = null
)