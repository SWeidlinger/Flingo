package com.flingoapp.flingo.data.models.book.page

sealed class PageDetails {
    data class ReadPageDetails(
        val content: String,
        val images: ArrayList<String>? = arrayListOf(),
    ) : PageDetails()

    data class RemoveWordPageDetails(
        val content: String,
        val answer: String
    ) : PageDetails()
}

