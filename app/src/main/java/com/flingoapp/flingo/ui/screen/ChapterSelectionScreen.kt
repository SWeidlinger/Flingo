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
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
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
import com.flingoapp.flingo.ui.animatedBorder
import com.flingoapp.flingo.ui.component.button.CustomElevatedButton
import com.flingoapp.flingo.ui.component.button.CustomElevatedButton2
import com.flingoapp.flingo.ui.component.topbar.CustomTopBar
import com.flingoapp.flingo.ui.theme.FlingoColors
import com.flingoapp.flingo.ui.theme.FlingoTheme
import com.flingoapp.flingo.ui.toPx
import com.flingoapp.flingo.viewmodel.BookUiState
import com.flingoapp.flingo.viewmodel.MainAction
import com.flingoapp.flingo.viewmodel.PersonalizationUiState
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

@Composable
fun ChapterSelectionScreen(
    bookUiState: BookUiState,
    personalizationUiState: PersonalizationUiState,
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
                onNavigate = onNavigate,
                isGeneratingChapter = personalizationUiState.isLoading
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
    isGeneratingChapter: Boolean,
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
            var lazyRowHeight by remember { mutableIntStateOf(0) }
            val chapterButtonSize = 300
            val chapterButtonElevationOffset = 14.dp
            val chapterButtonElevationOffsetPixel = chapterButtonElevationOffset.toPx()
            val maxButtonOffset by
            remember { derivedStateOf { lazyRowHeight - ((chapterButtonSize + chapterButtonElevationOffsetPixel) * 2f) } }
            val chapterButtonCoordinateHashMap = remember { HashMap<Int, Offset>() }

            Log.e("LazyRowHeight", lazyRowHeight.toString())
            Log.e("MaxButtonOffset", maxButtonOffset.toString())

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
                            .offset {
                                val y = (maxButtonOffset * chapter.positionOffset).toInt()
                                IntOffset(0, y)
                            }
                            .onGloballyPositioned { layoutCoordinates ->
                                chapterButtonCoordinateHashMap[index] =
                                    layoutCoordinates.positionInParent()
                            }
                    ) {
                        CustomElevatedButton(
                            size = DpSize(chapterButtonSize.dp, chapterButtonSize.dp),
                            shape = CircleShape,
                            elevation = chapterButtonElevationOffset,
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
                                    NavigationAction.Screen(
                                        destination = NavigationDestination.Chapter(chapterIndex = index)
                                    )
                                )
                            },
                            buttonContent = {
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    val showButtonIcon = chapter.isCompleted || isChapterLocked

                                    if (showButtonIcon) {
                                        Icon(
                                            modifier = Modifier.size((chapterButtonSize / 1.75).dp),
                                            tint = if (chapter.type == ChapterType.CHALLENGE || chapter.isCompleted) Color.White
                                            else Color.Black,
                                            imageVector = if (chapter.isCompleted) Icons.Default.Check else Icons.Default.Lock,
                                            contentDescription = if (chapter.isCompleted) "Chapter finished" else "Chapter Locked"
                                        )
                                    }

                                    AutoResizableText(
                                        modifier = if (showButtonIcon) Modifier.padding(16.dp) else Modifier,
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

                item {
                    var buttonPosition by remember { mutableStateOf(Offset.Zero) }
                    var buttonSize by remember { mutableStateOf(IntSize.Zero) }

                    var previousChapterCount by remember { mutableIntStateOf(chapters.size) }

                    //TODO: fix confetti
                    if (chapters.size > previousChapterCount) {
                        KonfettiView(
                            modifier = Modifier
                                .fillMaxSize(),
                            parties = listOf(
                                Party(
                                    position = Position.Absolute(
                                        x = buttonPosition.x + buttonSize.width / 2,
                                        y = buttonPosition.y
                                    ),
                                    emitter = Emitter(
                                        duration = 1000,
                                        timeUnit = TimeUnit.MILLISECONDS
                                    ).perSecond(200),
                                    spread = 90,
                                    angle = -90
                                )
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .offset {
                                val y = (maxButtonOffset * 0.5).toInt()
                                IntOffset(0, y)
                            }
//                            .onGloballyPositioned { layoutCoordinates ->
//                                chapterButtonCoordinateHashMap[index] =
//                                    layoutCoordinates.positionInParent()
//                            }
                    ) {
                        CustomElevatedButton2(
                            modifier = Modifier
                                .size(
                                    DpSize(
                                        chapterButtonSize.dp,
                                        chapterButtonSize.dp
                                    )
                                )
                                .then(
                                    if (isGeneratingChapter) {
                                        Modifier.animatedBorder(
                                            strokeWidth = 3.dp,
                                            shape = CircleShape,
                                            durationMillis = 1500
                                        )
                                    } else {
                                        Modifier
                                    }
                                )
                                .onGloballyPositioned {
                                    buttonPosition = it.positionInParent()
                                    buttonSize = it.size
                                },
                            shape = CircleShape,
                            elevation = chapterButtonElevationOffset,
                            isPressed = isGeneratingChapter,
                            backgroundColor = FlingoColors.LightGray,
                            onClick = {
                                previousChapterCount = chapters.size
                                onAction(MainAction.PersonalizationAction.GenerateChapter)
                            },
                            buttonContent = {
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        modifier = Modifier.size((chapterButtonSize / 1.75).dp),
                                        tint = FlingoColors.Text,
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Generate new chapter"
                                    )

                                    AutoResizableText(
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .padding(horizontal = 24.dp),
                                        text = "Generate Chapter",
                                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 42.sp),
                                        color = FlingoColors.Text
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
            isGeneratingChapter = false,
            currentLives = 3,
            onAction = {},
            onNavigate = {}
        )
    }
}