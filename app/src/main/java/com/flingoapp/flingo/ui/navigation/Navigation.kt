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
import com.flingoapp.flingo.data.models.book.ChapterType
import com.flingoapp.flingo.ui.screens.ChallengeScreen
import com.flingoapp.flingo.ui.screens.ChapterSelectionScreen
import com.flingoapp.flingo.ui.screens.HomeScreen
import com.flingoapp.flingo.ui.screens.ReadScreen
import com.flingoapp.flingo.viewmodels.main.MainIntent
import com.flingoapp.flingo.viewmodels.main.MainViewModel

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
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable<NavigationDestination.Home> {
            val mainUiState by mainViewModel.uiState.collectAsStateWithLifecycle()

            //TODO: remove after testing
            // used to launch with specific chapter and book selected
//            val wantedBook = mainUiState.userData?.books?.get(1)
//            val wantedChapter = wantedBook?.chapters?.first()
//
//            val testUiState = MainUiState(
//                userData = mainUiState.userData,
//                currentBook = wantedBook,
//                currentChapter = wantedChapter
//            )
//
//            ChallengeScreen(
//                mainUiState = testUiState,
//                onAction = { action -> mainViewModel.onAction(action) },
//                onNavigate = { destination ->
//                    processNavigation(destination, navController)
//                }
//            )

            HomeScreen(
                mainUiState = mainUiState,
                onAction = { action -> mainViewModel.onAction(action) },
                onNavigate = { destination ->
                    processNavigation(destination, navController)
                }
            )
        }

        composable<NavigationDestination.ChapterSelection> { backStackEntry ->
            val args = backStackEntry.toRoute<NavigationDestination.ChapterSelection>()
            mainViewModel.onAction(MainIntent.OnBookSelected(args.bookIndex))

            val mainUiState by mainViewModel.uiState.collectAsStateWithLifecycle()

            ChapterSelectionScreen(
                mainUiState = mainUiState,
                onAction = { action -> mainViewModel.onAction(action) },
                onNavigate = { destination ->
                    processNavigation(destination, navController)
                }
            )
        }

        composable<NavigationDestination.Chapter> { backStackEntry ->
            val args = backStackEntry.toRoute<NavigationDestination.Chapter>()
            mainViewModel.onAction(MainIntent.OnChapterSelected(args.chapterIndex))

            val mainUiState by mainViewModel.uiState.collectAsStateWithLifecycle()

            val chapterType = mainUiState.currentChapter?.type ?: return@composable

            when (chapterType) {
                ChapterType.CHALLENGE -> {
                    ChallengeScreen(
                        mainUiState = mainUiState,
                        onAction = { action -> mainViewModel.onAction(action) },
                        onNavigate = { destination ->
                            processNavigation(destination, navController)
                        }
                    )
                }

                ChapterType.READ -> {
                    ReadScreen(
                        mainUiState = mainUiState,
                        onAction = { action -> mainViewModel.onAction(action) },
                        onNavigate = { destination ->
                            processNavigation(destination, navController)
                        }
                    )
                }

                ChapterType.MIXED -> {
                    //TODO: not yet implemented, might not be needed
                }
            }
        }
    }
}

/**
 * Function used to handle the [NavigationIntent] from the onNavigate lambda parameter present on the
 * different screen
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