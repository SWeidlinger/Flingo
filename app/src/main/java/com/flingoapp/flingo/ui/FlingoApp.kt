package com.flingoapp.flingo.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeGesturesPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.flingoapp.flingo.navigation.NavHostComposable
import com.flingoapp.flingo.ui.theme.FlingoTheme
import com.flingoapp.flingo.viewmodel.BookViewModel
import com.flingoapp.flingo.viewmodel.MainViewModel
import com.flingoapp.flingo.viewmodel.UserViewModel

/**
 * Top level composable of the app, used to initialize the navigation graph
 *
 * @param mainViewModel
 * @param navHostController
 */
@Composable
fun FlingoApp(
    mainViewModel: MainViewModel,
    bookViewModel: BookViewModel,
    userViewModel: UserViewModel,
    navHostController: NavHostController = rememberNavController()
) {
    FlingoTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .safeGesturesPadding()
        ) { innerPadding ->
            NavHostComposable(
                modifier = Modifier.padding(innerPadding),
                navController = navHostController,
                mainViewModel = mainViewModel,
                bookViewModel = bookViewModel,
                userViewModel = userViewModel
            )
        }
    }
}