package com.flingoapp.flingo.ui.component.button

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flingoapp.flingo.ui.darken
import com.flingoapp.flingo.ui.theme.FlingoColors
import com.flingoapp.flingo.ui.theme.FlingoTheme
import com.flingoapp.flingo.ui.toDp

/**
 * Custom elevated text button, specifically created to be used with text, offers streamlined approach by
 * using [CustomElevatedButton] and add additional improvement useful for showing text in such a button
 *
 * @param modifier
 * @param text
 * @param elevation
 * @param showSpeakerIcon used for the description of the pages as these are read aloud, additional
 * indication is shown by a speaker icon
 * @param isPressed
 * @param pressedColor
 * @param textColor
 * @param fontSize
 * @param addOutline
 * @param isTextStrikethrough if the text should appear struck through
 * @param onClick
 */
@Composable
fun CustomElevatedTextButton2(
    modifier: Modifier = Modifier,
    textModifier: Modifier = modifier,
    @DrawableRes icon: Int? = null,
    profileImage: Painter? = null,
    text: String,
    elevation: Dp = 8.dp,
    showSpeakerIcon: Boolean = false,
    fill: Boolean = false,
    isPressed: Boolean = false,
    pressedColor: Color = Color.White,
    textColor: Color = FlingoColors.Text,
    shape: Shape = RoundedCornerShape(20),
    fontSize: TextUnit = 48.sp,
    addOutline: Boolean = true,
    isTextStrikethrough: Boolean = false,
    backgroundColor: Color = Color.White,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    CustomElevatedButton2(
        modifier = modifier,
        elevation = elevation,
        addOutline = addOutline,
        backgroundColor = backgroundColor,
        shadowColor = backgroundColor.darken(0.3f),
        enabled = enabled,
        shape = shape,
        isPressed = isPressed,
        pressedColor = pressedColor,
        onClick = onClick,
        buttonContent = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = if (showSpeakerIcon || fill) textModifier
                    .fillMaxWidth()
                    .padding(16.dp) else textModifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    icon?.let {
                        Icon(
                            painter = painterResource(it),
                            contentDescription = "Model icon",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(fontSize.toDp())
                        )
                    }

                    Text(
                        modifier = textModifier,
                        textAlign = TextAlign.Center,
                        text = text,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = fontSize,
                            textDecoration = if (isTextStrikethrough) TextDecoration.LineThrough else TextDecoration.None
                        ),
                        color = textColor
                    )

                    profileImage?.let {
                        Image(
                            modifier = Modifier
                                .padding(start = 6.dp)
                                .size(fontSize.toDp() * 1.5f),
                            painter = it,
                            contentDescription = "Profile Image",
                        )
                    }
                }

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
private fun CustomWordButton2Preview() {
    FlingoTheme {
        CustomElevatedTextButton2(
            text = "Testing",
            fontSize = 24.sp,
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CustomWordButton2SpeakerPreview() {
    FlingoTheme {
        CustomElevatedTextButton2(
            text = "Lies den Satz und klicke auf das unpassende Wort!",
            showSpeakerIcon = true,
            onClick = {}
        )
    }
}