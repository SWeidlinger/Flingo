package com.flingoapp.flingo.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
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
import com.flingoapp.flingo.R
import com.flingoapp.flingo.ui.components.common.CustomHighlightedText
import com.flingoapp.flingo.ui.components.common.CustomPageIndicator
import com.flingoapp.flingo.ui.components.common.topbar.CustomTopBar
import com.flingoapp.flingo.ui.navigation.NavigationIntent
import com.flingoapp.flingo.ui.theme.FlingoTheme
import com.flingoapp.flingo.viewmodels.main.MainIntent
import com.flingoapp.flingo.viewmodels.main.MainUiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReadScreen(
    mainUiState: MainUiState,
    onAction: (MainIntent) -> Unit,
    onNavigate: (NavigationIntent) -> Unit
) {
    val pages = mainUiState.currentChapter?.pages ?: return

    val pagerState = rememberPagerState { pages.size }

    val coroutineScope = rememberCoroutineScope()

    Scaffold(topBar = {
        CustomTopBar(
            title = mainUiState.currentChapter?.title ?: "Chapter Title",
            navigateUp = { onNavigate(NavigationIntent.NavigateUp()) },
            onSettingsClick = {},
            onAwardClick = {
                //TODO: remove after testing
                mainUiState.currentChapter.isCompleted = true
                onNavigate(NavigationIntent.NavigateUp())
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
                    .fillMaxWidth()
                    .weight(2f),
                userScrollEnabled = false
            ) { page ->
                val currentPage = pages[page]

                val content = currentPage.content.split(" ")
                var currentWordIndex by remember { mutableIntStateOf(0) }

                val image = when (currentPage.images?.get(0)) {
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
                        .padding(innerPadding)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                if (currentWordIndex < content.size - 1) {
                                    currentWordIndex += 1
                                } else if (pagerState.currentPage < pagerState.pageCount) {
                                    if (pagerState.currentPage == pagerState.pageCount - 1) {
                                        mainUiState.currentChapter.isCompleted = true
                                        onNavigate(NavigationIntent.NavigateUp())
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
                            .padding(top = 64.dp, start = 64.dp, end = 64.dp),
                        content = content,
                        currentWordIndex = currentWordIndex
                    )
                }
            }

            CustomPageIndicator(pagerState = pagerState)
        }
    }
}

@Preview
@Composable
private fun ReadScreenPreview() {
    FlingoTheme {

    }
}