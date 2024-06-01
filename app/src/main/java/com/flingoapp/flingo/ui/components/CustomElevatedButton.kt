package com.flingoapp.flingo.ui.components

import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class ButtonState { Pressed, Idle }

@Composable
fun CustomElevatedButton(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape,
    elevation: Dp,
    color: Color = MaterialTheme.colorScheme.primary,
    shadowColor: Color = Color.Black.copy(alpha = 0.30f),
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

    Box(
        modifier = modifier
            .clip(shape)
            .background(shadowColor)
            .offset(y = if (buttonState == ButtonState.Pressed) 0.dp else (-elevation))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape)
                .background(color)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = {
                        if (animateButtonClick) {
                            buttonState = ButtonState.Pressed
                        }
                        onClick()
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            buttonContent()
        }
    }
}