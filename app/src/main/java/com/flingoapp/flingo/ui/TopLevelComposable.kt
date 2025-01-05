package com.flingoapp.flingo.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.flingoapp.flingo.ui.navigation.NavHostComposable
import com.flingoapp.flingo.viewmodels.main.MainViewModel

/**
 * Top level composable of the app, used to initialize the navigation graph
 *
 * @param mainViewModel
 * @param navHostController
 */
@Composable
fun TopLevelComposable(
    modifier: Modifier,
    mainViewModel: MainViewModel,
    navHostController: NavHostController = rememberNavController()
) {
    NavHostComposable(
        modifier = modifier,
        navController = navHostController,
        mainViewModel = mainViewModel
    )
}