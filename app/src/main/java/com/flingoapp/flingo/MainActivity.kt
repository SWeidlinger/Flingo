package com.flingoapp.flingo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
    private val bookViewModel: BookViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        loadMockData()

        setContent {
            FlingoApp(
                mainViewModel, bookViewModel, userViewModel
            )
        }
    }

    private fun loadMockData() {
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
//            assets.open("book/book_order_story.json").bufferedReader()
//                .use { it.readText() }
//        bookList.add(book4)

        val user = assets.open("user/user_jakob.json").bufferedReader().use { it.readText() }

        userViewModel.onAction(MainAction.UserAction.FetchUser(user))
        bookViewModel.onAction(MainAction.BookAction.FetchBooks(bookList))
    }
}