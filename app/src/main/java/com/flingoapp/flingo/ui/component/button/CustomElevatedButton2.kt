package com.flingoapp.flingo.ui.component.button

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flingoapp.flingo.ui.CustomPreview
import com.flingoapp.flingo.ui.darken
import com.flingoapp.flingo.ui.theme.FlingoColors
import com.flingoapp.flingo.ui.theme.FlingoTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

object CustomElevatedButton2Default {
    val MinWidth = 100.dp
    val MinHeight = 72.dp
}

/**
 * Custom elevated button, used to mimic an actual button press, by using elevation and a darker shade to
 * symbolize depth
 *
 * @param modifier
 * @param shape of the button
 * @param addOutline if outline should be added to button
 * @param elevation
 * @param backgroundColor
 * @param shadowColor
 * @param enabled
 * @param isPressed if the button should appear to be pressed
 * @param pressedColor
 * @param disabledColor
 * @param clickSound currently not used as current implementation is quite resource heavy
 * @param onClick
 * @param buttonContent content which should be shown inside of the button
 */
@Composable
fun CustomElevatedButton2(
    modifier: Modifier = Modifier,
    shape: Shape,
    addOutline: Boolean = false,
    elevation: Dp = 8.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    shadowColor: Color = backgroundColor.darken(0.3f),
    enabled: Boolean = true,
    isPressed: Boolean = false,
    pressedColor: Color = backgroundColor,
    disabledColor: Color = FlingoColors.LightGray.copy(alpha = 0.75f),
    //TODO: investigate why this duration is not the actual one
    // seems to depend on which device it is run on
    buttonPressDurationMilli: Int = 0,
    progressAnimationType: ButtonProgressAnimation2 = ButtonProgressAnimation2.LEFT_TO_RIGHT,
    //TODO: disabled for now since it causes system stutters, fix before using
//    clickSound: Int? = R.raw.button_click,
    clickSound: Int? = null,
    onClick: () -> Unit,
    buttonContent: @Composable () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val mediaPlayer: MediaPlayer? = clickSound?.let { MediaPlayer.create(context, it) }

    var buttonState by remember { mutableStateOf(ButtonState2.IDLE) }
    val interactionSource = remember { MutableInteractionSource() }
    val isButtonPressed by interactionSource.collectIsPressedAsState()

    val isTimedButton by remember { mutableStateOf(buttonPressDurationMilli > 0) }
    var currentButtonPressedDuration by remember { mutableFloatStateOf(0f) }
    val buttonProgress by animateFloatAsState(
        targetValue = if (isTimedButton) (currentButtonPressedDuration / buttonPressDurationMilli) else 0f,
        label = "button progress"
    )

    LaunchedEffect(isPressed) {
        buttonState = if (isPressed) ButtonState2.PRESSED else ButtonState2.IDLE
    }

    var isButtonPressCompleted by remember {
        mutableStateOf(false)
    }

    if (isButtonPressed) {
        buttonState = ButtonState2.PRESSED
        LaunchedEffect(key1 = Unit) {
            mediaPlayer?.start()
        }

        if (isTimedButton) {
            LaunchedEffect(Unit) {
                while (currentButtonPressedDuration < buttonPressDurationMilli) {
                    delay(10.milliseconds)
                    currentButtonPressedDuration += 10
                }

                Log.i("CustomElevatedButton", "ButtonPressDuration reached")
                isButtonPressCompleted = true
                onClick()
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                buttonState = ButtonState2.IDLE
                if (!isButtonPressCompleted) {
                    //don't reset if buttonPress is already completed
                    currentButtonPressedDuration = 0f
                }
            }
        }
    }

    Box(
        modifier = modifier
            //somehow also changing color to transparent is needed for outline to not be shown
            .border(
                width = if (addOutline) 1.dp else 0.dp,
                color = if (addOutline) shadowColor else Color.Transparent,
                shape = shape
            )
            .clip(shape)
            .background(shadowColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    if (!enabled) return@clickable

                    buttonState = ButtonState2.PRESSED

                    if (isTimedButton) {
                        // reset button on short press if it is a timed button
                        coroutineScope.launch {
                            delay(50)
                            buttonState = ButtonState2.IDLE
                        }
                    } else {
                        onClick()
                    }
                }
            )
            .offset {
                val y = if (buttonState == ButtonState2.PRESSED || !enabled || isPressed) 0
                else (-elevation.toPx().toInt())

                IntOffset(0, y)
            }
    ) {
        Box(
            modifier = Modifier
                .border(
                    if (addOutline) 1.dp else 0.dp,
                    if (addOutline) shadowColor else Color.Transparent,
                    shape
                )
                .clip(shape)
                .background(if (isPressed) pressedColor else backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            if (isTimedButton) {
                if (progressAnimationType == ButtonProgressAnimation2.END_TO_CENTER) {
                    // reverse center animation
                    Box(
                        Modifier
                            .align(Alignment.CenterStart)
                            .fillMaxHeight()
                            .fillMaxWidth(buttonProgress * 0.5f)
                            .background(pressedColor)
                    )

                    Box(
                        Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxHeight()
                            .fillMaxWidth(buttonProgress * 0.5f)
                            .background(pressedColor)
                    )
                } else {
                    Box(
                        Modifier
                            .align(progressAnimationType.alignment)
                            .fillMaxHeight(if (progressAnimationType.isVertical) buttonProgress else 1f)
                            .fillMaxWidth(if (progressAnimationType.isVertical) 1f else buttonProgress)
                            .background(pressedColor)
                    )
                }
            }

            // used to alleviate the content shift a bit
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .offset {
                        val y =
                            if (buttonState == ButtonState2.PRESSED || !enabled || isPressed) 0
                            else (elevation.toPx().toInt() / 2)

                        IntOffset(0, y)
                    }
            ) {
                buttonContent()
            }
        }

        if (!enabled) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(disabledColor)
            )
        }
    }
}

