package com.flingoapp.flingo.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import com.flingoapp.flingo.data.viewmodels.main.MainIntent
import com.flingoapp.flingo.data.viewmodels.main.MainUiState
import com.flingoapp.flingo.ui.CustomPreview
import com.flingoapp.flingo.ui.components.common.CustomElevatedButton
import com.flingoapp.flingo.ui.components.common.CustomTopBar
import com.flingoapp.flingo.ui.navigation.NavigationIntent
import com.flingoapp.flingo.ui.theme.FlingoTheme
import kotlin.random.Random

@Composable
fun LevelSelectionScreen(
    bookIndex: Int,
    mainUiState: MainUiState,
    onAction: (MainIntent) -> Unit,
    onNavigate: (NavigationIntent) -> Unit
) {
    val currentBook = mainUiState.userData?.books?.get(bookIndex)
    val levels = currentBook?.levels
    val levelButtonOffsetList = mutableListOf<Float>()
    levels?.size?.let {
        repeat(it) {
            levelButtonOffsetList.add(Random.nextFloat())
        }
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                title = currentBook?.title ?: "Book Title",
                navigateUp = { onNavigate(NavigationIntent.NavigateUp()) },
                onSettingsClick = {},
                onAwardClick = {}
            )
        }) { innerPadding ->
        if (levels.isNullOrEmpty()) {
            Text(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                text = "No levels found for book $bookIndex",
            )
        } else {
            var lazyRowHeight by remember { mutableStateOf(0.dp) }
            val levelButtonSize = 300
            val maxButtonOffset = lazyRowHeight - (levelButtonSize.dp * 2f)
            val levelButtonCoordinateHashMap = remember { HashMap<Int, Offset>() }

            //TODO: fix layout shifting, could be because of globally position being called more than once
            LazyRow(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .onGloballyPositioned { layoutCoordinates ->
                        lazyRowHeight = layoutCoordinates.size.height.dp
                    }
                    .drawBehind {
                        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(50f, 25f), 0f)

                        for (i in 0 until levelButtonCoordinateHashMap.size - 1) {
                            val firstButtonCoordinates = levelButtonCoordinateHashMap[i]
                            val secondButtonCoordinates = levelButtonCoordinateHashMap[i + 1]

                            if (firstButtonCoordinates == null || secondButtonCoordinates == null) return@drawBehind

                            val centerButtonCoordinate = Offset(
                                x = firstButtonCoordinates.x + (levelButtonSize / 2),
                                y = firstButtonCoordinates.y + (levelButtonSize / 2)
                            )
                            val centerSecondButtonCoordinate = Offset(
                                x = secondButtonCoordinates.x + (levelButtonSize / 2),
                                y = secondButtonCoordinates.y + (levelButtonSize / 2)
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
                itemsIndexed(levels) { index, level ->
                    CustomElevatedButton(
                        modifier = Modifier
                            .size(levelButtonSize.dp)
                            .offset(y = maxButtonOffset * levelButtonOffsetList[index])
                            .onGloballyPositioned { layoutCoordinates ->
                                Log.e(
                                    "LEVELSELECTION",
                                    "Button coordinates $index: ${layoutCoordinates.positionInParent()}"
                                )
                                levelButtonCoordinateHashMap[index] = layoutCoordinates.positionInParent()
                            },
                        shape = CircleShape,
                        elevation = 15.dp,
                        color = Color.LightGray,
                        onClick = { /*TODO*/ },
                        buttonContent = {
                            Text(
                                text = level.title,
                                style = MaterialTheme.typography.headlineLarge
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
        LevelSelectionScreen(
            bookIndex = 2,
            mainUiState = MainUiState(),
            onAction = {},
            onNavigate = {}
        )
    }
}