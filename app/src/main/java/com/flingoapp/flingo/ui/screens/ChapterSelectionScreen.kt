package com.flingoapp.flingo.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.flingoapp.flingo.data.models.book.ChapterType
import com.flingoapp.flingo.ui.CustomPreview
import com.flingoapp.flingo.ui.components.common.button.CustomElevatedButton
import com.flingoapp.flingo.ui.components.common.topbar.CustomTopBar
import com.flingoapp.flingo.ui.navigation.NavigationIntent
import com.flingoapp.flingo.ui.theme.FlingoPrimary
import com.flingoapp.flingo.ui.theme.FlingoTheme
import com.flingoapp.flingo.viewmodels.main.MainIntent
import com.flingoapp.flingo.viewmodels.main.MainUiState
import kotlin.random.Random

@Composable
fun ChapterSelectionScreen(
    mainUiState: MainUiState,
    onAction: (MainIntent) -> Unit,
    onNavigate: (NavigationIntent) -> Unit
) {
    val chapters = remember { mainUiState.currentBook?.chapters }
    val chapterButtonOffsetList = remember { mutableListOf<Float>() }

    //TODO: replace with fixed offsets in JSON
    chapters?.size?.let {
        repeat(it) {
            chapterButtonOffsetList.add(Random.nextFloat())
        }
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = mainUiState.currentBook?.title ?: "Book Title",
                navigateUp = { onNavigate(NavigationIntent.NavigateUp()) },
                onSettingsClick = {},
                onAwardClick = {}
            )
        }) { innerPadding ->
        if (chapters.isNullOrEmpty()) {
            Text(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                text = "No chapters found for book ${mainUiState.currentBook?.title}",
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
                itemsIndexed(chapters) { index, chapter ->
                    CustomElevatedButton(
                        modifier = Modifier
                            .offset(y = maxButtonOffset * chapterButtonOffsetList[index])
                            .onGloballyPositioned { layoutCoordinates ->
                                Log.e(
                                    "CHAPTERSELECTION",
                                    "Button coordinates $index: ${layoutCoordinates.positionInParent()}"
                                )
                                chapterButtonCoordinateHashMap[index] = layoutCoordinates.positionInParent()
                            },
                        size = DpSize(chapterButtonSize.dp, chapterButtonSize.dp),
                        shape = CircleShape,
                        elevation = 15.dp,
                        backgroundColor = if (chapter.type == ChapterType.READ) Color.LightGray else FlingoPrimary,
                        onClick = {
                            onNavigate(NavigationIntent.NavigateToChapter(chapterIndex = index))
                        },
                        buttonContent = {
                            Text(
                                text = chapter.title,
                                style = MaterialTheme.typography.headlineLarge,
                                color = if (chapter.type == ChapterType.CHALLENGE) Color.White else Color.Black
                            )
                        }
                    )
                }
            }
        }
    }
}

@CustomPreview
@Composable
private fun LevelSelectionScreenPreview() {
    FlingoTheme {
        ChapterSelectionScreen(
            mainUiState = MainUiState(),
            onAction = {},
            onNavigate = {}
        )
    }
}