package com.flingoapp.flingo.ui.component.common

import android.util.Log
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.flingoapp.flingo.ui.CustomPreview
import com.flingoapp.flingo.ui.getBoundingBoxesForRange
import com.flingoapp.flingo.ui.inflate
import com.flingoapp.flingo.ui.lighten
import com.flingoapp.flingo.ui.theme.FlingoTheme

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
    if (content.isEmpty()) return

    // Derive the parts of the text from the content list.
    // Clamp the index to valid bounds.
    val safeIndex = currentWordIndex.coerceIn(0, content.lastIndex)
    val readWords = content.take(safeIndex)
    val currentWord = content[safeIndex]
    val unreadWords =
        if (safeIndex < content.lastIndex) content.drop(safeIndex + 1) else emptyList()

    // State for the bounding box path of the current word.
    var currentWordBoundingBoxPath by remember { mutableStateOf(Path()) }

    // Build an annotated string with styling for read, current, and unread words
    val annotatedText = buildAnnotatedString {
        readWords.forEachIndexed { index, word ->
            if (index > 0) append(" ")
            withStyle(style = SpanStyle(color = Color.LightGray)) {
                append(word)
            }
        }

        if (readWords.isNotEmpty()) append(" ")

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

    //TODO: replace ClickableText with basic text
    ClickableText(
        modifier = modifier.drawBehind {
            //TODO: add animated bounding box morphing and moving in place to make it more dynamic and
            // playful
            drawPath(
                path = currentWordBoundingBoxPath,
                style = Fill,
                color = highlightColor.copy(alpha = 0.2f)
            )
        },
        text = annotatedText,
        style = textStyle,
        onClick = { offset ->
            val safeOffset =
                if (offset >= annotatedText.length) annotatedText.length - 1 else offset

            annotatedText.getStringAnnotations(tag = "click", start = safeOffset, end = safeOffset)
                .firstOrNull()?.let {
                    Log.e("READSCREEN", "Clicked on ${it.item}")
                }
        },
        onTextLayout = { textLayout ->
            val readText = if (readWords.isNotEmpty()) readWords.joinToString(" ") + " " else ""
            val currentWordStartIndex = readText.length
            val currentWordEndIndex = (currentWordStartIndex + currentWord.length)
                .coerceAtMost(annotatedText.length - 1)

            val charBoundingBoxes = textLayout.getBoundingBoxesForRange(
                start = currentWordStartIndex,
                end = currentWordEndIndex
            )

            val cornerRadius = CornerRadius(x = 20f, y = 20f)

            currentWordBoundingBoxPath = Path().apply {
                charBoundingBoxes.forEachIndexed { index, rect ->
                    val leftRadius = if (index == 0) cornerRadius else CornerRadius.Zero
                    val rightRadius =
                        if (index == charBoundingBoxes.lastIndex) cornerRadius else CornerRadius.Zero
                    addRoundRect(
                        RoundRect(
                            rect.inflate(horizontalDelta = 8f, verticalDelta = 5f),
                            topLeft = leftRadius,
                            topRight = rightRadius,
                            bottomRight = rightRadius,
                            bottomLeft = leftRadius,
                        )
                    )
                }
            }
        }
    )
}

@CustomPreview
@Composable
private fun CustomHighlightedTextPreview() {
    FlingoTheme {
        CustomHighlightedText(
            content = listOf("Hello", "World!", "This", "is", "a", "test"),
            currentWordIndex = 1
        )
    }
}