package com.flingoapp.flingo.ui.chapter

import PageDetails
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import coil3.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.flingoapp.flingo.R
import com.flingoapp.flingo.data.model.Chapter
import com.flingoapp.flingo.data.model.MockData
import com.flingoapp.flingo.data.model.page.Page
import com.flingoapp.flingo.navigation.NavigationAction
import com.flingoapp.flingo.ui.CustomPreview
import com.flingoapp.flingo.ui.component.CustomHighlightedText
import com.flingoapp.flingo.ui.component.button.CustomElevatedButton2
import com.flingoapp.flingo.ui.component.topbar.CustomTopBar
import com.flingoapp.flingo.ui.theme.FlingoColors
import com.flingoapp.flingo.ui.theme.FlingoTheme
import com.flingoapp.flingo.ui.toDp
import com.flingoapp.flingo.viewmodel.MainAction
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

/**
 * Read screen used to display chapters with the [com.flingoapp.flingo.data.model.ChapterType.READ] type
 *
 * @param onNavigate
 * @receiver
 * @receiver
 */
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
    val wordIndexList = remember {
        mutableStateListOf<Int>().apply {
            repeat(safePages.size) { add(0) }
        }
    }
    val wordCountList = remember {
        mutableStateListOf<Int>().apply {
            repeat(safePages.size) { add(safePages[it].content.split(" ").size) }
        }
    }

    //TODO: remove
    LaunchedEffect(pagerState.currentPage) {
        onAction(MainAction.BookAction.SelectPage(pagerState.currentPage))
    }

    Scaffold(topBar = {
        CustomTopBar(
            title = chapter.title,
            navigateUp = { onNavigate(NavigationAction.Up()) },
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
                VerticalPager(
                    state = pagerState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    userScrollEnabled = true,
                    contentPadding = PaddingValues(
                        start = 32.dp, end = 32.dp,
                        top = (rowHeight / 3).toDp(),
                    ),
                    horizontalAlignment = Alignment.Start,
                    pageSize = PageSize.Fill
                ) { pageIndex ->
                    val content = safePages[pageIndex].content.split(" ")
                    val pageOffset =
                        ((pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction).absoluteValue

                    CustomHighlightedText(
                        modifier = Modifier.graphicsLayer {
                            val alphaValue = lerp(
                                start = 0f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )
                            alpha = alphaValue
                        },
                        content = content,
                        currentWordIndex = wordIndexList[pageIndex],
                        enabled = pageIndex == pagerState.currentPage,
                        textStyle = MaterialTheme.typography.headlineLarge.copy(fontSize = 32.sp)
                    )
                }

                AnimatedContent(
                    modifier = Modifier
                        .weight(0.75f)
                        .fillMaxHeight()
                        .padding(top = 32.dp, bottom = 128.dp)
                        .align(Alignment.CenterVertically)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            wordIndexList[pagerState.currentPage] += 1
                            if (wordIndexList[pagerState.currentPage] >= wordCountList[pagerState.currentPage]) {
                                if (pagerState.currentPage == pagerState.pageCount - 1) {
                                    onAction(MainAction.BookAction.CompleteChapter)
                                    onNavigate(NavigationAction.Up())
                                } else {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                }
                            }
                        },
                    targetState = safePages[pagerState.currentPage].imageUrl,
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

                        LottieAnimation(
                            composition = composition,
                            iterations = LottieConstants.IterateForever
                        )
                    } else {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "additional image",
                            onError = { imageLoadingFailed = true },
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            endY = 80f,
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
                    .padding(top = 64.dp, bottom = 16.dp)
            ) {
                ReadingProgressBar(
                    modifier = Modifier.fillMaxWidth(),
                    pageCount = safePages.size,
                    pagerState = pagerState,
                    wordIndexList = wordIndexList,
                    wordCountList = wordCountList
                )
            }
        }
    }
}

@Composable
fun ReadingProgressBar(
    modifier: Modifier = Modifier,
    pageCount: Int,
    pagerState: PagerState,
    wordIndexList: List<Int>,
    wordCountList: List<Int>
) {
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        repeat(pageCount) { index ->
            val progressAnimation by animateFloatAsState(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                targetValue = (wordIndexList[index].toFloat() / wordCountList[index])
            )

            ReadingProgressBarSection(
                modifier = modifier.weight(1f),
                isPressed = pagerState.targetPage == index,
                progress = progressAnimation,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
    }
}

@Composable
fun ReadingProgressBarSection(
    modifier: Modifier = Modifier,
    isPressed: Boolean,
    progress: Float,
    onClick: () -> Unit
) {
    CustomElevatedButton2(
        modifier = modifier.height(40.dp),
        buttonAlignment = Alignment.CenterStart,
        elevation = 6.dp,
        shape = RoundedCornerShape(50),
        onClick = onClick,
        isPressed = isPressed,
        backgroundColor = if (progress > 0.95f) FlingoColors.Success else FlingoColors.LightGray,
        buttonContent = {
            Box(
                modifier = Modifier
                    .height(40.dp)
                    .background(
                        FlingoColors.Success,
                        shape = RoundedCornerShape(
                            topEndPercent = 50,
                            bottomEndPercent = 50
                        )
                    )
                    .fillMaxWidth(progress)
            )
        }
    )
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