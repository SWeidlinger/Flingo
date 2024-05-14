package com.flingoapp.flingo.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.flingoapp.flingo.data.viewmodels.main.MainIntent
import com.flingoapp.flingo.data.viewmodels.main.MainViewModel
import com.flingoapp.flingo.ui.screens.HomeScreen

@Composable
fun NavHostComposable(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = NavigationDestination.Home.route,
    mainViewModel: MainViewModel
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavigationDestination.Home.route) {
            HomeScreen(
                mainUiStateFlow = mainViewModel.uiState,
                onAction = { action ->
                    if (isNavigationAction(action)) {
                        processNavigationAction(action, navController)
                    } else {
                        mainViewModel.onAction(action)
                    }
                }
            )
        }
    }
}

private fun isNavigationAction(action: MainIntent): Boolean {
    return when (action) {
        is MainIntent.Navigate -> true
        is MainIntent.NavigateUp -> true
        else -> false
    }
}

private fun processNavigationAction(
    action: MainIntent,
    navController: NavController
) {
    when (action) {
        is MainIntent.Navigate -> {
            val destination = if (action.optionalParameters == null) {
                action.destination.route
            } else {
                action.destination.route + "/${action.optionalParameters}"
            }
            Log.i("MyNavHost", "Navigating to $destination")
            navController.navigate(destination)
        }

        is MainIntent.NavigateUp -> {
            action.customBackAction()
            navController.navigateUp()
        }

        else -> return
    }
}