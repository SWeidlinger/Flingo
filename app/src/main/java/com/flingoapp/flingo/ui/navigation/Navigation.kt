package com.flingoapp.flingo.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.flingoapp.flingo.data.models.book.ChapterType
import com.flingoapp.flingo.ui.screens.ChallengeScreen
import com.flingoapp.flingo.ui.screens.ChapterSelectionScreen
import com.flingoapp.flingo.ui.screens.HomeScreen
import com.flingoapp.flingo.ui.screens.ReadScreen
import com.flingoapp.flingo.viewmodels.main.MainIntent
import com.flingoapp.flingo.viewmodels.main.MainViewModel

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
            val mainUiState by mainViewModel.uiState.collectAsStateWithLifecycle()

            HomeScreen(
                mainUiState = mainUiState,
                onAction = { action -> mainViewModel.onAction(action) },
                onNavigate = { destination -> processNavigation(destination, navController) }
            )
        }

        composable(
            route = NavigationDestination.ChapterSelection.route + "/{bookIndex}",
            arguments = listOf(navArgument("bookIndex") {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val bookIndex = backStackEntry.arguments?.getInt("bookIndex") ?: return@composable
            mainViewModel.onAction(MainIntent.OnBookSelected(bookIndex))

            val mainUiState by mainViewModel.uiState.collectAsStateWithLifecycle()

            ChapterSelectionScreen(
                mainUiState = mainUiState,
                onAction = { action -> mainViewModel.onAction(action) },
                onNavigate = { destination -> processNavigation(destination, navController) }
            )
        }

        composable(
            route = NavigationDestination.Chapter.route + "/{chapterIndex}",
            arguments = listOf(navArgument("chapterIndex") { type = NavType.IntType })
        ) { backStackEntry ->
            val chapterIndex = backStackEntry.arguments?.getInt("chapterIndex") ?: return@composable
            mainViewModel.onAction(MainIntent.OnChapterSelected(chapterIndex))

            val mainUiState by mainViewModel.uiState.collectAsStateWithLifecycle()

            val chapterType = mainUiState.currentChapter?.type ?: return@composable

            when (chapterType) {
                ChapterType.CHALLENGE -> {
                    ChallengeScreen(
                        mainUiState = mainUiState,
                        onAction = { action -> mainViewModel.onAction(action) },
                        onNavigate = { destination -> processNavigation(destination, navController) }
                    )
                }

                ChapterType.READ -> {
                    ReadScreen(
                        mainUiState = mainUiState,
                        onAction = { action -> mainViewModel.onAction(action) },
                        onNavigate = { destination -> processNavigation(destination, navController) }
                    )
                }
            }
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
//            val mainUiState by mainViewModel.uiState.collectAsStateWithLifecycle()
//
//            HomeScreen(
//                mainUiState = mainUiState,
////                navController = navController,
//                onAction = { action -> mainViewModel.onAction(action) },
//                onNavigate = { destination -> processNavigation(destination, navController) }
//            )
//        }
//
//        composable<NavigationDestination.LevelSelection> { backStackEntry ->
//            val args = backStackEntry.toRoute<NavigationDestination.LevelSelection>()
//
//            val mainUiState by mainViewModel.uiState.collectAsStateWithLifecycle()
//
//            LevelSelectionScreen(
//                bookIndex = args.bookIndex,
//                mainUiState = mainUiState,
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

        is NavigationIntent.NavigateToChapterSelection -> {
            navController.navigate(intent.chapterSelection.route + "/${intent.bookIndex}")
        }

        is NavigationIntent.NavigateUp -> {
            intent.customBackAction()
            navController.navigateUp()
        }
    }
}