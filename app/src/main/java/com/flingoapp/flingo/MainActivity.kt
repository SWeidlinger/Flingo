package com.flingoapp.flingo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.flingoapp.flingo.ui.FlingoApp
import com.flingoapp.flingo.viewmodels.MainAction
import com.flingoapp.flingo.viewmodels.book.BookViewModel
import com.flingoapp.flingo.viewmodels.main.MainViewModel
import com.flingoapp.flingo.viewmodels.user.UserViewModel

/**
 * Main activity
 *
 * @constructor Create empty Main activity
 */
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var bookViewModel: BookViewModel
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        bookViewModel = ViewModelProvider(this)[BookViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        // Initialize the MainViewModel with BookViewModel and UserViewModel
        mainViewModel.initializeViewModels(bookViewModel, userViewModel)

        loadMockData()

        setContent {
            FlingoApp(mainViewModel = mainViewModel)
        }
    }

    private fun loadMockData() {
        //TODO: remove after mock_user data is definitely not needed anymore
//        val mockUserJson = assets.open("mock_user.json").bufferedReader().use { it.readText() }
//        mainViewModel.onAction(MainAction.FetchMockData(mockUserJson))

        val bookList = mutableListOf<String>()
        val book1 =
            assets.open("book/book_example_textbook_wunderwelt_sprache.json").bufferedReader()
                .use { it.readText() }
        bookList.add(book1)

//        val book2 =
//            assets.open("book/book_remove_word.json").bufferedReader()
//                .use { it.readText() }
//        bookList.add(book2)
//
//        val book3 =
//            assets.open("book/book_quiz.json").bufferedReader()
//                .use { it.readText() }
//        bookList.add(book3)
//
//        val book4 =
//            assets.open("book/book_remove_word.json").bufferedReader()
//                .use { it.readText() }
//        bookList.add(book4)

        val user = assets.open("user/user_jakob.json").bufferedReader().use { it.readText() }

        userViewModel.onAction(MainAction.UserAction.FetchUser(user))
        bookViewModel.onAction(MainAction.BookAction.FetchBooks(bookList))
    }
}