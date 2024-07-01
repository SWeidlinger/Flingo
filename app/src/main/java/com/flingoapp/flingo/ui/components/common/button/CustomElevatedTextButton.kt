package com.flingoapp.flingo.ui.components.common.button

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.flingoapp.flingo.ui.darken
import com.flingoapp.flingo.ui.theme.FlingoColors
import com.flingoapp.flingo.ui.theme.FlingoTheme

@Composable
fun CustomElevatedTextButton(
    modifier: Modifier = Modifier,
    text: String,
    elevation: Dp = 10.dp,
    showSpeakerIcon: Boolean = false,
    isPressed: Boolean = false,
    pressedColor: Color = Color.White,
    textColor: Color = FlingoColors.Text,
    addOutline: Boolean = true,
    isTextStrikethrough: Boolean = false,
    onClick: () -> Unit
) {
    CustomElevatedButton(
        modifier = modifier,
        elevation = elevation,
        addOutline = addOutline,
        backgroundColor = Color.White,
        shadowColor = Color.White.darken(0.3f),
        shape = RoundedCornerShape(15.dp),
        isPressed = isPressed,
        pressedColor = pressedColor,
        onClick = onClick,
        buttonContent = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = if (showSpeakerIcon) Modifier.fillMaxWidth() else Modifier
            ) {
                Text(
                    textAlign = TextAlign.Center,
                    text = text,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        textDecoration = if (isTextStrikethrough) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    color = textColor
                )

                if (showSpeakerIcon) {
                    Icon(
                        modifier = Modifier.align(alignment = Alignment.BottomEnd),
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        tint = textColor,
                        contentDescription = "Listen to description"
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun CustomWordButtonPreview() {
    FlingoTheme {
        CustomElevatedTextButton(
            text = "Testing",
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CustomWordButtonSpeakerPreview() {
    FlingoTheme {
        CustomElevatedTextButton(
            text = "Lies den Satz und klicke auf das unpassende Wort!",
            showSpeakerIcon = true,
            onClick = {}
        )
    }
}