package com.flingoapp.flingo.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.flingoapp.flingo.di.MainApplication
import com.flingoapp.flingo.di.viewModelFactory
import com.flingoapp.flingo.ui.chapter.ChapterScreen
import com.flingoapp.flingo.ui.screen.ChallengeFinishedScreen
import com.flingoapp.flingo.ui.screen.ChapterSelectionScreen
import com.flingoapp.flingo.ui.screen.HomeScreen
import com.flingoapp.flingo.ui.screen.InterestSelectionScreen
import com.flingoapp.flingo.viewmodel.BookViewModel
import com.flingoapp.flingo.viewmodel.MainAction
import com.flingoapp.flingo.viewmodel.MainViewModel
import com.flingoapp.flingo.viewmodel.PersonalizationViewModel
import com.flingoapp.flingo.viewmodel.UserViewModel

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
    mainViewModel: MainViewModel,
    bookViewModel: BookViewModel,
    userViewModel: UserViewModel,
    personalizationViewModel: PersonalizationViewModel = viewModel<PersonalizationViewModel>(
        factory = viewModelFactory {
            PersonalizationViewModel(
                MainApplication.appModule.genAiModule
            )
        }
    )
) {
    val mainUiState by mainViewModel.uiState.collectAsStateWithLifecycle()
    val bookUiState by bookViewModel.uiState.collectAsStateWithLifecycle()
    val userUiState by userViewModel.uiState.collectAsStateWithLifecycle()

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable<NavigationDestination.Home> {
            HomeScreen(
                bookUiState = bookUiState,
                userUiState = userUiState,
                onAction = { processAction(bookViewModel, userViewModel, it) },
                onNavigate = { processNavigation(it, navController) }
            )
        }

        composable<NavigationDestination.ChapterSelection> { backStackEntry ->
            val args = backStackEntry.toRoute<NavigationDestination.ChapterSelection>()
            bookViewModel.onAction(MainAction.BookAction.SelectBook(args.bookIndex))

            ChapterSelectionScreen(
                bookUiState = bookUiState,
                currentLives = userUiState.currentLives,
                book = bookViewModel.getCurrentBook(),
                onAction = { processAction(bookViewModel, userViewModel, it) },
                onNavigate = { processNavigation(it, navController) }
            )
        }

        composable<NavigationDestination.Chapter> { backStackEntry ->
            val args = backStackEntry.toRoute<NavigationDestination.Chapter>()
            bookViewModel.onAction(MainAction.BookAction.SelectChapter(args.chapterIndex))

            ChapterScreen(
                bookUiState = bookUiState,
                userUiState = userUiState,
                chapter = bookViewModel.getCurrentChapter(),
                onAction = { processAction(bookViewModel, userViewModel, it) },
                onNavigate = { processNavigation(it, navController) }
            )
        }

        composable<NavigationDestination.ChallengeFinished> { backStackEntry ->
            val args = backStackEntry.toRoute<NavigationDestination.ChallengeFinished>()

            ChallengeFinishedScreen(
                mainUiState = mainUiState,
                onAction = { processAction(bookViewModel, userViewModel, it) },
                onNavigate = { processNavigation(it, navController) }
            )
        }

        composable<NavigationDestination.InterestSelection> { backStackEntry ->
            val args = backStackEntry.toRoute<NavigationDestination.InterestSelection>()

            InterestSelectionScreen(
                userUiState = userUiState,
                onAction = { processAction(bookViewModel, userViewModel, it) },
                onNavigate = { processNavigation(it, navController) }
            )
        }
    }
}

private fun processAction(
    bookViewModel: BookViewModel,
    userViewModel: UserViewModel,
//    personalizationViewModel: PersonalizationViewModel,
    action: MainAction
) {
    when (action) {
        is MainAction.BookAction -> {
            bookViewModel.onAction(action)
        }

        is MainAction.UserAction -> {
            userViewModel.onAction(action)
        }

        is MainAction.PersonalizationAction.GenerateBook -> {
            //TODO
//            personalizationViewModel.onAction(action)
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