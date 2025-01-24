package com.flingoapp.flingo.ui.screen

import androidx.compose.foundation.layout.Arrangement
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
import com.flingoapp.flingo.ui.CustomPreview
import com.flingoapp.flingo.ui.component.BookItem
import com.flingoapp.flingo.ui.component.common.topbar.CustomHomeScreenTopBar
import com.flingoapp.flingo.ui.navigation.NavigationDestination
import com.flingoapp.flingo.ui.navigation.NavigationIntent
import com.flingoapp.flingo.ui.theme.FlingoTheme
import com.flingoapp.flingo.viewmodels.main.MainIntent
import com.flingoapp.flingo.viewmodels.main.MainUiState
import kotlin.math.absoluteValue

/**
 * Home screen
 *
 * @param mainUiState
 * @param onAction
 * @param onNavigate
 */
@Composable
fun HomeScreen(
    mainUiState: MainUiState,
    onAction: (MainIntent) -> Unit,
    onNavigate: (NavigationIntent) -> Unit
) {
    val TAG = "HomeScreen"

    val userBooks = mainUiState.userData?.books ?: emptyList()
    val userName = mainUiState.userData?.name ?: "User"
    val currentStreak = mainUiState.userData?.currentReadingStreak ?: 0

    val pagerState = rememberPagerState(pageCount = { userBooks.size })

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    Scaffold(topBar = {
        CustomHomeScreenTopBar(
            userName = userName,
            currentStreak = currentStreak,
            onUserClick = {
                onNavigate(NavigationIntent.Screen(NavigationDestination.InterestSelection))
            },
            onSettingsClick = {
                //TODO: implement
            },
            onAwardClick = {
                //TODO: implement
            }
        )
    }) { innerPadding ->
        if (userBooks.isEmpty()) {
            Text(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                text = "User has no books!",
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
                        currentBookItem = userBooks[bookIndex],
                        onClick = {
                            onNavigate(
                                NavigationIntent.Screen(
                                    destination = NavigationDestination.ChapterSelection(bookIndex = bookIndex)
                                )
                            )
                        }
                    )
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
            mainUiState = MainUiState(),
            onAction = {},
            onNavigate = {}
        )
    }
}