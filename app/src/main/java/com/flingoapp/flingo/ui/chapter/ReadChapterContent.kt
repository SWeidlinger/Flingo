package com.flingoapp.flingo.ui.chapter

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flingoapp.flingo.R
import com.flingoapp.flingo.data.model.Chapter
import com.flingoapp.flingo.data.model.page.Page
import com.flingoapp.flingo.data.model.page.PageDetails
import com.flingoapp.flingo.navigation.NavigationAction
import com.flingoapp.flingo.ui.component.CustomHighlightedText
import com.flingoapp.flingo.ui.component.pageIndicator.CustomPageIndicator
import com.flingoapp.flingo.ui.component.topbar.CustomTopBar
import com.flingoapp.flingo.ui.theme.FlingoTheme
import com.flingoapp.flingo.viewmodel.MainAction
import kotlinx.coroutines.launch

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

    val coroutineScope = rememberCoroutineScope()

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(2f),
                userScrollEnabled = false
            ) { page ->
                val currentPage = pages[page]
                val details = currentPage.details as PageDetails.ReadPageDetails

                val content = details.content.split(" ")
                var currentWordIndex by remember { mutableIntStateOf(0) }

                val image = when (details.images[0]) {
                    "circus_image" -> {
                        painterResource(id = R.drawable.circus_image)
                    }

                    "pony" -> {
                        painterResource(id = R.drawable.pony)
                    }

                    "old_man" -> {
                        painterResource(id = R.drawable.old_man)
                    }

                    else -> {
                        painterResource(id = R.drawable.circus_image)
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        modifier = Modifier
                            .weight(2f)
                            .padding(top = 16.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                if (currentWordIndex < content.size - 1) {
                                    currentWordIndex += 1
                                } else if (pagerState.currentPage < pagerState.pageCount) {
                                    if (pagerState.currentPage == pagerState.pageCount - 1) {
                                        onAction(MainAction.BookAction.CompleteChapter)
                                        onNavigate(NavigationAction.Up())
                                    }

                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(page + 1)
                                    }
                                }
                            },
                        painter = image,
                        contentDescription = "Additional Image"
                    )

                    CustomHighlightedText(
                        modifier = Modifier
                            .weight(1f)
                            .padding(
                                top = 64.dp,
                                start = 32.dp,
                                end = 32.dp
                            ),
                        content = content,
                        currentWordIndex = currentWordIndex,
                        textStyle = MaterialTheme.typography.headlineLarge.copy(fontSize = 32.sp)
                    )
                }
            }

            CustomPageIndicator(
                pagerState = pagerState,
                modifier = Modifier.fillMaxWidth(),
                pages = pages
            )
        }
    }
}

@Preview
@Composable
private fun ReadScreenPreview() {
    FlingoTheme {

    }
}