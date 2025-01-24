package com.flingoapp.flingo.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.flingoapp.flingo.ui.screen.ChallengeFinishedScreen
import com.flingoapp.flingo.ui.screen.ChapterSelectionScreen
import com.flingoapp.flingo.ui.screen.HomeScreen
import com.flingoapp.flingo.ui.screen.InterestSelectionScreen
import com.flingoapp.flingo.viewmodels.main.MainIntent
import com.flingoapp.flingo.viewmodels.main.MainViewModel
import com.flingoapp.flingo.ui.screen.chapter.ChapterScreen

/**
 * Nav host composable
 *
 * @param modifier
 * @param navController
 * @param startDestination
 * @param mainViewModel
 */
@Composable
fun NavHostComposable(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: NavigationDestination = NavigationDestination.Home,
    mainViewModel: MainViewModel
) {
    val mainUiState by mainViewModel.uiState.collectAsStateWithLifecycle()

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable<NavigationDestination.Home> {
            HomeScreen(
                mainUiState = mainUiState,
                onAction = mainViewModel::onAction,
                onNavigate = { processNavigation(it, navController) }
            )
        }

        composable<NavigationDestination.ChapterSelection> { backStackEntry ->
            val args = backStackEntry.toRoute<NavigationDestination.ChapterSelection>()
            mainViewModel.onAction(MainIntent.OnBookSelect(args.bookIndex))

            ChapterSelectionScreen(
                mainUiState = mainUiState,
                book = mainViewModel.getCurrentBook(),
                onAction = mainViewModel::onAction,
                onNavigate = { processNavigation(it, navController) }
            )
        }

        composable<NavigationDestination.Chapter> { backStackEntry ->
            val args = backStackEntry.toRoute<NavigationDestination.Chapter>()
            mainViewModel.onAction(MainIntent.OnChapterSelect(args.chapterIndex))

            ChapterScreen(
                mainUiState = mainUiState,
                chapter = mainViewModel.getCurrentChapter(),
                onAction = mainViewModel::onAction,
                onNavigate = { processNavigation(it, navController) }
            )
        }

        composable<NavigationDestination.ChallengeFinished> { backStackEntry ->
            val args = backStackEntry.toRoute<NavigationDestination.ChallengeFinished>()

            ChallengeFinishedScreen(
                mainUiState = mainUiState,
                onAction = mainViewModel::onAction,
                onNavigate = { processNavigation(it, navController) }
            )
        }

        composable<NavigationDestination.InterestSelection> { backStackEntry ->
            val args = backStackEntry.toRoute<NavigationDestination.InterestSelection>()

            InterestSelectionScreen(
                mainUiState = mainUiState,
                onAction = mainViewModel::onAction,
                onNavigate = { processNavigation(it, navController) }
            )
        }
    }
}

/**
 * Function used to handle [NavigationIntent] from the onNavigate lambda parameter
 *
 * @param intent
 * @param navController
 */
private fun processNavigation(
    intent: NavigationIntent,
    navController: NavController
) {
    when (intent) {
        is NavigationIntent.Screen -> {
            Log.i("Navigation", "Navigating to ${intent.destination.javaClass.simpleName}")
            navController.navigate(intent.destination)
        }

        is NavigationIntent.Up -> {
            Log.i("Navigation", "Navigating up")
            intent.customBackAction.invoke()
            navController.navigateUp()
        }
    }
}