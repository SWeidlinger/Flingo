package com.flingoapp.flingo.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.flingoapp.flingo.ui.CustomPreview
import com.flingoapp.flingo.ui.components.BookItem
import com.flingoapp.flingo.ui.components.common.topbar.CustomHomeScreenTopBar
import com.flingoapp.flingo.ui.navigation.NavigationIntent
import com.flingoapp.flingo.ui.theme.FlingoTheme
import com.flingoapp.flingo.viewmodels.main.MainIntent
import com.flingoapp.flingo.viewmodels.main.MainUiState
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
 * @param mainUiState
 * @param onAction
 * @param onNavigate
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    mainUiState: MainUiState,
    onAction: (MainIntent) -> Unit,
    onNavigate: (NavigationIntent) -> Unit
) {
    val TAG = "HomeScreen"

    val userBooks = mainUiState.userData?.books ?: emptyList()

    val pagerState = rememberPagerState(pageCount = { userBooks.size })

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    var shootConfetti by remember { mutableStateOf(false) }

    Scaffold(topBar = {
        CustomHomeScreenTopBar(
            userName = mainUiState.userData?.name.toString(),
            onUserClick = {
                //TODO: remove
                shootConfetti = true
            },
            onSettingsClick = {},
            onAwardClick = {}
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

            HorizontalPager(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalAlignment = Alignment.CenterVertically,
                pageSpacing = -(pageSpacing * 1.05f),
                state = pagerState
            ) { bookIndex ->
                val pageOffset = (
                        (pagerState.currentPage - bookIndex) + pagerState.currentPageOffsetFraction).absoluteValue
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            alpha = lerp(
                                start = 0.5f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )
                            lerp(
                                start = 0.6f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            ).also { scale ->
                                scaleX = scale
                                scaleY = scale
                            }
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
                            //TODO: use once compose navigation side effect for weird pager scrolling behavior is fixed
//                        val levelSelection = NavigationDestination.LevelSelection(
//                            bookIndex = pageIndex
//                        )

                            onNavigate(
                                NavigationIntent.NavigateToChapterSelection(
                                    bookIndex = bookIndex
                                )
                            )
                        }
                    )
                }
            }

            //TODO: remove
            if (shootConfetti) {
                LaunchedEffect(key1 = shootConfetti) {
                    delay(3000)
                    shootConfetti = false
                }

                KonfettiView(
                    modifier = Modifier.fillMaxSize(),
                    parties = listOf(
                        Party(
                            position = Position.Relative(0.06, 0.065),
                            emitter = Emitter(duration = 1000, TimeUnit.MILLISECONDS).perSecond(500)
                        )
                    )
                )
            }
        }
    }
}

//TODO: create mock user for previews
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