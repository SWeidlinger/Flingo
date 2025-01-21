package com.flingoapp.flingo.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flingoapp.flingo.data.models.book.page.Page
import com.flingoapp.flingo.data.models.book.page.PageDetails
import com.flingoapp.flingo.data.models.book.page.PageType
import com.flingoapp.flingo.ui.components.challengeContent.OrderStoryChallengeContent
import com.flingoapp.flingo.ui.components.challengeContent.QuizChallengeContent
import com.flingoapp.flingo.ui.components.challengeContent.RemoveWordChallengeContent
import com.flingoapp.flingo.ui.components.common.CustomPageIndicator
import com.flingoapp.flingo.ui.components.common.button.CustomIconButton
import com.flingoapp.flingo.ui.components.common.topbar.CustomChallengeTopBar
import com.flingoapp.flingo.ui.navigation.NavigationIntent
import com.flingoapp.flingo.ui.pxToDp
import com.flingoapp.flingo.ui.theme.FlingoColors
import com.flingoapp.flingo.ui.theme.FlingoTheme
import com.flingoapp.flingo.viewmodels.main.MainIntent
import com.flingoapp.flingo.viewmodels.main.MainUiState
import kotlinx.coroutines.launch

/**
 * Challenge screen used to display the different kind of challenges
 *
 * @param mainUiState
 * @param onAction
 * @param onNavigate
 * @receiver
 * @receiver
 */
@Composable
fun ChallengeScreen(
    mainUiState: MainUiState,
    onAction: (MainIntent) -> Unit,
    onNavigate: (NavigationIntent) -> Unit
) {
    val pages = mainUiState.currentChapter?.pages?.toMutableStateList()
    val completedPages = remember { mutableListOf<Page>() }

    if (pages == null) {
        Text(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center),
            text = "No pages for chapter ${mainUiState.currentChapter?.title}"
        )
    } else {
        val pagerState = rememberPagerState { pages.size }
        val currentPage by remember {
            derivedStateOf {
                pages[pagerState.currentPage]
            }
        }

        var taskDefinitionWidth by remember {
            mutableIntStateOf(0)
        }

        //TODO: does not work right now
        val chapterCompleted by remember {
            derivedStateOf {
                pages.count { !it.isCompleted } == 0
            }
        }

        LaunchedEffect(chapterCompleted) {
            if (chapterCompleted) {
                Log.e("ChallengeScreen", "All pages completed")
                mainUiState.currentChapter.isCompleted = true
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CustomChallengeTopBar(
                    taskDefinition = currentPage.taskDefinition,
                    hint = currentPage.hint,
                    navigateUp = { onNavigate(NavigationIntent.Up()) },
                    taskDefinitionWidth = { width ->
                        taskDefinitionWidth = width
                    }
                )
            },
            bottomBar = {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .width(taskDefinitionWidth.pxToDp())
                            .padding(bottom = 8.dp, top = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val coroutineScope = rememberCoroutineScope()

                        if (pages.size > 1) {
                            if (currentPage.type == PageType.QUIZ) {
                                val isFirstPage by remember {
                                    derivedStateOf { pagerState.currentPage == 0 }
                                }

                                CustomIconButton(
                                    modifier = Modifier,
                                    shape = RoundedCornerShape(24.dp),
                                    icon = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                                    iconContentDescription = "Previous Question",
                                    backgroundColor = if (isFirstPage) Color.LightGray else FlingoColors.Primary,
                                    enabled = !isFirstPage
                                ) {
                                    if (isFirstPage) return@CustomIconButton
                                    val previousPage = pagerState.currentPage - 1
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(previousPage)
                                    }
                                }
                            }

                            CustomPageIndicator(
                                modifier = Modifier,
                                pagerState = pagerState
                            )

                            if (currentPage.type == PageType.QUIZ) {
                                val isLastPage by remember {
                                    derivedStateOf { pagerState.currentPage + 1 == pagerState.pageCount }
                                }

                                CustomIconButton(
                                    modifier = Modifier,
                                    shape = RoundedCornerShape(24.dp),
                                    icon = Icons.AutoMirrored.Default.KeyboardArrowRight,
                                    iconContentDescription = "Next Question",
                                    backgroundColor = if (isLastPage) Color.LightGray else FlingoColors.Primary,
                                    enabled = !isLastPage
                                ) {
                                    if (isLastPage) return@CustomIconButton
                                    val nextPage = pagerState.currentPage + 1
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(nextPage)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                HorizontalPager(
                    modifier = Modifier.fillMaxSize(),
                    state = pagerState
                ) { pageIndex ->
                    val pageInPager = pages[pageIndex]

                    when (pageInPager.type) {
                        PageType.REMOVE_WORD -> {
                            RemoveWordChallengeContent(
                                modifier = Modifier.fillMaxSize(),
                                mainUiState = mainUiState,
                                onNavigate = onNavigate,
                                pageDetails = pageInPager.details as PageDetails.RemoveWordPageDetails,
                                onPageCompleted = { score ->
                                    pages[pagerState.currentPage].isCompleted = true
                                }
                            )
                        }

                        PageType.QUIZ -> {
                            QuizChallengeContent(
                                modifier = Modifier.fillMaxSize(),
                                mainUiState = mainUiState,
                                onNavigate = onNavigate,
                                pageDetails = pageInPager.details as PageDetails.QuizPageDetails,
                                taskDefinitionTopBarWidth = taskDefinitionWidth.pxToDp(),
                                onAction = onAction,
                                onPageCompleted = { pageScore ->
                                    pages[pagerState.currentPage].isCompleted = true
                                    Log.e(
                                        "ChallengeScreen", "onPageCompleted ${
                                            pages[pagerState.currentPage].id
                                        }"
                                    )
                                }
                            )
                        }

                        PageType.ORDER_STORY -> {
                            OrderStoryChallengeContent(
                                modifier = Modifier.fillMaxSize(),
                                mainUiState = mainUiState,
                                onNavigate = onNavigate,
                                pageDetails = pageInPager.details as PageDetails.OrderStoryPageDetails,
                                onAction = onAction,
                                onPageCompleted = { pageScore ->
                                    pages[pagerState.currentPage].isCompleted = true
                                }
                            )
                        }

                        else -> {
                            Log.e("ChallengeScreen", "Invalid PageType")
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ChallengeScreenPreview() {
    FlingoTheme {

    }
}