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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.flingoapp.flingo.data.model.MockData
import com.flingoapp.flingo.navigation.NavigationDestination
import com.flingoapp.flingo.navigation.NavigationIntent
import com.flingoapp.flingo.ui.CustomPreview
import com.flingoapp.flingo.ui.animatedBorder
import com.flingoapp.flingo.ui.component.BookItem
import com.flingoapp.flingo.ui.component.button.CustomElevatedTextButton2
import com.flingoapp.flingo.ui.component.topbar.CustomHomeScreenTopBar
import com.flingoapp.flingo.ui.theme.FlingoColors
import com.flingoapp.flingo.ui.theme.FlingoTheme
import com.flingoapp.flingo.viewmodel.BookUiState
import com.flingoapp.flingo.viewmodel.MainAction
import com.flingoapp.flingo.viewmodel.PersonalizationUiState
import com.flingoapp.flingo.viewmodel.UserUiState
import kotlinx.coroutines.delay
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit
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
    onNavigate: (NavigationIntent) -> Unit
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
                    onNavigate(NavigationIntent.Screen(NavigationDestination.InterestSelection))
                },
                onStreakClick = {
                    onNavigate(NavigationIntent.Screen(NavigationDestination.StreakAndStars))
                },
                onSettingsClick = {
                    //TODO: implement
                },
                onAwardClick = {
                    //TODO: implement
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
                                    NavigationIntent.Screen(
                                        destination = NavigationDestination.ChapterSelection(
                                            bookIndex = bookIndex
                                        )
                                    )
                                )
                            }
                        )
                    }
                }

                var buttonPosition by remember { mutableStateOf(Offset.Zero) }
                var buttonSize by remember { mutableStateOf(IntSize.Zero) }

                var generateBookButtonPressed by remember { mutableStateOf(false) }

                if (generateBookButtonPressed && personalizationUiState.isSuccess && !bookUiState.isError) {
                    //TODO: simplify logic
                    LaunchedEffect(Unit) {
                        delay(2500)
                        generateBookButtonPressed = false
                    }
                    KonfettiView(
                        modifier = Modifier
                            .fillMaxSize(),
                        parties = listOf(
                            Party(
                                position = Position.Absolute(
                                    x = buttonPosition.x + buttonSize.width / 2,
                                    y = buttonPosition.y
                                ),
                                emitter = Emitter(
                                    duration = 1000,
                                    timeUnit = TimeUnit.MILLISECONDS
                                ).perSecond(250),
                                spread = 90,
                                angle = -90
                            )
                        )
                    )
                }

                CustomElevatedTextButton2(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomEnd)
                        .then(
                            if (personalizationUiState.isLoading) {
                                Modifier.animatedBorder(
                                    strokeWidth = 3.dp,
                                    shape = CircleShape,
                                    durationMillis = 1500
                                )
                            } else {
                                Modifier
                            }
                        )
                        .onGloballyPositioned {
                            buttonPosition = it.positionOnScreen()
                            buttonSize = it.size
                        },
                    textModifier = Modifier.padding(horizontal = 8.dp),
                    fontSize = 24.sp,
                    shape = CircleShape,
                    elevation = 6.dp,
                    isPressed = personalizationUiState.isLoading,
                    backgroundColor = if (bookUiState.isError) FlingoColors.Error else Color.White,
                    text = "Überrasch mich!",
                    onClick = {
                        generateBookButtonPressed = true
                        onAction(MainAction.PersonalizationAction.GenerateBook)
                    }
                )
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