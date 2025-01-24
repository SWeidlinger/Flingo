package com.flingoapp.flingo.ui.screen.chapter

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flingoapp.flingo.data.models.book.Chapter
import com.flingoapp.flingo.data.models.book.page.Page
import com.flingoapp.flingo.data.models.book.page.PageDetails
import com.flingoapp.flingo.data.models.book.page.PageType
import com.flingoapp.flingo.ui.component.challengeContent.OrderStoryChallengeContent
import com.flingoapp.flingo.ui.component.challengeContent.QuizChallengeContent
import com.flingoapp.flingo.ui.component.challengeContent.RemoveWordChallengeContent
import com.flingoapp.flingo.ui.component.common.pageIndicator.CustomChallengePageIndicator
import com.flingoapp.flingo.ui.component.common.topbar.CustomChallengeTopBar
import com.flingoapp.flingo.ui.navigation.NavigationIntent
import com.flingoapp.flingo.ui.theme.FlingoTheme
import com.flingoapp.flingo.ui.toDp
import com.flingoapp.flingo.viewmodels.main.MainIntent

/**
 * Challenge screen used to display the different kind of challenges
 *
 * @param onAction
 * @param onNavigate
 * @receiver
 * @receiver
 */
@Composable
fun ChallengeChapterContent(
    chapter: Chapter,
    pages: List<Page>,
    onAction: (MainIntent) -> Unit,
    onNavigate: (NavigationIntent) -> Unit
) {
    val completedPages = remember { mutableStateListOf<Page>() }

    val pagerState = rememberPagerState { pages.size }
    val currentPage by remember {
        derivedStateOf {
            pages[pagerState.currentPage]
        }
    }

    var taskDefinitionWidth by remember {
        mutableIntStateOf(0)
    }

    val chapterCompleted by remember {
        derivedStateOf { completedPages.size == pages.size }
    }

    LaunchedEffect(chapterCompleted) {
        if (chapterCompleted) {
//            onAction(MainIntent.OnCurrentChapterComplete)
            chapter.isCompleted = true
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
                CustomChallengePageIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .width(taskDefinitionWidth.toDp())
                        .padding(bottom = 8.dp, top = 16.dp),
                    pagerState = pagerState,
                    showPageControlButtons = currentPage.type == PageType.QUIZ
                )
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

                //TODO: add possibility to save the state of a challenge state, and what got answered
                // so if user goes back to a previous page, the state is saved
                when (pageInPager.type) {
                    PageType.REMOVE_WORD -> {
                        RemoveWordChallengeContent(
                            modifier = Modifier.fillMaxSize(),
                            onNavigate = onNavigate,
                            pageDetails = pageInPager.details as PageDetails.RemoveWordPageDetails,
                            onAction = onAction,
                            onPageCompleted = { score ->
                                currentPage.isCompleted = true
                                completedPages.add(currentPage)
                            }
                        )
                    }

                    PageType.QUIZ -> {
                        QuizChallengeContent(
                            modifier = Modifier.fillMaxSize(),
                            onNavigate = onNavigate,
                            pageDetails = pageInPager.details as PageDetails.QuizPageDetails,
                            taskDefinitionTopBarWidth = taskDefinitionWidth.toDp(),
                            onAction = onAction,
                            onPageCompleted = { pageScore ->
                                currentPage.isCompleted = true
                                completedPages.add(currentPage)
                            }
                        )
                    }

                    PageType.ORDER_STORY -> {
                        OrderStoryChallengeContent(
                            modifier = Modifier.fillMaxSize(),
                            onNavigate = onNavigate,
                            pageDetails = pageInPager.details as PageDetails.OrderStoryPageDetails,
                            onAction = onAction,
                            onPageCompleted = { pageScore ->
                                currentPage.isCompleted = true
                                completedPages.add(currentPage)
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

@Preview
@Composable
private fun ChallengeScreenPreview() {
    FlingoTheme {

    }
}