package com.flingoapp.flingo.ui.components.common

import android.util.Log
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.flingoapp.flingo.ui.getBoundingBoxesForRange
import com.flingoapp.flingo.ui.inflate
import com.flingoapp.flingo.ui.lighten

/**
 * Custom highlighted text used for reading page type to allow highlighting of the word
 *
 * @param modifier
 * @param content
 * @param currentWordIndex
 * @param highlightColor
 * @param textStyle
 */
@Composable
fun CustomHighlightedText(
    modifier: Modifier = Modifier,
    content: List<String>,
    currentWordIndex: Int,
    highlightColor: Color = MaterialTheme.colorScheme.primary,
    textStyle: TextStyle = MaterialTheme.typography.headlineLarge
) {
    val unreadWords = remember { content.subList(1, content.size).toMutableList() }
    val readWords = remember { mutableListOf("") }
    var currentWord by remember { mutableStateOf(content.first()) }
    var prevWordIndex by remember { mutableIntStateOf(0) }
    var currentWordBoundingBoxPath by remember { mutableStateOf(Path()) }

    LaunchedEffect(key1 = currentWordIndex) {
        //stop multiple executions from happening
        if (currentWordIndex >= content.size || currentWordIndex < 0 || prevWordIndex == currentWordIndex) return@LaunchedEffect

        readWords.add(currentWord)
        currentWord = content[currentWordIndex]
        unreadWords.first()

        prevWordIndex = currentWordIndex
    }

    val currentAnnotatedText = buildAnnotatedString {
        readWords.forEachIndexed { index, word ->
            if (index > 0) {
                append(" ")
            }

            withStyle(style = SpanStyle(color = Color.LightGray)) {
                append(word)
            }
        }

        append(" ")

        withStyle(style = SpanStyle(color = highlightColor.lighten(0.2f))) {
            pushStringAnnotation(tag = "click", annotation = currentWord)
            append(currentWord)
            pop()
        }

        unreadWords.forEach { word ->
            append(" ")
            withStyle(style = SpanStyle(color = Color.Black)) {
                append(word)
            }
        }
    }

    ClickableText(
        modifier = modifier
            .drawBehind {
                //TODO: add animated bounding box
                drawPath(
                    path = currentWordBoundingBoxPath,
                    style = Fill,
                    color = highlightColor.copy(alpha = 0.2f)
                )
            },
        text = currentAnnotatedText,
        style = textStyle,
        onClick = { offset ->
            currentAnnotatedText.getStringAnnotations(tag = "click", start = offset, end = offset)
                .firstOrNull()?.let {
                    Log.e("READSCREEN", "Clicked on ${it.item}")
                }
        },
        onTextLayout = { textLayout ->
            val currentWordCharIndex = readWords.joinToString(" ").length

            if (currentWordCharIndex >= currentAnnotatedText.length) return@ClickableText

            val boundingBoxStart = currentAnnotatedText.indexOf(
                startIndex = currentWordCharIndex,
                string = currentWord
            )

            val boundingBoxEnd =
                if (boundingBoxStart + currentWord.length >= currentAnnotatedText.length)
                    (currentAnnotatedText.length - 1)
                else boundingBoxStart + currentWord.length

            val charBoundingBoxes = textLayout.getBoundingBoxesForRange(
                start = boundingBoxStart,
                end = boundingBoxEnd
            )

            val cornerRadius = CornerRadius(x = 20f, y = 20f)

            currentWordBoundingBoxPath = Path().apply {
                for (i in charBoundingBoxes.indices) {
                    val boundingBox = charBoundingBoxes[i]
                    val leftCornerRoundRect =
                        if (i == 0) cornerRadius else CornerRadius.Zero
                    val rightCornerRoundRect =
                        if (i == charBoundingBoxes.indices.last) cornerRadius else CornerRadius.Zero
                    addRoundRect(
                        RoundRect(
                            boundingBox.inflate(verticalDelta = 5f, horizontalDelta = 8f),
                            topLeft = leftCornerRoundRect,
                            topRight = rightCornerRoundRect,
                            bottomRight = rightCornerRoundRect,
                            bottomLeft = leftCornerRoundRect,
                        )
                    )
                }
            }
        }
    )
}