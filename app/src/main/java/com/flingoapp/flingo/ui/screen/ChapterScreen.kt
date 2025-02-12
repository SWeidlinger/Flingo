package com.flingoapp.flingo.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.flingoapp.flingo.data.model.Chapter
import com.flingoapp.flingo.data.model.ChapterType
import com.flingoapp.flingo.navigation.NavigationAction
import com.flingoapp.flingo.ui.chapter.ChallengeChapterContent
import com.flingoapp.flingo.ui.chapter.ReadChapterContent
import com.flingoapp.flingo.viewmodel.BookUiState
import com.flingoapp.flingo.viewmodel.MainAction
import com.flingoapp.flingo.viewmodel.UserUiState

@Composable
fun ChapterScreen(
    bookUiState: BookUiState,
    userUiState: UserUiState,
    chapter: Chapter?,
    onAction: (MainAction) -> Unit,
    onNavigate: (NavigationAction) -> Unit
) {
    if (chapter == null) {
        Log.e("Navigation", "Chapter (${bookUiState.currentChapterId}) not found!")
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
                        currentLives = userUiState.currentLives,
                        onAction = onAction,
                        onNavigate = onNavigate
                    )
                }

                ChapterType.READ -> {
                    ReadChapterContent(
                        chapter = chapter,
                        pages = pagesInChapter,
                        onAction = onAction,
                        onNavigate = onNavigate,
                    )
                }

                ChapterType.MIXED -> {
                    //TODO: not yet implemented, might not be needed
                }
            }
        }
    }
}