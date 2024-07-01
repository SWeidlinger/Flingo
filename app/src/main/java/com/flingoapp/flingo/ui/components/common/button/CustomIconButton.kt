package com.flingoapp.flingo.ui.components.common.button

import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.flingoapp.flingo.ui.darken

/**
 * Created by Sebastian on 04.06.2024.
 */

@Composable
fun CustomIconButton(
    modifier: Modifier = Modifier,
    size: Dp = 75.dp,
    iconTint: Color = Color.Black.copy(alpha = 0.4f),
    backgroundColor: Color = Color.Black.copy(alpha = 0.5f),
    shadowColor: Color = backgroundColor.darken(0.2f),
    icon: ImageVector,
    iconPainter: Painter? = null,
    iconContentDescription: String,
    elevation: Dp = 7.dp,
    enabled: Boolean = true,
    isPressed: Boolean = false,
    disabledColor: Color = Color.LightGray.copy(alpha = 0.75f),
    animateButtonClick: Boolean = true,
    //TODO: disabled for now since it causes lags
//    clickSound: Int? = R.raw.button_click,
    clickSound: Int? = null,
    onClick: () -> Unit
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
            .size(size)
            .clip(CircleShape)
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
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(backgroundColor)
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
                if (iconPainter != null) {
                    Icon(
                        modifier = Modifier
                            .fillMaxSize()
                            .scale(1.3f),
                        painter = iconPainter,
                        contentDescription = iconContentDescription,
                        tint = iconTint
                    )
                } else {
                    Icon(
                        modifier = Modifier
                            .fillMaxSize()
                            .scale(1.3f),
                        imageVector = icon,
                        contentDescription = iconContentDescription,
                        tint = iconTint
                    )
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
}

@Preview
@Composable
private fun CustomIconButtonPreview() {

}