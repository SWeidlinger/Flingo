package com.flingoapp.flingo.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.flingoapp.flingo.data.viewmodels.main.MainViewModel
import com.flingoapp.flingo.ui.screens.HomeScreen
import com.flingoapp.flingo.ui.screens.LevelSelectionScreen

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
                onAction = { action -> mainViewModel.onAction(action) },
                onNavigate = { destination -> processNavigation(destination, navController) }
            )
        }

        composable(
            route = NavigationDestination.LevelSelection.route + "/{bookIndex}",
            arguments = listOf(navArgument("bookIndex") {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val bookIndex = backStackEntry.arguments?.getInt("bookIndex") ?: return@composable

            LevelSelectionScreen(
                bookIndex = bookIndex,
                mainUiStateFlow = mainViewModel.uiState,
                onAction = { action -> mainViewModel.onAction(action) },
                onNavigate = { destination -> processNavigation(destination, navController) }
            )
        }
    }
}

//TODO: use once compose navigation side effect for weird pager scrolling behavior is fixed
//@Composable
//fun NavHostComposable(
//    modifier: Modifier = Modifier,
//    navController: NavHostController,
//    startDestination: NavigationDestination = NavigationDestination.Home,
//    mainViewModel: MainViewModel
//) {
//    NavHost(
//        modifier = modifier,
//        navController = navController,
//        startDestination = startDestination
//    ) {
//        composable<NavigationDestination.Home> {
//            HomeScreen(
//                mainUiStateFlow = mainViewModel.uiState,
////                navController = navController,
//                onAction = { action -> mainViewModel.onAction(action) },
//                onNavigate = { destination -> processNavigation(destination, navController) }
//            )
//        }
//
//        composable<NavigationDestination.LevelSelection> { backStackEntry ->
//            val args = backStackEntry.toRoute<NavigationDestination.LevelSelection>()
//
//            LevelSelectionScreen(
//                bookIndex = args.bookIndex,
//                mainUiStateFlow = mainViewModel.uiState,
//                onAction = { action -> mainViewModel.onAction(action) },
//                onNavigate = { destination -> processNavigation(destination, navController) }
//            )
//        }
//    }
//}

private fun processNavigation(
    intent: NavigationIntent,
    navController: NavController
) {
    Log.i("MyNavHost", "Navigating to ${intent.javaClass.simpleName}")
    when (intent) {
        is NavigationIntent.NavigateToHome -> {
            navController.navigate(intent.home.route)
        }

        is NavigationIntent.NavigateToLevelSelection -> {
            navController.navigate(intent.levelSelection.route + "/${intent.bookIndex}")
        }

        is NavigationIntent.NavigateUp -> {
            intent.customBackAction()
            navController.navigateUp()
        }
    }
}