/**
 * Button state
 *
 */
enum class ButtonState2 {
    PRESSED,
    IDLE
}

enum class ButtonProgressAnimation2 {
    LEFT_TO_RIGHT,
    RIGHT_TO_LEFT,
    CENTER_TO_END,
    END_TO_CENTER,
    TOP_TO_BOTTOM,
    BOTTOM_TO_TOP,
    CENTER_TO_TOP_AND_BOTTOM
}

// Extension for checking animation type alignment
private val ButtonProgressAnimation2.alignment: Alignment
    get() = when (this) {
        ButtonProgressAnimation2.LEFT_TO_RIGHT -> Alignment.CenterStart
        ButtonProgressAnimation2.CENTER_TO_END -> Alignment.Center
        ButtonProgressAnimation2.RIGHT_TO_LEFT -> Alignment.CenterEnd
        ButtonProgressAnimation2.BOTTOM_TO_TOP -> Alignment.BottomCenter
        ButtonProgressAnimation2.TOP_TO_BOTTOM -> Alignment.TopCenter
        ButtonProgressAnimation2.CENTER_TO_TOP_AND_BOTTOM -> Alignment.Center
        else -> Alignment.CenterStart
    }

private val ButtonProgressAnimation2.isVertical: Boolean
    get() = this in listOf(
        ButtonProgressAnimation2.BOTTOM_TO_TOP,
        ButtonProgressAnimation2.TOP_TO_BOTTOM,
        ButtonProgressAnimation2.CENTER_TO_TOP_AND_BOTTOM
    )

@CustomPreview
@Composable
private fun CustomElevatedButton2Preview() {
    FlingoTheme {
        CustomElevatedButton2(
            enabled = true,
            isPressed = false,
            shape = RoundedCornerShape(20),
            backgroundColor = Color.LightGray,
            onClick = {},
            buttonContent = {
                Text(text = "Click Me!", fontSize = 56.sp)
            }
        )
    }
}

@CustomPreview
@Composable
private fun CustomElevatedButtonOutline2Preview() {
    FlingoTheme {
        CustomElevatedButton2(
            enabled = true,
            isPressed = false,
            addOutline = true,
            shape = RoundedCornerShape(20),
            backgroundColor = Color.LightGray,
            onClick = {},
            buttonContent = {
                Text(text = "Click Me!", fontSize = 56.sp)
            }
        )
    }
}

@CustomPreview
@Composable
private fun CustomElevatedButtonCircle2Preview() {
    FlingoTheme {
        CustomElevatedButton2(
            enabled = true,
            isPressed = false,
            addOutline = true,
            shape = CircleShape,
            backgroundColor = Color.LightGray,
            onClick = {},
            buttonContent = {
                Text(text = "Click Me!", fontSize = 24.sp)
            }
        )
    }
}