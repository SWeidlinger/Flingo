package com.flingoapp.flingo.navigation

import android.util.Log
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.toRoute
import com.flingoapp.flingo.BuildConfig
import com.flingoapp.flingo.ui.screen.AllLivesLostScreen
import com.flingoapp.flingo.ui.screen.ChallengeFinishedScreen
import com.flingoapp.flingo.ui.screen.ChapterScreen
import com.flingoapp.flingo.ui.screen.ChapterSelectionScreen
import com.flingoapp.flingo.ui.screen.HomeScreen
import com.flingoapp.flingo.ui.screen.SettingsScreen
import com.flingoapp.flingo.ui.screen.StreakAndStarsScreen
import com.flingoapp.flingo.ui.screen.UserScreen
import com.flingoapp.flingo.viewmodel.BookViewModel
import com.flingoapp.flingo.viewmodel.MainAction
import com.flingoapp.flingo.viewmodel.MainViewModel
import com.flingoapp.flingo.viewmodel.PersonalizationViewModel
import com.flingoapp.flingo.viewmodel.UserViewModel

private var NAVIGATION_ANIMATION_DURATION = if (BuildConfig.DEBUG) 0 else 1000

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
    personalizationViewModel: PersonalizationViewModel
) {
    //TODO move to individual screens
    val mainUiState by mainViewModel.uiState.collectAsStateWithLifecycle()
    val bookUiState by bookViewModel.uiState.collectAsStateWithLifecycle()
    val userUiState by userViewModel.uiState.collectAsStateWithLifecycle()
    val personalizationUiState by personalizationViewModel.uiState.collectAsStateWithLifecycle()

    val backStackEntry by navController.currentBackStackEntryAsState()
    var currentDestinationRoute by rememberSaveable { mutableStateOf(backStackEntry?.destination?.route) }
    var previousDestinationRoute by rememberSaveable { mutableStateOf(backStackEntry?.destination?.parent?.route) }

    LaunchedEffect(backStackEntry?.destination?.route) {
        previousDestinationRoute = currentDestinationRoute
        currentDestinationRoute = backStackEntry?.destination?.route

        Log.d("NavHostComposable", "currentDestinationRoute: $currentDestinationRoute")
        Log.d("NavHostComposable", "previousDestinationRoute: $previousDestinationRoute")
    }

    Log.e("NavHostComposable", "in NavHostComposable")

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable<NavigationDestination.Home>(
            popEnterTransition = {
                if (isRoute(previousDestinationRoute, NavigationDestination.StreakAndStars)) {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Up,
                        tween(NAVIGATION_ANIMATION_DURATION)
                    )
                } else if (isRoute(
                        previousDestinationRoute,
                        NavigationDestination.User
                    )
                ) {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        tween(NAVIGATION_ANIMATION_DURATION)
                    )
                } else {
                    null
                }
            },
            exitTransition = {
                if (isRoute(currentDestinationRoute, NavigationDestination.StreakAndStars)) {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Down,
                        tween(NAVIGATION_ANIMATION_DURATION)
                    )
                } else if (isRoute(currentDestinationRoute, NavigationDestination.User)
                ) {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        tween(NAVIGATION_ANIMATION_DURATION)
                    )
                } else {
                    null
                }
            }
        ) {
            Log.e("NavHostComposable", "in home")

            HomeScreen(
                bookUiState = bookUiState,
                userUiState = userUiState,
                personalizationUiState = personalizationUiState,
                onAction = {
                    processAction(
                        bookViewModel,
                        userViewModel,
                        personalizationViewModel,
                        it
                    )
                },
                onNavigate = { processNavigation(it, navController) }
            )
        }

        composable<NavigationDestination.ChapterSelection> { backStackEntry ->
            Log.e("NavHostComposable", "in chapterSelection")

            val args = backStackEntry.toRoute<NavigationDestination.ChapterSelection>()
            bookViewModel.onAction(MainAction.BookAction.SelectBook(args.bookId))

            ChapterSelectionScreen(
                bookUiState = bookUiState,
                currentLives = userUiState.currentLives,
                book = bookViewModel.getCurrentBook(),
                onAction = {
                    processAction(
                        bookViewModel,
                        userViewModel,
                        personalizationViewModel,
                        it
                    )
                },
                onNavigate = { processNavigation(it, navController) }
            )
        }

        composable<NavigationDestination.Chapter> { backStackEntry ->
            Log.e("NavHostComposable", "in chapter")

            val args = backStackEntry.toRoute<NavigationDestination.Chapter>()
            bookViewModel.onAction(MainAction.BookAction.SelectChapter(args.chapterId))

            ChapterScreen(
                bookUiState = bookUiState,
                userUiState = userUiState,
                chapter = bookViewModel.getCurrentChapter(),
                onAction = {
                    processAction(
                        bookViewModel,
                        userViewModel,
                        personalizationViewModel,
                        it
                    )
                },
                onNavigate = { processNavigation(it, navController) }
            )
        }

        composable<NavigationDestination.ChallengeFinished> { backStackEntry ->
            Log.e("NavHostComposable", "in challengeFinished")

            val args = backStackEntry.toRoute<NavigationDestination.ChallengeFinished>()

            ChallengeFinishedScreen(
                bookUiState = bookUiState,
                onAction = {
                    processAction(
                        bookViewModel,
                        userViewModel,
                        personalizationViewModel,
                        it
                    )
                },
                onNavigate = { processNavigation(it, navController) }
            )
        }

        composable<NavigationDestination.User>(
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    tween(NAVIGATION_ANIMATION_DURATION)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    tween(NAVIGATION_ANIMATION_DURATION)
                )
            }
        ) { backStackEntry ->
            Log.e("NavHostComposable", "in InterestSelection")

            val args = backStackEntry.toRoute<NavigationDestination.User>()

            UserScreen(
                userUiState = userUiState,
                onAction = {
                    processAction(
                        bookViewModel,
                        userViewModel,
                        personalizationViewModel,
                        it
                    )
                },
                onNavigate = { processNavigation(it, navController) }
            )
        }

        composable<NavigationDestination.StreakAndStars>(
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down,
                    tween(NAVIGATION_ANIMATION_DURATION)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up,
                    tween(NAVIGATION_ANIMATION_DURATION)
                )
            }
        ) {
            StreakAndStarsScreen(
                onNavigate = { processNavigation(it, navController) }
            )
        }

        composable<NavigationDestination.Settings> {
            Log.e("NavHostComposable", "in settings")

            SettingsScreen(
                personalizationUiState = personalizationUiState,
                onAction = {
                    processAction(
                        bookViewModel,
                        userViewModel,
                        personalizationViewModel,
                        it
                    )
                },
                onNavigate = { processNavigation(it, navController) }
            )
        }

        composable<NavigationDestination.AllLivesLost> {
            AllLivesLostScreen(
                onAction = {
                    processAction(
                        bookViewModel,
                        userViewModel,
                        personalizationViewModel,
                        it
                    )
                },
                onNavigate = { processNavigation(it, navController) }
            )
        }
    }
}

private fun isRoute(
    route: String?,
    destination: NavigationDestination
): Boolean {
    return route == destination::class.qualifiedName
}

private fun processAction(
    bookViewModel: BookViewModel,
    userViewModel: UserViewModel,
    personalizationViewModel: PersonalizationViewModel,
    action: MainAction
) {
    when (action) {
        is MainAction.BookAction -> {
            bookViewModel.onAction(action)
        }

        is MainAction.UserAction -> {
            userViewModel.onAction(action)
        }

        is MainAction.PersonalizationAction -> {
            personalizationViewModel.onAction(action)
        }
    }
}

/**
 * Function used to handle [NavigationAction] from the onNavigate lambda parameter
 *
 * @param intent
 * @param navController
 */
private fun processNavigation(
    intent: NavigationAction,
    navController: NavController
) {
    when (intent) {
        is NavigationAction.Screen -> {
            Log.i("Navigation", "Navigating to ${intent.destination.javaClass.simpleName}")
            navController.navigate(intent.destination, intent.navOptions)
        }

        is NavigationAction.Up -> {
            Log.i("Navigation", "Navigating up")
            intent.customBackAction.invoke()
            navController.navigateUp()
        }
    }
}