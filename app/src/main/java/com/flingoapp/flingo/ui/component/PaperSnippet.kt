package com.flingoapp.flingo.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flingoapp.flingo.ui.AutoResizableText
import com.flingoapp.flingo.ui.CustomPreview
import com.flingoapp.flingo.ui.theme.FlingoTheme
import kotlin.random.Random

@Composable
fun PaperSnippet(
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Black,
    snippetColor: Color = Color(0xFFFFD54F),
    onDragEnd: (x: Float, y: Float) -> Unit = { _, _ -> }
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    val snippetShape = GenericShape { size, _ ->
        val frayAmount = 25
        val stepX = size.width / frayAmount
        val stepY = size.height / frayAmount

        //upper border
        moveTo(0f, Random.nextFloat() * stepY)
        for (i in 1..frayAmount) {
            lineTo(i * stepX, Random.nextFloat() * stepY)
        }

        //right border
        for (i in 1..frayAmount) {
            lineTo(size.width - Random.nextFloat() * stepX, i * stepY)
        }

        //lower border
        for (i in frayAmount downTo 0) {
            lineTo(i * stepX, size.height - Random.nextFloat() * stepY)
        }

        //left border
        for (i in frayAmount downTo 0) {
            lineTo(Random.nextFloat() * stepX, i * stepY)
        }

        close()
    }

    Box(
        modifier = modifier
            .graphicsLayer(
                translationX = offsetX,
                translationY = offsetY
            )
            .background(snippetColor, snippetShape)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = { onDragEnd(offsetX, offsetY) },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                )
            }
            .padding(16.dp)
    ) {
        AutoResizableText(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = text,
            color = textColor,
            fontSize = 28.sp
        )
    }
}

@CustomPreview
@Composable
private fun PaperSnippetPreview() {
    FlingoTheme {
        PaperSnippet(text = "Hallo das ist ein Test-Snippet")
    }
}