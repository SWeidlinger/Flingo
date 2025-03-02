package com.flingoapp.flingo.ui.chapter

import PageDetails
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import coil3.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import com.flingoapp.flingo.R
import com.flingoapp.flingo.data.model.Chapter
import com.flingoapp.flingo.data.model.MockData
import com.flingoapp.flingo.data.model.page.Page
import com.flingoapp.flingo.decodeBase64ToImage
import com.flingoapp.flingo.navigation.NavigationAction
import com.flingoapp.flingo.navigation.NavigationDestination
import com.flingoapp.flingo.ui.CustomPreview
import com.flingoapp.flingo.ui.component.CustomHighlightedText
import com.flingoapp.flingo.ui.component.topbar.CustomReadingTopBar
import com.flingoapp.flingo.ui.theme.FlingoTheme
import com.flingoapp.flingo.viewmodel.MainAction
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.math.absoluteValue


/**
 * Read screen used to display chapters with the [com.flingoapp.flingo.data.model.ChapterType.READ] type
 *
 * @param onNavigate
 * @receiver
 * @receiver
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalEncodingApi::class)
@Composable
fun ReadChapterContent(
    chapter: Chapter,
    pages: List<Page>,
    onAction: (MainAction) -> Unit,
    onNavigate: (NavigationAction) -> Unit
) {
    val pagerState = rememberPagerState { pages.size }

    val safePages = pages.map { it.details as PageDetails.Read }

    val coroutineScope = rememberCoroutineScope()
    val wordIndexList = remember(safePages.size) {
        mutableStateListOf<Int>().apply {
            repeat(safePages.size) { add(0) }
        }
    }
    val wordCountList = remember(safePages.size) {
        mutableStateListOf<Int>().apply {
            repeat(safePages.size) { add(safePages[it].content.split(" ").size) }
        }
    }

    val readingTextScrollState = rememberScrollState()

    //TODO: remove
    LaunchedEffect(pagerState.currentPage) {
        onAction(MainAction.BookAction.SelectPage(pagerState.currentPage))
        readingTextScrollState.animateScrollTo(0)
    }

    val completedPages = remember { mutableStateListOf<Int>() }
    LaunchedEffect(completedPages.size) {
        if (completedPages.size == safePages.size) {
            onNavigate(NavigationAction.Screen(NavigationDestination.ChallengeFinished))
        }
    }

    var showOriginalContent by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CustomReadingTopBar(
                title = chapter.title,
                navigateUp = { onNavigate(NavigationAction.Up()) },
                pageCount = safePages.size,
                pagerState = pagerState,
                wordIndexList = wordIndexList,
                wordCountList = wordCountList,
                onSettingsClick = {},
                onAwardClick = {
                    //TODO: remove after testing
                    onAction(MainAction.BookAction.CompleteChapter)
                    onNavigate(NavigationAction.Up())
                },
                onSettingsLongClick = {
                    onAction(MainAction.PersonalizationAction.ToggleDebugMode)
                }
            )
        }) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 32.dp)
        ) {
            var rowHeight by remember { mutableIntStateOf(0) }

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned {
                        rowHeight = it.size.height
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    userScrollEnabled = true,
                    contentPadding = PaddingValues(
                        start = 32.dp,
                        end = 32.dp,
                        top = 32.dp,
                        bottom = 16.dp
                    ),
                    verticalAlignment = Alignment.Top,
                    pageSize = PageSize.Fill,
                    pageSpacing = 16.dp
                ) { pageIndex ->
                    val content = safePages[pageIndex].content.split(" ")
                    val originalContent = safePages[pageIndex].originalContent.split(" ")
                    val pageOffset =
                        ((pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction).absoluteValue

                    CustomHighlightedText(
                        modifier = Modifier
                            .verticalScroll(readingTextScrollState)
                            .graphicsLayer {
                                val alphaValue = lerp(
                                    start = 0f,
                                    stop = 1f,
                                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                )
                                alpha = alphaValue
                            },
                        content = if (showOriginalContent) originalContent else content,
                        currentWordIndex = wordIndexList[pageIndex],
                        enabled = pageIndex == pagerState.currentPage,
                        textStyle = MaterialTheme.typography.headlineLarge.copy(fontSize = 32.sp)
                    )
                }

                AnimatedContent(
                    modifier = Modifier
                        .weight(0.75f)
                        .fillMaxHeight()
                        .padding(top = 32.dp, bottom = 32.dp)
                        .align(Alignment.CenterVertically)
                        .combinedClickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                wordIndexList[pagerState.currentPage] += 1
                                if (wordIndexList[pagerState.currentPage] >= wordCountList[pagerState.currentPage]) {
                                    completedPages.add(pagerState.currentPage)
                                    if (pagerState.currentPage < pagerState.pageCount) {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                        }
                                    }
                                }
                            },
                            onLongClick = {
                                showOriginalContent = !showOriginalContent
                            }
                        ),
                    targetState = safePages[pagerState.currentPage].imageData,
                    transitionSpec = {
                        //TODO: improve
                        ContentTransform(
                            targetContentEnter = fadeIn(),
                            initialContentExit = fadeOut()
                        )
                    },
                    contentAlignment = Alignment.Center
                ) { imageUrl ->
                    var imageLoadingFailed by remember(imageUrl) { mutableStateOf(false) }

                    if (imageLoadingFailed) {
                        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animation_eyes_loading))
                        val animatable = rememberLottieAnimatable()

                        LaunchedEffect(composition) {
                            // Wait until the composition is loaded.
                            composition?.let {
                                while (true) {
                                    // Animate one full cycle.
                                    animatable.animate(
                                        composition = it,
                                        iterations = 1
                                    )
                                    // Pause for 3 seconds at the beginning of the next cycle.
                                    delay(3000)
                                }
                            }
                        }

                        // Render the animation using the progress from the animatable.
                        LottieAnimation(
                            composition = composition,
                            progress = { animatable.progress }
                        )
                    } else {
                        if (safePages[pagerState.currentPage].isFromVertexAi) {
                            //special handling for vertex ai images
                            val bitmap = decodeBase64ToImage(imageUrl)
                            if (bitmap == null) {
                                imageLoadingFailed = true
                            } else {
                                Image(
                                    bitmap = decodeBase64ToImage(imageUrl)!!,
                                    contentDescription = "additional image",
                                )
                            }
                        } else {
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = "additional image",
                                onError = { imageLoadingFailed = true },
                            )
                        }
                    }
                }
            }
        }
    }
}

@CustomPreview
@Composable
private fun ReadScreenPreview() {
    FlingoTheme {
        ReadChapterContent(
            chapter = MockData.chapter,
            pages = MockData.chapter.copy(
                pages =
                listOf(
                    MockData.page.copy(id = "1", details = MockData.pageDetailsRead),
                    MockData.page.copy(id = "2", details = MockData.pageDetailsRead),
                    MockData.page.copy(id = "3", details = MockData.pageDetailsRead)
                )
            ).pages!!,
            onAction = {},
            onNavigate = {}
        )
    }
}