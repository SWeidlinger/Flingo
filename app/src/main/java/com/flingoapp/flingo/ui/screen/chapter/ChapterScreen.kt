package com.flingoapp.flingo.ui.screen.chapter

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.flingoapp.flingo.data.models.book.Chapter
import com.flingoapp.flingo.data.models.book.ChapterType
import com.flingoapp.flingo.ui.navigation.NavigationIntent
import com.flingoapp.flingo.viewmodels.main.MainIntent
import com.flingoapp.flingo.viewmodels.main.MainUiState

@Composable
fun ChapterScreen(
    mainUiState: MainUiState,
    chapter: Chapter?,
    onAction: (MainIntent) -> Unit,
    onNavigate: (NavigationIntent) -> Unit
) {
    if (chapter == null) {
        Log.e("Navigation", "Chapter (${mainUiState.currentChapterId}) not found!")
        Text(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center),
            text = "Chapter not found!"
        )
    } else {
        val pagesInChapter = chapter.pages
        if (pagesInChapter == null) {
            Log.e("Navigation", "No pages in chapter ${chapter.title}!")
            Text(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                text = "This chapter contains no pages!"
            )
        } else {

            when (chapter.type) {
                ChapterType.CHALLENGE -> {
                    ChallengeChapterContent(
                        chapter = chapter,
                        pages = pagesInChapter,
                        currentLives = mainUiState.userData?.currentLives ?: 0,
                        onAction = onAction,
                        onNavigate = onNavigate
                    )
                }

                ChapterType.READ -> {
                    ReadChapterContent(
                        chapter = chapter,
                        pages = pagesInChapter,
                        onNavigate = onNavigate
                    )
                }

                ChapterType.MIXED -> {
                    //TODO: not yet implemented, might not be needed
                }
            }
        }
    }
}