package com.flingoapp.flingo.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flingoapp.flingo.data.models.MockData
import com.flingoapp.flingo.data.models.book.Book
import com.flingoapp.flingo.data.models.book.Chapter
import com.flingoapp.flingo.data.models.book.ChapterType
import com.flingoapp.flingo.ui.CustomPreview
import com.flingoapp.flingo.ui.component.common.button.CustomElevatedButton
import com.flingoapp.flingo.ui.component.common.topbar.CustomTopBar
import com.flingoapp.flingo.ui.navigation.NavigationDestination
import com.flingoapp.flingo.ui.navigation.NavigationIntent
import com.flingoapp.flingo.ui.theme.FlingoColors
import com.flingoapp.flingo.ui.theme.FlingoTheme
import com.flingoapp.flingo.viewmodels.main.MainIntent
import com.flingoapp.flingo.viewmodels.main.MainUiState

@Composable
fun ChapterSelectionScreen(
    mainUiState: MainUiState,
    book: Book?,
    onAction: (MainIntent) -> Unit,
    onNavigate: (NavigationIntent) -> Unit
) {
    if (book == null) {
        Log.e("Navigation", "Book (${mainUiState.currentBookId}) not found!")
        Text(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center),
            text = "Book not found!"
        )
    } else {
        if (book.chapters.isEmpty()) {
            Log.e("Navigation", "Book (${mainUiState.currentBookId}) no chapters found!")
            Text(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                text = "Chapters for this book not found!"
            )
        } else {
            ChapterSelectionContent(
                book = book,
                chapters = book.chapters,
                onAction = onAction,
                currentLives = mainUiState.userData?.currentLives ?: 0,
                onNavigate = onNavigate
            )
        }
    }
}

/**
 * Chapter selection screen used to select a chapter from a book
 *
 * @param onAction
 * @param onNavigate
 */
@Composable
private fun ChapterSelectionContent(
    book: Book,
    chapters: List<Chapter>,
    currentLives: Int,
    onAction: (MainIntent) -> Unit,
    onNavigate: (NavigationIntent) -> Unit
) {
    //TODO: remove after testing
    var unlockAll by remember { mutableStateOf(false) }
    var showPath by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = book.title,
                navigateUp = { onNavigate(NavigationIntent.Up()) },
                onAwardClick = {
                    unlockAll = !unlockAll
                },
                onSettingsClick = {
                    showPath = !showPath
                },
                showLives = true,
                currentLives = currentLives
            )
        }) { innerPadding ->
        if (chapters.isEmpty()) {
            Text(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                text = "No chapters found for book ${book.title}",
            )
        } else {
            var lazyRowHeight by remember { mutableStateOf(0.dp) }
            val chapterButtonSize = 300
            val maxButtonOffset = lazyRowHeight - (chapterButtonSize.dp * 2f)
            val chapterButtonCoordinateHashMap = remember { HashMap<Int, Offset>() }

            //TODO: fix paths not scrolling and add some variation to it not just straight line
            LazyRow(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .onGloballyPositioned { layoutCoordinates ->
                        lazyRowHeight = layoutCoordinates.size.height.dp
                    }
                    .drawBehind {
                        if (!showPath) {
                            return@drawBehind
                        }
                        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(50f, 25f), 0f)

                        for (i in 0 until chapterButtonCoordinateHashMap.size - 1) {
                            val firstButtonCoordinates = chapterButtonCoordinateHashMap[i]
                            val secondButtonCoordinates = chapterButtonCoordinateHashMap[i + 1]

                            if (firstButtonCoordinates == null || secondButtonCoordinates == null) return@drawBehind

                            val centerButtonCoordinate = Offset(
                                x = firstButtonCoordinates.x + (chapterButtonSize / 2),
                                y = firstButtonCoordinates.y + (chapterButtonSize / 2)
                            )
                            val centerSecondButtonCoordinate = Offset(
                                x = secondButtonCoordinates.x + (chapterButtonSize / 2),
                                y = secondButtonCoordinates.y + (chapterButtonSize / 2)
                            )

                            drawLine(
                                color = Color.Gray,
                                start = centerButtonCoordinate,
                                end = centerSecondButtonCoordinate,
                                pathEffect = pathEffect,
                                strokeWidth = 5f,
                                cap = StrokeCap.Round
                            )
                        }
                    },
                contentPadding = PaddingValues(20.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(125.dp)
            ) {
                itemsIndexed(
                    items = chapters,
                    key = { _, chapter -> chapter.id }
                ) { index, _ ->
                    val chapter = chapters[index]
                    val isChapterLocked by remember {
                        derivedStateOf {
                            if (index == 0 || unlockAll) false
                            else !chapters[index - 1].isCompleted
                        }
                    }

                    //Box used to not apply offset to the button, as it can mess up the layout
                    Box(
                        modifier = Modifier
                            .offset(y = maxButtonOffset * chapter.positionOffset)
                            .onGloballyPositioned { layoutCoordinates ->
                                chapterButtonCoordinateHashMap[index] =
                                    layoutCoordinates.positionInParent()
                            }
                    ) {
                        CustomElevatedButton(
                            size = DpSize(chapterButtonSize.dp, chapterButtonSize.dp),
                            shape = CircleShape,
                            elevation = 15.dp,
                            isPressed = chapter.isCompleted,
                            enabled = !isChapterLocked,
                            backgroundColor =
                            if (chapter.isCompleted) {
                                FlingoColors.Success
                            } else {
                                if (chapter.type == ChapterType.READ) FlingoColors.LightGray
                                else MaterialTheme.colorScheme.primary
                            },
                            onClick = {
                                onNavigate(
                                    NavigationIntent.Screen(
                                        destination = NavigationDestination.Chapter(chapterIndex = index)
                                    )
                                )
                            },
                            buttonContent = {
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    if (chapter.isCompleted) {
                                        Icon(
                                            modifier = Modifier
                                                .size((chapterButtonSize / 1.75).dp),
                                            tint = Color.White,
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Chapter finished"
                                        )
                                    } else if (isChapterLocked) {
                                        Icon(
                                            modifier = Modifier.size((chapterButtonSize / 1.75).dp),
                                            tint =
                                            if (chapter.type == ChapterType.CHALLENGE)
                                                Color.White
                                            else
                                                Color.Black,
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = "Chapter Locked"
                                        )
                                    }

                                    Text(
                                        text = chapter.title,
                                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 42.sp),
                                        color =
                                        if (chapter.isCompleted) {
                                            Color.White
                                        } else {
                                            if (chapter.type == ChapterType.CHALLENGE) Color.White
                                            else FlingoColors.Text
                                        }
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@CustomPreview
@Composable
private fun LevelSelectionScreenPreview() {
    FlingoTheme {
        ChapterSelectionContent(
            book = MockData.book,
            chapters = MockData.book.chapters,
            currentLives = 3,
            onAction = {},
            onNavigate = {}
        )
    }
}