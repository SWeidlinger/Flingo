package com.flingoapp.flingo.ui.chapter

import PageDetails
import PageDetailsType
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flingoapp.flingo.data.model.Chapter
import com.flingoapp.flingo.data.model.MockData
import com.flingoapp.flingo.data.model.page.Page
import com.flingoapp.flingo.navigation.NavigationAction
import com.flingoapp.flingo.navigation.NavigationDestination
import com.flingoapp.flingo.ui.CustomPreview
import com.flingoapp.flingo.ui.challenge.orderStory.OrderStoryChallengeContent
import com.flingoapp.flingo.ui.challenge.quiz.QuizChallengeContent
import com.flingoapp.flingo.ui.challenge.removeWord.RemoveWordChallengeContent
import com.flingoapp.flingo.ui.component.pageIndicator.CustomChallengePageIndicator
import com.flingoapp.flingo.ui.component.topbar.CustomChallengeTopBar
import com.flingoapp.flingo.ui.theme.FlingoTheme
import com.flingoapp.flingo.ui.toDp
import com.flingoapp.flingo.viewmodel.MainAction

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
    currentLives: Int,
    onAction: (MainAction) -> Unit,
    onNavigate: (NavigationAction) -> Unit
) {
    val completedPages = remember { mutableStateListOf<Page>() }

    val pagerState = rememberPagerState { pages.size }

    var taskDefinitionWidth by remember {
        mutableIntStateOf(0)
    }

    val chapterCompleted by remember {
        derivedStateOf { completedPages.size == pages.size }
    }

    var showReferenceText by remember { mutableStateOf(false) }

    LaunchedEffect(chapterCompleted) {
        if (chapterCompleted) {
            onAction(MainAction.BookAction.CompleteChapter)
        }
    }

    LaunchedEffect(currentLives) {
        if (currentLives <= 0) {
            onNavigate(NavigationAction.Screen(NavigationDestination.AllLivesLost))
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CustomChallengeTopBar(
                    taskDefinition = pages[pagerState.currentPage].taskDefinition,
                    hint = pages[pagerState.currentPage].hint,
                    navigateUp = { onNavigate(NavigationAction.Up()) },
                    currentLives = currentLives,
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
                        pages = pages,
                        showPageControlButtons = pages[pagerState.currentPage].details.type == PageDetailsType.QUIZ
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
                    when (pageInPager.details.type) {
                        PageDetailsType.REMOVE_WORD -> {
                            RemoveWordChallengeContent(
                                modifier = Modifier.fillMaxSize(),
                                onNavigate = onNavigate,
                                pageDetails = pageInPager.details as PageDetails.RemoveWord,
                                onAction = onAction,
                                pagerState = pagerState,
                                onPageCompleted = { score ->
                                    onAction(MainAction.BookAction.CompletePage(pageIndex))
                                    completedPages.add(pages[pagerState.currentPage])
                                }
                            )
                        }

                        PageDetailsType.QUIZ -> {
                            QuizChallengeContent(
                                modifier = Modifier.fillMaxSize(),
                                onNavigate = onNavigate,
                                pageDetails = pageInPager.details as PageDetails.Quiz,
                                taskDefinitionTopBarWidth = taskDefinitionWidth.toDp(),
                                onAction = onAction,
                                onPageCompleted = { pageScore ->
                                    onAction(MainAction.BookAction.CompletePage(pageIndex))
                                    completedPages.add(pages[pagerState.currentPage])
                                }
                            )
                        }

                        PageDetailsType.ORDER_STORY -> {
                            OrderStoryChallengeContent(
                                modifier = Modifier.fillMaxSize(),
                                onNavigate = onNavigate,
                                pageDetails = pageInPager.details as PageDetails.OrderStory,
                                onAction = onAction,
                                onPageCompleted = { pageScore ->
                                    onAction(MainAction.BookAction.CompletePage(pageIndex))
                                    completedPages.add(pages[pagerState.currentPage])
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

@CustomPreview
@Composable
private fun ChallengeScreenPreview() {
    FlingoTheme {
        ChallengeChapterContent(
            chapter = MockData.chapter,
            pages = MockData.chapter.pages!!,
            currentLives = 3,
            onAction = {},
            onNavigate = {}
        )
    }
}