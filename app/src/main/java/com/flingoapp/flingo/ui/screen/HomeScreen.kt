package com.flingoapp.flingo.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.flingoapp.flingo.data.model.MockData
import com.flingoapp.flingo.navigation.NavigationAction
import com.flingoapp.flingo.navigation.NavigationDestination
import com.flingoapp.flingo.ui.CustomPreview
import com.flingoapp.flingo.ui.component.BookItem
import com.flingoapp.flingo.ui.component.topbar.CustomHomeScreenTopBar
import com.flingoapp.flingo.ui.theme.FlingoTheme
import com.flingoapp.flingo.viewmodel.BookUiState
import com.flingoapp.flingo.viewmodel.MainAction
import com.flingoapp.flingo.viewmodel.PersonalizationUiState
import com.flingoapp.flingo.viewmodel.UserUiState
import kotlin.math.absoluteValue

/**
 * Home screen
 *
 * @param onAction
 * @param onNavigate
 */
@Composable
fun HomeScreen(
    userUiState: UserUiState,
    bookUiState: BookUiState,
    personalizationUiState: PersonalizationUiState,
    onAction: (MainAction) -> Unit,
    onNavigate: (NavigationAction) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { bookUiState.books.size })

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    Scaffold(
        topBar = {
            CustomHomeScreenTopBar(
                userName = userUiState.name,
                currentStreak = userUiState.currentReadingStreak,
                currentLives = userUiState.currentLives,
                onUserClick = {
                    onNavigate(NavigationAction.Screen(NavigationDestination.InterestSelection))
                },
                onStreakClick = {
                    onNavigate(NavigationAction.Screen(NavigationDestination.StreakAndStars))
                },
                onSettingsClick = {
                    onNavigate(NavigationAction.Screen(NavigationDestination.Settings))
                },
                onSettingsLongClick = {
                    onAction(MainAction.PersonalizationAction.ToggleDebugMode)
                },
                onAwardClick = {
                    //TODO
                }
            )
        }
    ) { innerPadding ->
        if (bookUiState.books.isEmpty()) {
            Text(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                text = "User has no book!",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )
        } else {
            val pageSpacing = (screenWidth / 2)
            val bookSize = pageSpacing * 0.75f
            val horizontalOffsetPager = pageSpacing - (bookSize / 2)

            Box(modifier = Modifier.fillMaxSize()) {
                HorizontalPager(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalAlignment = Alignment.CenterVertically,
                    contentPadding = PaddingValues(
                        start = horizontalOffsetPager,
                        end = horizontalOffsetPager
                    ),
                    pageSize = PageSize.Fixed(pageSpacing),
                    state = pagerState,
                ) { bookIndex ->
                    val pageOffset = (
                            (pagerState.currentPage - bookIndex) + pagerState.currentPageOffsetFraction).absoluteValue

                    Column(
                        modifier = Modifier
                            .graphicsLayer {
                                val alphaValue = lerp(
                                    start = 0.4f,
                                    stop = 1f,
                                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                )
                                val scaleValue = lerp(
                                    start = 0.7f,
                                    stop = 1f,
                                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                )
                                alpha = alphaValue
                                scaleX = scaleValue
                                scaleY = scaleValue
                            },
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        BookItem(
                            itemSize = bookSize,
                            pagerState = pagerState,
                            bookIndex = bookIndex,
                            book = bookUiState.books[bookIndex],
                            onClick = {
                                onNavigate(
                                    NavigationAction.Screen(
                                        destination = NavigationDestination.ChapterSelection(
                                            bookId = bookUiState.books[bookIndex].id
                                        )
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@CustomPreview
@Composable
private fun HomeScreenPreview() {
    FlingoTheme {
        HomeScreen(
            userUiState = UserUiState(),
            bookUiState = BookUiState(
                books = arrayListOf(MockData.book)
            ),
            personalizationUiState = PersonalizationUiState(),
            onAction = {},
            onNavigate = {}
        )
    }
}