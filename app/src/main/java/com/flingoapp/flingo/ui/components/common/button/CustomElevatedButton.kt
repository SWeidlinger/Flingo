package com.flingoapp.flingo.ui.components.common.button

import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.flingoapp.flingo.ui.darken
import com.flingoapp.flingo.ui.theme.FlingoTheme

object CustomElevatedButtonDefault {
    val MinWidth = 58.dp
    val MinHeight = 72.dp
}

@Composable
fun CustomElevatedButton(
    modifier: Modifier = Modifier,
    size: DpSize? = null,
    shape: RoundedCornerShape,
    addOutline: Boolean = false,
    elevation: Dp = 20.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    shadowColor: Color = backgroundColor.darken(0.2f),
    enabled: Boolean = true,
    isPressed: Boolean = false,
    pressedColor: Color = backgroundColor,
    disabledColor: Color = Color.LightGray.copy(alpha = 0.75f),
    animateButtonClick: Boolean = true,
    //TODO: disabled for now since it causes lags
//    clickSound: Int? = R.raw.button_click,
    clickSound: Int? = null,
    onClick: () -> Unit,
    buttonContent: @Composable () -> Unit
) {
    val mediaPlayer: MediaPlayer? = clickSound?.let { MediaPlayer.create(LocalContext.current, it) }

    var buttonState by remember { mutableStateOf(ButtonState.Idle) }

    val interactionSource = remember { MutableInteractionSource() }
    val isButtonPressed by interactionSource.collectIsPressedAsState()

    var buttonContentSize by remember { mutableStateOf(IntSize.Zero) }

    if (isButtonPressed) {
        buttonState = ButtonState.Pressed
        LaunchedEffect(key1 = Unit) {
            mediaPlayer?.start()
        }

        DisposableEffect(Unit) {
            onDispose {
                buttonState = ButtonState.Idle
            }
        }
    }

    val customSizeModifier = if (size != null) {
        modifier.size(size)
    } else {
        modifier
    }

    val customSizeButtonContentModifier = if (size != null) {
        Modifier.size(size)
    } else {
        Modifier
    }

    Box(
        modifier = customSizeModifier
            //somehow also changing color to transparent is needed for outline to not be shown
            .border(
                if (addOutline) 1.dp else 0.dp, if (addOutline) shadowColor else Color.Transparent,
                shape
            )
            .defaultMinSize(CustomElevatedButtonDefault.MinWidth, CustomElevatedButtonDefault.MinHeight)
            .clip(shape)
            .background(shadowColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    if (!enabled) return@clickable

                    if (animateButtonClick) {
                        buttonState = ButtonState.Pressed
                    }
                    onClick()
                }
            )
            .offset(y = if (buttonState == ButtonState.Pressed || !enabled || isPressed) 0.dp else (-elevation))
    ) {
        Box(
            modifier = customSizeButtonContentModifier
                .border(
                    if (addOutline) 1.dp else 0.dp,
                    if (addOutline) shadowColor else Color.Transparent,
                    shape
                )
                .onGloballyPositioned { layoutCoordinates ->
                    buttonContentSize = layoutCoordinates.size
                }
                .clip(shape)
                .background(if (isPressed) pressedColor else backgroundColor)
                .padding(
                    top = 24.dp,
                    bottom = 12.dp,
                    start = 24.dp,
                    end = 24.dp
                ),
            contentAlignment = Alignment.Center
        ) {
            // used to alleviate the content shift a bit
            Box(
                modifier = Modifier.offset(
                    y = if (buttonState == ButtonState.Pressed || !enabled || isPressed) -(elevation / 2) else 0.dp
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

enum class ButtonState { Pressed, Idle }

@Preview(showBackground = true)
@Composable
private fun CustomElevatedButtonPreview() {
    FlingoTheme {
        CustomElevatedButton(
            size = DpSize(150.dp, 75.dp),
            enabled = true,
            isPressed = false,
            shape = RoundedCornerShape(50.dp),
            elevation = 10.dp,
            backgroundColor = Color.LightGray,
            animateButtonClick = true,
            onClick = {},
            buttonContent = {
                Text(text = "Click Me!")
            }
        )
    }
}