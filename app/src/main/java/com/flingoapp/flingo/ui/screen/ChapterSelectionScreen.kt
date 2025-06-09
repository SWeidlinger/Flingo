package com.flingoapp.flingo.ui.screen

import PageDetailsSelectionEntry
import PageDetailsType
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flingoapp.flingo.data.model.Book
import com.flingoapp.flingo.data.model.Chapter
import com.flingoapp.flingo.data.model.ChapterType
import com.flingoapp.flingo.data.model.MockData
import com.flingoapp.flingo.navigation.NavigationAction
import com.flingoapp.flingo.navigation.NavigationDestination
import com.flingoapp.flingo.ui.AutoResizableText
import com.flingoapp.flingo.ui.CustomPreview
import com.flingoapp.flingo.ui.component.button.CustomElevatedButton
import com.flingoapp.flingo.ui.component.topbar.CustomTopBar
import com.flingoapp.flingo.ui.theme.FlingoColors
import com.flingoapp.flingo.ui.theme.FlingoTheme
import com.flingoapp.flingo.ui.toDp
import com.flingoapp.flingo.viewmodel.BookUiState
import com.flingoapp.flingo.viewmodel.MainAction

/**
 * Chapter selection screen
 *
 * @param bookUiState
 * @param currentLives
 * @param book
 * @param onAction
 * @param onNavigate
 * @receiver
 * @receiver
 */
@Composable
fun ChapterSelectionScreen(
    bookUiState: BookUiState,
    currentLives: Int,
    book: Book?,
    onAction: (MainAction) -> Unit,
    onNavigate: (NavigationAction) -> Unit
) {
    if (book == null) {
        Log.e("Navigation", "Book (${bookUiState.currentBookId}) not found!")
        Text(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center),
            text = "Book not found!"
        )
    } else {
        if (book.chapters.isEmpty()) {
            Log.e("Navigation", "Book (${bookUiState.currentBookId}) no chapters found!")
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
                currentLives = currentLives,
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
    onAction: (MainAction) -> Unit,
    onNavigate: (NavigationAction) -> Unit
) {
    //TODO: remove after testing
    var unlockAll by remember { mutableStateOf(false) }
    var showPath by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = book.title,
                navigateUp = { onNavigate(NavigationAction.Up()) },
                onAwardClick = {
                    unlockAll = !unlockAll
                },
                onSettingsClick = {
                    onNavigate(NavigationAction.Screen(NavigationDestination.Settings))
                },
                showLives = true,
                currentLives = currentLives,
                onSettingsLongClick = {
                    onAction(MainAction.PersonalizationAction.ToggleDebugMode)
                }
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
            val density = LocalDensity.current
            var lazyRowHeight by remember { mutableIntStateOf(0) }
            val chapterButtonSize = 300.dp
            val chapterButtonElevationOffset = 14.dp
            //+8.dp to add padding to the bottom of button to compensate for the elevation offset of elevated button
            val maxButtonOffset by
            remember { derivedStateOf { lazyRowHeight.toDp(density) - (chapterButtonSize + chapterButtonElevationOffset + 8.dp) } }
            val chapterButtonCoordinateHashMap = remember { HashMap<Int, Offset>() }

            //TODO: fix paths not scrolling and add some variation to it not just straight line
            LazyRow(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .onGloballyPositioned { layoutCoordinates ->
                        lazyRowHeight = layoutCoordinates.size.height
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
                                x = firstButtonCoordinates.x + (chapterButtonSize.toPx() / 2),
                                y = firstButtonCoordinates.y + (chapterButtonSize.toPx() / 2)
                            )
                            val centerSecondButtonCoordinate = Offset(
                                x = secondButtonCoordinates.x + (chapterButtonSize.toPx() / 2),
                                y = secondButtonCoordinates.y + (chapterButtonSize.toPx() / 2)
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

                    //TODO: removed for now, can be activated again if needed
//                    val isChapterLocked by remember {
//                        derivedStateOf {
//                            if (index == 0 || unlockAll) false
//                            else !chapters[index - 1].isCompleted
//                        }
//                    }
                    val isChapterLocked = false
                    val pageTypeInChapter = chapter.pages?.first()?.details?.type

                    //Box used to not apply offset to the button, as it can mess up the layout
                    Box(
                        modifier = Modifier
                            .animateItem()
                            .offset {
                                val y = (maxButtonOffset * chapter.positionOffset).toPx().toInt()
                                IntOffset(0, y)
                            }
                            .onGloballyPositioned { layoutCoordinates ->
                                chapterButtonCoordinateHashMap[index] =
                                    layoutCoordinates.positionInParent()
                            }
                    ) {
                        CustomElevatedButton(
                            size = DpSize(chapterButtonSize, chapterButtonSize),
                            shape = CircleShape,
                            elevation = chapterButtonElevationOffset,
                            isPressed = chapter.isCompleted,
                            enabled = !isChapterLocked,
                            backgroundColor =
                                if (chapter.isCompleted) {
                                    FlingoColors.Success
                                } else {
                                    when (chapter.type) {
                                        ChapterType.READ -> FlingoColors.LightGray
                                        else -> {
                                            when (pageTypeInChapter) {
                                                PageDetailsType.ORDER_STORY -> PageDetailsSelectionEntry.ORDER_STORY.backgroundColor
                                                PageDetailsType.REMOVE_WORD -> PageDetailsSelectionEntry.REMOVE_WORD.backgroundColor
                                                PageDetailsType.QUIZ -> PageDetailsSelectionEntry.QUIZ.backgroundColor
                                                else -> MaterialTheme.colorScheme.primary
                                            }
                                        }
                                    }
                                },
                            onClick = {
                                onNavigate(
                                    NavigationAction.Screen(
                                        destination = NavigationDestination.Chapter(chapterId = chapter.id)
                                    )
                                )
                            },
                            onLongClick = {
                                onAction(MainAction.PersonalizationAction.GenerateChapter(chapter))
                            },
                            buttonContent = {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    val showButtonIcon = chapter.isCompleted || isChapterLocked

                                    //TODO: refactor
                                    val pageDetailEntry =
                                        PageDetailsSelectionEntry.entries.find { it.pageType == pageTypeInChapter }
                                    if (chapter.type == ChapterType.CHALLENGE && pageDetailEntry != null) {
                                        Icon(
                                            modifier = Modifier.size((chapterButtonSize / 2)),
                                            tint = pageDetailEntry.iconTint,
                                            painter = painterResource(pageDetailEntry.iconRes),
                                            contentDescription = if (chapter.isCompleted) "Chapter finished" else "Chapter Locked"
                                        )

                                        Spacer(modifier = Modifier.padding(8.dp))
                                    } else if (showButtonIcon) {
                                        Icon(
                                            modifier = Modifier.size((chapterButtonSize / 2)),
                                            tint = if (chapter.type == ChapterType.CHALLENGE || chapter.isCompleted) Color.White
                                            else Color.Black,
                                            imageVector = if (chapter.isCompleted) Icons.Default.Check else Icons.Default.Lock,
                                            contentDescription = if (chapter.isCompleted) "Chapter finished" else "Chapter Locked"
                                        )

                                        Spacer(modifier = Modifier.padding(8.dp))
                                    }

                                    AutoResizableText(
                                        modifier = Modifier.padding(horizontal = 24.dp),
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
private fun ChapterSelectionScreenPreview() {
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