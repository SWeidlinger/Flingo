package com.flingoapp.flingo.ui.component

import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.flingoapp.flingo.ui.CustomPreview
import com.flingoapp.flingo.ui.getBoundingBoxesForRange
import com.flingoapp.flingo.ui.inflate
import com.flingoapp.flingo.ui.lighten
import com.flingoapp.flingo.ui.theme.FlingoColors
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
    enabled: Boolean = true,
    highlightColor: Color = MaterialTheme.colorScheme.primary,
    textStyle: TextStyle = MaterialTheme.typography.headlineLarge
) {
    if (content.isEmpty()) return

    val readWords = content.take(currentWordIndex)
    val currentWord =
        if (currentWordIndex < 0 || readWords.lastIndex == content.lastIndex) "" else content[currentWordIndex]
    val unreadWords = if (currentWordIndex < 0) content else
        if (currentWordIndex < content.lastIndex && readWords.lastIndex != content.lastIndex) content.drop(
            currentWordIndex + 1
        ) else emptyList()

    // State for the bounding box path of the current word.
    var currentWordBoundingBoxPath by remember { mutableStateOf(Path()) }

    // Build an annotated string with styling for read, current, and unread words
    val annotatedText = buildAnnotatedString {
        if (currentWordIndex >= 0) {
            readWords.forEachIndexed { index, word ->
                if (index > 0) append(" ")
                withStyle(style = SpanStyle(color = Color.LightGray)) {
                    append(word)
                }
            }

            if (readWords.isNotEmpty()) append(" ")
            if (content.lastIndex == readWords.lastIndex) return@buildAnnotatedString


            withStyle(
                style = SpanStyle(
                    color = if (enabled) highlightColor.lighten(0.2f) else FlingoColors.Text
                )
            ) {
                pushStringAnnotation(tag = "click", annotation = currentWord)
                append(currentWord)
                pop()
            }
        }

        unreadWords.forEachIndexed { index, word ->
            if (index > 0 || currentWordIndex >= 0) append(" ")
            withStyle(style = SpanStyle(color = FlingoColors.Text)) {
                append(word)
            }
        }
    }

    var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    val updatedAnnotatedText by rememberUpdatedState(newValue = annotatedText)

    BasicText(
        text = annotatedText,
        style = textStyle,
        onTextLayout = { textLayout ->
            if (currentWordIndex < 0 || readWords.lastIndex == content.lastIndex) return@BasicText

            layoutResult = textLayout
            val readText = if (readWords.isNotEmpty()) readWords.joinToString(" ") + " " else ""
            val currentWordStartIndex = readText.length

            val currentWordEndIndex = (currentWordStartIndex + currentWord.length)
                .coerceAtMost(annotatedText.length - 1)

            // Get bounding boxes for each character in the current word.
            val charBoundingBoxes = textLayout.getBoundingBoxesForRange(
                start = currentWordStartIndex,
                end = currentWordEndIndex
            )

            val cornerRadius = CornerRadius(x = 20f, y = 20f)

            //TODO: fix additional space added, by line spacing
            currentWordBoundingBoxPath = Path().apply {
                charBoundingBoxes.forEachIndexed { index, rect ->
                    val leftRadius = if (index == 0) cornerRadius else CornerRadius.Zero
                    val rightRadius =
                        if (index == charBoundingBoxes.lastIndex) cornerRadius else CornerRadius.Zero
                    addRoundRect(
                        RoundRect(
                            rect = rect.inflate(horizontalDelta = 8f, verticalDelta = 5f),
                            topLeft = leftRadius,
                            topRight = rightRadius,
                            bottomRight = rightRadius,
                            bottomLeft = leftRadius,
                        )
                    )
                }
            }
        },
        modifier = modifier
            // Handle taps manually.
            .pointerInput(Unit) {
                detectTapGestures { tapOffset ->
                    layoutResult?.let { textLayout ->
                        // Get the character offset corresponding to the tap.
                        val tappedOffset = textLayout.getOffsetForPosition(tapOffset)
                        val safeOffset = if (tappedOffset >= updatedAnnotatedText.length)
                            updatedAnnotatedText.length - 1
                        else tappedOffset

                        // Look for annotations tagged as "click".
                        updatedAnnotatedText.getStringAnnotations(
                            tag = "click",
                            start = safeOffset,
                            end = safeOffset
                        ).firstOrNull()?.let { annotation ->
                            Log.e("CustomHighlightedText", "Clicked on ${annotation.item}")
                        }
                    }
                }
            }
            // Draw the animated bounding box behind the text.
            .drawBehind {
                if (currentWordIndex < 0 || !enabled) return@drawBehind
                drawPath(
                    path = currentWordBoundingBoxPath,
                    style = Fill,
                    color = highlightColor.copy(alpha = 0.2f)
                )
            }
    )
}

@CustomPreview
@Composable
private fun CustomHighlightedTextPreview() {
    FlingoTheme {
        CustomHighlightedText(
            content = listOf("Hello", "World!", "This", "is", "a", "test"),
            currentWordIndex = 0
        )
    }
}