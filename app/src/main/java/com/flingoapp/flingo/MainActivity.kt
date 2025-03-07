package com.flingoapp.flingo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flingoapp.flingo.di.MainApplication
import com.flingoapp.flingo.di.viewModelFactory
import com.flingoapp.flingo.ui.FlingoApp
import com.flingoapp.flingo.viewmodel.BookViewModel
import com.flingoapp.flingo.viewmodel.MainAction
import com.flingoapp.flingo.viewmodel.MainViewModel
import com.flingoapp.flingo.viewmodel.UserViewModel

/**
 * Main activity
 *
 * @constructor Create empty Main activity
 */
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val bookViewModel: BookViewModel = viewModel<BookViewModel>(
                factory = viewModelFactory {
                    BookViewModel(
                        bookRepository = MainApplication.bookRepository
                    )
                }
            )
            val userViewModel: UserViewModel by viewModels()

            loadMockData(
                bookViewModel,
                userViewModel
            )

            FlingoApp(
                mainViewModel,
                bookViewModel,
                userViewModel
            )
        }
    }

    private fun loadMockData(bookViewModel: BookViewModel, userViewModel: UserViewModel) {
        val bookList = mutableListOf<String>()
        val exampleBook = assets.open("book/book_example_textbook_wunderwelt_sprache.json").bufferedReader()
                .use { it.readText() }
        bookList.add(exampleBook)

        val user = assets.open("user/user_jakob.json").bufferedReader().use { it.readText() }

        userViewModel.onAction(MainAction.UserAction.FetchUser(user))
        bookViewModel.onAction(MainAction.BookAction.FetchBooks(bookList))
    }
}