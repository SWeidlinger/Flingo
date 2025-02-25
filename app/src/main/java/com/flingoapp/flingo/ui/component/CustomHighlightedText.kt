package com.flingoapp.flingo.ui.component

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
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
    enabled: Boolean = true,
    highlightColor: Color = MaterialTheme.colorScheme.primary,
    textStyle: TextStyle = MaterialTheme.typography.headlineLarge,
    lineCount: (Int) -> Unit = {}
) {
    if (content.isEmpty()) return

    val readWords = content.take(currentWordIndex)
    val currentWord = when {
        currentWordIndex < 0 || readWords.lastIndex == content.lastIndex -> ""
        else -> content[currentWordIndex]
    }
    val unreadWords = when {
        currentWordIndex < 0 -> content
        currentWordIndex < content.lastIndex && readWords.lastIndex != content.lastIndex -> content.drop(
            currentWordIndex + 1
        )

        else -> emptyList()
    }

    var charBoundingBoxesState by remember { mutableStateOf<List<Rect>>(emptyList()) }

    var baseTargetOffset by remember { mutableStateOf(Offset.Zero) }
    val animatedOffset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }

    //TODO: fix does not animate to next word bounding box correctly
//    LaunchedEffect(currentWordIndex, baseTargetOffset) {
//        if (charBoundingBoxesState.isNotEmpty()) {
//            animatedOffset.animateTo(
//                targetValue = baseTargetOffset,
//                animationSpec = spring(
//                    stiffness = Spring.StiffnessLow,
//                    dampingRatio = Spring.DampingRatioMediumBouncy
//                )
//            )
//        }
//    }

    val infiniteTransition = rememberInfiniteTransition()

    // vertical shift
    val shiftY by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    // horizontal shift
    val shiftX by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val tiltAngle by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val baseInflateHorizontal = 8f
    val baseInflateVertical = 5f

    val animatedInflateHorizontal by infiniteTransition.animateFloat(
        initialValue = baseInflateHorizontal,
        targetValue = baseInflateHorizontal + 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val animatedInflateVertical by infiniteTransition.animateFloat(
        initialValue = baseInflateVertical,
        targetValue = baseInflateVertical + 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val animatedCornerRadius by infiniteTransition.animateFloat(
        initialValue = 15f,
        targetValue = 25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val annotatedText = buildAnnotatedString {
        if (currentWordIndex >= 0) {
            readWords.forEachIndexed { index, word ->
                if (index > 0) append(" ")
                withStyle(style = SpanStyle(color = Color.LightGray)) {
                    append(word)
                }
            }
            if (readWords.isNotEmpty()) append(" ")
            // Only add the current word if not at the end
            if (content.lastIndex != readWords.lastIndex) {
                withStyle(
                    style = SpanStyle(
                        color = if (enabled) highlightColor.lighten(0.2f) else Color.Black
                    )
                ) {
                    pushStringAnnotation(tag = "click", annotation = currentWord)
                    append(currentWord)
                    pop()
                }
            }
        }
        unreadWords.forEachIndexed { index, word ->
            if (index > 0 || currentWordIndex >= 0) append(" ")
            withStyle(style = SpanStyle(color = Color.Black)) {
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

            lineCount(textLayout.lineCount)

            layoutResult = textLayout
            val readText = if (readWords.isNotEmpty()) readWords.joinToString(" ") + " " else ""
            val currentWordStartIndex = readText.length
            val currentWordEndIndex = (currentWordStartIndex + currentWord.length)
                .coerceAtMost(annotatedText.length - 1)
            // Compute bounding boxes for each character in the current word
            val charBoxes = textLayout.getBoundingBoxesForRange(
                start = currentWordStartIndex,
                end = currentWordEndIndex
            )
            charBoundingBoxesState = charBoxes

            // Compute the union of all character bounding boxes.
            if (charBoxes.isNotEmpty()) {
                val unionRect = charBoxes.reduce { acc, rect ->
                    Rect(
                        left = minOf(acc.left, rect.left),
                        top = minOf(acc.top, rect.top),
                        right = maxOf(acc.right, rect.right),
                        bottom = maxOf(acc.bottom, rect.bottom)
                    )
                }
                // Use the union's top-left as the base offset.
                baseTargetOffset = Offset(
                    x = unionRect.left,
                    y = unionRect.top
                )
            }
        },
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures { tapOffset ->
                    layoutResult?.let { textLayout ->
                        val tappedOffset = textLayout.getOffsetForPosition(tapOffset)
                        val safeOffset = if (tappedOffset >= updatedAnnotatedText.length)
                            updatedAnnotatedText.length - 1 else tappedOffset
                        updatedAnnotatedText.getStringAnnotations(
                            tag = "click",
                            start = safeOffset,
                            end = safeOffset
                        ).firstOrNull()?.let { annotation ->
                            Log.d("CustomHighlightedText", "Clicked on ${annotation.item}")
                        }
                    }
                }
            }
            .drawBehind {
                if (currentWordIndex < 0 || !enabled) return@drawBehind
                if (charBoundingBoxesState.isNotEmpty()) {
                    // Rebuild the path for the bounding box using the animated morphing values
                    val path = Path().apply {
                        charBoundingBoxesState.forEachIndexed { index, rect ->
                            val leftRadius = if (index == 0)
                                CornerRadius(animatedCornerRadius, animatedCornerRadius)
                            else CornerRadius.Zero
                            val rightRadius = if (index == charBoundingBoxesState.lastIndex)
                                CornerRadius(animatedCornerRadius, animatedCornerRadius)
                            else CornerRadius.Zero
                            addRoundRect(
                                RoundRect(
                                    rect = rect.inflate(
                                        horizontalDelta = animatedInflateHorizontal,
                                        verticalDelta = animatedInflateVertical
                                    ),
                                    topLeft = leftRadius,
                                    topRight = rightRadius,
                                    bottomRight = rightRadius,
                                    bottomLeft = leftRadius
                                )
                            )
                        }
                    }
                    // Compute a union of all bounding boxes to use as the pivot for rotation.
                    val unionRect = charBoundingBoxesState.reduce { acc, rect ->
                        Rect(
                            left = minOf(acc.left, rect.left),
                            top = minOf(acc.top, rect.top),
                            right = maxOf(acc.right, rect.right),
                            bottom = maxOf(acc.bottom, rect.bottom)
                        )
                    }
                    val pivot = unionRect.center

                    // Combine the animated base translation with continuous shift, bounce, and tilt.
                    translate(
                        left = animatedOffset.value.x + shiftX,
                        top = animatedOffset.value.y + shiftY
                    ) {
                        rotate(degrees = tiltAngle, pivot = pivot) {
                            drawPath(
                                path = path,
                                style = Fill,
                                color = highlightColor.copy(alpha = 0.2f)
                            )
                        }
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
            modifier = Modifier.padding(16.dp),
            content = listOf("Hello", "World!", "This", "is", "a", "test"),
            currentWordIndex = 1
        )
    }
}