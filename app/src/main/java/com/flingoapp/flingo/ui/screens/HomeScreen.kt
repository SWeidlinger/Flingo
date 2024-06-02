package com.flingoapp.flingo.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.flingoapp.flingo.data.viewmodels.main.MainIntent
import com.flingoapp.flingo.data.viewmodels.main.MainUiState
import com.flingoapp.flingo.ui.CustomPreview
import com.flingoapp.flingo.ui.components.BookItem
import com.flingoapp.flingo.ui.navigation.NavigationDestination
import com.flingoapp.flingo.ui.navigation.NavigationIntent
import com.flingoapp.flingo.ui.theme.FlingoTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    mainUiStateFlow: StateFlow<MainUiState>,
    onAction: (MainIntent) -> Unit,
    onNavigate: (NavigationIntent) -> Unit
) {
    val TAG = "HomeScreen"

    val mainUiState by mainUiStateFlow.collectAsState()
    val userBooks = mainUiState.userData?.books ?: emptyList()

    val pagerState = rememberPagerState(pageCount = { userBooks.size })

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

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
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            pageSpacing = -(pageSpacing * 1.05f),
            state = pagerState
        ) { pageIndex ->
            val pageOffset = (
                    (pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction).absoluteValue
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
                    bookIndex = pageIndex,
                    currentBookItem = userBooks[pageIndex],
                    onClick = {
                        //TODO: use once compose navigation side effect for weird pager scrolling behavior is fixed
//                        val levelSelection = NavigationDestination.LevelSelection(
//                            bookIndex = pageIndex
//                        )

                        onNavigate(
                            NavigationIntent.NavigateToLevelSelection(
                                levelSelection = NavigationDestination.LevelSelection,
                                bookIndex = pageIndex
                            )
                        )
                    }
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
            mainUiStateFlow = MutableStateFlow(MainUiState()),
            onAction = {},
            onNavigate = {}
        )
    }
}