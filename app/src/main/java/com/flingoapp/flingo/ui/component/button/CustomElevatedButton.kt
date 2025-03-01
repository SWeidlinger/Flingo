package com.flingoapp.flingo.ui.component.button

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flingoapp.flingo.ui.AutoResizableText
import com.flingoapp.flingo.ui.darken
import com.flingoapp.flingo.ui.theme.FlingoColors
import com.flingoapp.flingo.ui.theme.FlingoTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

object CustomElevatedButtonDefault {
    val MinWidth = 58.dp
    val MinHeight = 72.dp
}

/**
 * Custom elevated button, used to mimic an actual button press, by using elevation and a darker shade to
 * symbolize depth
 *
 * @param modifier
 * @param size specified size of the button, if null [buttonContent] will specify size of button
 * @param shape of the button
 * @param addOutline if outline should be added to button
 * @param elevation
 * @param backgroundColor
 * @param shadowColor
 * @param enabled
 * @param isPressed if the button should appear to be pressed
 * @param pressedColor
 * @param disabledColor
 * @param animateButtonClick
 * @param clickSound currently not used as current implementation is quite resource heavy
 * @param onClick
 * @param buttonContent content which should be shown inside of the button
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CustomElevatedButton(
    modifier: Modifier = Modifier,
    buttonContentModifier: Modifier = Modifier,
    size: DpSize? = null,
    shape: Shape,
    addOutline: Boolean = false,
    elevation: Dp = 8.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    shadowColor: Color = backgroundColor.darken(0.2f),
    enabled: Boolean = true,
    isPressed: Boolean = false,
    pressedColor: Color = backgroundColor,
    disabledColor: Color = FlingoColors.LightGray.copy(alpha = 0.75f),
    animateButtonClick: Boolean = true,
    //TODO: investigate why this duration is not the actual one
    // seems to depend on which device it is run on
    buttonPressDurationMilli: Int = 0,
    progressAnimationType: ButtonProgressAnimation = ButtonProgressAnimation.LEFT_TO_RIGHT,
    //TODO: disabled for now since it causes system stutters, fix before using
//    clickSound: Int? = R.raw.button_click,
    clickSound: Int? = null,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    buttonContent: @Composable () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val mediaPlayer: MediaPlayer? = clickSound?.let { MediaPlayer.create(context, it) }

    var buttonState by remember { mutableStateOf(ButtonState.IDLE) }
    val interactionSource = remember { MutableInteractionSource() }
    val isButtonPressed by interactionSource.collectIsPressedAsState()

    val isTimedButton = buttonPressDurationMilli > 0
    var currentButtonPressedDuration by remember { mutableFloatStateOf(0f) }
    val buttonProgress by animateFloatAsState(
        targetValue = if (isTimedButton) (currentButtonPressedDuration / buttonPressDurationMilli) else 0f,
        label = "button progress"
    )

    //added for buttons to be able to reset when isPressed boolean is changed
    // not sure if this is really efficient
    LaunchedEffect(isPressed) {
        buttonState = if (isPressed) ButtonState.PRESSED else ButtonState.IDLE
    }

    var isButtonPressCompleted by remember {
        mutableStateOf(false)
    }

    if (isButtonPressed) {
        buttonState = ButtonState.PRESSED
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
                buttonState = ButtonState.IDLE
                if (!isButtonPressCompleted) {
                    //don't reset if buttonPress is already completed
                    currentButtonPressedDuration = 0f
                }
            }
        }
    }

    val customSizeModifier = if (size != null) {
        modifier.size(size)
    } else {
        modifier
    }

    Box(
        modifier = customSizeModifier
            //somehow also changing color to transparent is needed for outline to not be shown
            .border(
                width = if (addOutline) 1.dp else 0.dp,
                color = if (addOutline) shadowColor else Color.Transparent,
                shape = shape
            )
            .defaultMinSize(
                CustomElevatedButtonDefault.MinWidth,
                CustomElevatedButtonDefault.MinHeight
            )
            .clip(shape)
            .background(shadowColor)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    if (!enabled) return@combinedClickable

                    if (animateButtonClick) {
                        buttonState = ButtonState.PRESSED

                        if (isTimedButton) {
                            // reset button on short press if it is a timed button
                            coroutineScope.launch {
                                delay(50)
                                buttonState = ButtonState.IDLE
                            }
                        }
                    }

                    if (!isTimedButton) {
                        onClick()
                    }
                },
                onLongClick = onLongClick
            )
//            .offset {
//                val y =
//                    if (buttonState == ButtonState.PRESSED || !enabled || isPressed) 0
//                    else (-elevationPixel)
//
//                IntOffset(0, y)
//            }
            //TODO: should be replaced with remembered offset as seen above, current problem is that the
            // content shift is not correctly alleviated, therefore it is not implemented yet, until a
            // solution for this problem is found
            .offset(y = if (buttonState == ButtonState.PRESSED || !enabled || isPressed) 0.dp else (-elevation))
    ) {
        Box(
            modifier = customSizeModifier
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
                //TODO: ask if this random animation mechanism should be kept or not
                if (progressAnimationType == ButtonProgressAnimation.END_TO_CENTER) {
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
                modifier = buttonContentModifier
                    .padding(horizontal = 24.dp)
                    .padding(top = 24.dp, bottom = 12.dp)
//                    .offset {
//                        val y =
//                            if (buttonState == ButtonState.PRESSED || !enabled || isPressed) 0
//                            else (-elevationPixel / 2)
//
//                        IntOffset(0, y)
//                    }
                    .offset(
                        y = if (buttonState == ButtonState.PRESSED || !enabled || isPressed) -(elevation / 2)
                        else 0.dp
                    )
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
enum class ButtonState {
    PRESSED,
    IDLE
}

enum class ButtonProgressAnimation {
    LEFT_TO_RIGHT,
    RIGHT_TO_LEFT,
    CENTER_TO_END,
    END_TO_CENTER,
    TOP_TO_BOTTOM,
    BOTTOM_TO_TOP,
    CENTER_TO_TOP_AND_BOTTOM
}

// Extension for checking animation type alignment
private val ButtonProgressAnimation.alignment: Alignment
    get() = when (this) {
        ButtonProgressAnimation.LEFT_TO_RIGHT -> Alignment.CenterStart
        ButtonProgressAnimation.CENTER_TO_END -> Alignment.Center
        ButtonProgressAnimation.RIGHT_TO_LEFT -> Alignment.CenterEnd
        ButtonProgressAnimation.BOTTOM_TO_TOP -> Alignment.BottomCenter
        ButtonProgressAnimation.TOP_TO_BOTTOM -> Alignment.TopCenter
        ButtonProgressAnimation.CENTER_TO_TOP_AND_BOTTOM -> Alignment.Center
        else -> Alignment.CenterStart
    }

private val ButtonProgressAnimation.isVertical: Boolean
    get() = this in listOf(
        ButtonProgressAnimation.BOTTOM_TO_TOP,
        ButtonProgressAnimation.TOP_TO_BOTTOM,
        ButtonProgressAnimation.CENTER_TO_TOP_AND_BOTTOM
    )

@Preview(showBackground = true)
@Composable
private fun CustomElevatedButtonPreview() {
    FlingoTheme {
        CustomElevatedButton(
            size = DpSize(150.dp, 75.dp),
            enabled = true,
            isPressed = false,
            shape = RoundedCornerShape(50.dp),
            backgroundColor = Color.LightGray,
            animateButtonClick = true,
            onClick = {},
            buttonContent = {
                AutoResizableText(text = "Click Me!", fontSize = 100.sp)
            }
        )
    }
}