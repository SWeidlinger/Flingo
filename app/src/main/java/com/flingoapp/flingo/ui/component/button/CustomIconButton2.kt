package com.flingoapp.flingo.ui.component.button

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.flingoapp.flingo.ui.darken
import com.flingoapp.flingo.ui.lighten
import com.flingoapp.flingo.ui.theme.FlingoColors
import com.flingoapp.flingo.ui.theme.FlingoTheme

/**
 * Custom icon button used specifically for the icon buttons used in the top app bar
 *
 * @param modifier
 * @param iconTint
 * @param backgroundColor
 * @param shadowColor
 * @param icon
 * @param iconContentDescription
 * @param elevation
 * @param enabled
 * @param isPressed
 * @param disabledColor
 * @param onClick
 * @receiver
 */
@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun CustomIconButton2(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.LightGray,
    iconTint: Color = backgroundColor.lighten(1f),
    shadowColor: Color = backgroundColor.darken(0.2f),
    icon: Painter? = null,
    iconImageVector: ImageVector? = null,
    iconScale: Float = 1.3f,
    iconContentDescription: String,
    elevation: Dp = 4.dp,
    enabled: Boolean = true,
    isPressed: Boolean = false,
    disabledColor: Color = backgroundColor.copy(alpha = 0.75f),
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null
) {
    CustomElevatedButton2(
        modifier = modifier,
        shape = CircleShape,
        addOutline = false,
        elevation = elevation,
        backgroundColor = backgroundColor,
        shadowColor = shadowColor,
        enabled = enabled,
        isPressed = isPressed,
        disabledColor = disabledColor,
        onClick = onClick,
        buttonContent = {
            if (iconImageVector != null) {
                Icon(
                    modifier = Modifier
                        .padding(16.dp)
                        .scale(iconScale),
                    imageVector = iconImageVector,
                    contentDescription = iconContentDescription,
                    tint = iconTint
                )
            } else {
                if (icon != null) {
                    Icon(
                        modifier = Modifier
                            .padding(16.dp)
                            .scale(iconScale),
                        painter = icon,
                        contentDescription = iconContentDescription,
                        tint = iconTint
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun CustomIconButton2Preview() {
    FlingoTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CustomIconButton2(
                iconImageVector = Icons.Default.QuestionMark,
                iconContentDescription = "Play",
                onClick = {}
            )

            CustomIconButton2(
                iconImageVector = Icons.Default.PlayArrow,
                iconContentDescription = "Play",
                backgroundColor = FlingoColors.Primary,
                onClick = {}
            )
        }

    }
}