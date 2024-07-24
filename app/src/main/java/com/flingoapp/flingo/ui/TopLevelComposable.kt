package com.flingoapp.flingo.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    mainViewModel: MainViewModel,
    navHostController: NavHostController = rememberNavController()
) {
    NavHostComposable(
        modifier = Modifier.padding(vertical = 16.dp),
        navController = navHostController,
        mainViewModel = mainViewModel
    )
}