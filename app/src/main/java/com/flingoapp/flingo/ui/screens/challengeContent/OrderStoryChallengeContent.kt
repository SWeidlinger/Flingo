package com.flingoapp.flingo.ui.screens.challengeContent

import android.content.ClipData
import android.content.ClipDescription
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.flingoapp.flingo.data.models.book.page.PageDetails
import com.flingoapp.flingo.ui.components.common.PaperSnippet
import com.flingoapp.flingo.ui.components.common.button.CustomElevatedButton
import com.flingoapp.flingo.ui.lighten
import com.flingoapp.flingo.ui.navigation.NavigationIntent
import com.flingoapp.flingo.ui.theme.FlingoColors
import com.flingoapp.flingo.viewmodels.main.MainUiState
import kotlinx.coroutines.delay
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OrderStoryChallengeContent(
    modifier: Modifier = Modifier,
    mainUiState: MainUiState,
    onNavigate: (NavigationIntent) -> Unit,
    pageDetails: PageDetails.OrderStoryPageDetails
) {
    val startingSnippetMap = remember {
        mutableStateMapOf<Int, PageDetails.OrderStoryPageDetails.Companion.Content?>()
    }
    val resultSnippetMap = remember {
        mutableStateMapOf<Int, PageDetails.OrderStoryPageDetails.Companion.Content?>()
    }
    //initialization of the two maps
    LaunchedEffect(Unit) {
        repeat(pageDetails.content.size) { index ->
            resultSnippetMap[index] = null
            startingSnippetMap[index] = pageDetails.content[index]
        }
    }

    // index of the item that is currently being dragged, implemented in order for the
    // snippet to not show during dragging
    var indexCurrentDragStartList by remember { mutableIntStateOf(-1) }
    var indexCurrentDragResultList by remember { mutableIntStateOf(-1) }

    fun moveSnippet(
        snippet: PageDetails.OrderStoryPageDetails.Companion.Content?,
        dragPosition: Int,
        moveToResultMap: Boolean
    ): Boolean {
        //handling for when the snippet is not present in FROM map or already present in TO map
        if (moveToResultMap &&
            (!startingSnippetMap.containsValue(snippet) || resultSnippetMap.containsValue(snippet))
        )
            return false

        if (!moveToResultMap &&
            (!resultSnippetMap.containsValue(snippet) || startingSnippetMap.containsValue(snippet))
        )
            return false

        if (moveToResultMap) {
            val indexStartingMap =
                getMapIdForContent(content = snippet, map = startingSnippetMap) ?: return false
            resultSnippetMap[dragPosition] = snippet
            startingSnippetMap[indexStartingMap] = null
            indexCurrentDragStartList = -1
        } else {
            val indexResultMap = getMapIdForContent(content = snippet, map = resultSnippetMap) ?: return false
            startingSnippetMap[dragPosition] = snippet
            resultSnippetMap[indexResultMap] = null
            indexCurrentDragResultList = -1
        }

        return true
    }

    val allSnippetsInResultMap by remember {
        derivedStateOf {
            resultSnippetMap.filterValues { it == null }.isEmpty()
        }
    }

    var isCorrectAnswer: Boolean? by remember { mutableStateOf(null) }
    var buttonColor by remember { mutableStateOf(FlingoColors.Primary) }
    var isContinueButtonEnabled by remember { mutableStateOf(true) }
    var continueButtonText by remember { mutableStateOf("Fertig") }
    var continueButtonPressed by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = isCorrectAnswer) {
        if (isCorrectAnswer == false) {
            buttonColor = FlingoColors.Error
            isContinueButtonEnabled = false
            delay(1000)
            buttonColor = FlingoColors.Primary
            isCorrectAnswer = null
            isContinueButtonEnabled = true
        } else if (isCorrectAnswer == true) {
            continueButtonPressed = true
            delay(1000)
            continueButtonPressed = false
            continueButtonText = "Weiter gehts!"
        }
    }

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(modifier = Modifier.weight(1f)) {
            //startingSnippetList
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .zIndex(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                startingSnippetMap.values.forEachIndexed { index, contentItem ->
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .dragAndDropTarget(
                            shouldStartDragAndDrop = { event ->
                                event
                                    .mimeTypes()
                                    .contains(ClipDescription.MIMETYPE_TEXT_PLAIN)
                            },
                            target = remember {
                                object : DragAndDropTarget {
                                    override fun onDrop(event: DragAndDropEvent): Boolean {
                                        val draggedItemId =
                                            event.toAndroidDragEvent().clipData?.getItemAt(0)?.text
                                                .toString()
                                                .toInt()

                                        val draggedContent = getContentFromId(draggedItemId, pageDetails)

                                        val draggedFromResultMap = moveSnippet(
                                            snippet = draggedContent,
                                            dragPosition = index,
                                            moveToResultMap = false
                                        )

                                        // dragged object did not come from result map, therefore
                                        // it is needed to reset the drag id, since otherwise the item in
                                        // the starting list would not show
                                        if (!draggedFromResultMap) {
                                            // item inside of the lit got reordered
                                            // switch previous position content with new position
                                            if (indexCurrentDragStartList != index) {
                                                val previousContent =
                                                    startingSnippetMap[indexCurrentDragStartList]
                                                val newContent = startingSnippetMap[index]

                                                startingSnippetMap[index] = previousContent
                                                startingSnippetMap[indexCurrentDragStartList] = newContent
                                            }

                                            indexCurrentDragStartList = -1
                                        }

                                        return true
                                    }

                                    override fun onEnded(event: DragAndDropEvent) {
                                        //to fix disappearing on invalid drops
                                        indexCurrentDragStartList = -1

                                        super.onEnded(event)
                                    }
                                }
                            }
                        )
                    ) {
                        if (contentItem != null && index != indexCurrentDragStartList) {
                            PaperSnippet(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .dragAndDropSource {
                                        detectTapGestures(
                                            onPress = { offset ->
                                                indexCurrentDragStartList = index
                                                startTransfer(
                                                    transferData = DragAndDropTransferData(
                                                        clipData = ClipData.newPlainText(
                                                            "id",
                                                            contentItem.id.toString()
                                                        )
                                                    )
                                                )
                                            }
                                        )
                                    },
                                text = contentItem.text
                            )
                        }
                    }
                }
            }

            if (allSnippetsInResultMap) {
                if (isCorrectAnswer == true) {
                    KonfettiView(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth()
                            .align(Alignment.Center),
                        parties = listOf(
                            Party(
                                spread = 360,
                                angle = 0,
                                emitter = Emitter(duration = 750, TimeUnit.MILLISECONDS).perSecond(200)
                            )
                        )
                    )
                }

                CustomElevatedButton(
                    modifier = Modifier.align(Alignment.Center),
                    elevation = 10.dp,
                    shape = CircleShape,
                    onClick = {
                        if (isCorrectAnswer == true) {
                            mainUiState.currentChapter?.isCompleted = true
                            onNavigate(NavigationIntent.Up())
                        } else {
                            val resultOrder = resultSnippetMap.values.map { it?.id ?: -1 }

                            isCorrectAnswer = pageDetails.correctOrder == resultOrder

                            if (isCorrectAnswer == true) {
                                buttonColor = FlingoColors.Tertiary
                            }
                        }
                    },
                    isPressed = continueButtonPressed,
                    backgroundColor = buttonColor,
                    buttonContent = {
                        Text(
                            modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp),
                            text = continueButtonText,
                            color = Color.White,
                            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 38.sp)
                        )
                    }
                )
            }
        }

        //Result List
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .zIndex(0f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            resultSnippetMap.values.forEachIndexed { index, content ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .border(
                            width = 1.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .zIndex(0f)
                        .dragAndDropTarget(
                            shouldStartDragAndDrop = { event ->
                                event
                                    .mimeTypes()
                                    .contains(ClipDescription.MIMETYPE_TEXT_PLAIN)
                            },
                            target = remember {
                                object : DragAndDropTarget {
                                    override fun onDrop(event: DragAndDropEvent): Boolean {
                                        val draggedContentId =
                                            event.toAndroidDragEvent().clipData?.getItemAt(0)?.text
                                                .toString()
                                                .toInt()

                                        val draggedContent = getContentFromId(draggedContentId, pageDetails)

                                        val draggedFromStartingMap = moveSnippet(
                                            snippet = draggedContent,
                                            dragPosition = index,
                                            moveToResultMap = true
                                        )

                                        // dragged object did not come from starting map, therefore
                                        // it is needed to reset the drag id, since otherwise the item in
                                        // the starting list would not show
                                        if (!draggedFromStartingMap) {
                                            // item inside of the lit got reordered
                                            // switch previous position content with new position
                                            if (indexCurrentDragResultList != index) {
                                                val previousContent =
                                                    resultSnippetMap[indexCurrentDragResultList]
                                                val newContent = resultSnippetMap[index]

                                                resultSnippetMap[index] = previousContent
                                                resultSnippetMap[indexCurrentDragResultList] = newContent
                                            }

                                            indexCurrentDragResultList = -1
                                        }

                                        return true
                                    }

                                    override fun onEnded(event: DragAndDropEvent) {
                                        //to fix disappearing on invalid drops
                                        indexCurrentDragResultList = -1

                                        super.onEnded(event)
                                    }
                                }
                            }
                        ),
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center),
                        text = "${(index + 1)}",
                        fontSize = 120.sp,
                        color = Color.Black.lighten(0.75f)
                    )

                    if (content != null && indexCurrentDragResultList != index) {
                        PaperSnippet(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .dragAndDropSource {
                                    detectTapGestures(
                                        onPress = { offset ->
                                            indexCurrentDragResultList = index
                                            startTransfer(
                                                transferData = DragAndDropTransferData(
                                                    clipData = ClipData.newPlainText(
                                                        "id",
                                                        content.id.toString()
                                                    )
                                                )
                                            )
                                        }
                                    )
                                },
                            text = content.text
                        )
                    }
                }
            }
        }
    }
}

private fun getContentFromId(
    givenId: Int,
    pageDetails: PageDetails.OrderStoryPageDetails
): PageDetails.OrderStoryPageDetails.Companion.Content? {
    return pageDetails.content.find { givenId == it.id }
}

private fun getMapIdForContent(
    content: PageDetails.OrderStoryPageDetails.Companion.Content?,
    map: SnapshotStateMap<Int, PageDetails.OrderStoryPageDetails.Companion.Content?>
): Int? {
    val collection = map.filterValues { it?.id == content?.id }.keys

    if (collection.isEmpty()) return null

    return collection.first()
}