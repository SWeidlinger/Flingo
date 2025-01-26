package com.flingoapp.flingo.ui.component.common.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.flingoapp.flingo.R
import com.flingoapp.flingo.ui.component.common.HeartExplodable
import com.flingoapp.flingo.ui.component.common.button.CustomIconButton
import com.flingoapp.flingo.ui.theme.FlingoColors
import com.flingoapp.flingo.ui.theme.FlingoTheme
import com.flingoapp.flingo.ui.toDp

/**
 * Custom top bar, basic implementation of the TopBar which is used in [CustomChallengeTopBar] and
 * [CustomHomeScreenTopBar]
 *
 * @param modifier
 * @param title
 * @param navigateUp
 * @param onSettingsClick
 * @param onAwardClick
 */
@Composable
fun CustomTopBar(
    modifier: Modifier = Modifier,
    title: String,
    navigateUp: () -> Unit,
    hideAdditionalInformation: Boolean = false,
    showLives: Boolean = false,
    currentLives: Int = 0,
    onAwardClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // back navigation
            CustomIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                iconContentDescription = "Back",
                backgroundColor = Color.LightGray,
                onClick = { navigateUp() }
            )

            if (!hideAdditionalInformation) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var iconButtonSize by remember { mutableStateOf(IntSize.Zero) }

                    if (showLives) {
                        HeartExplodable(
                            modifier = Modifier
                                .height(iconButtonSize.height.toDp())
                                .background(color = FlingoColors.LightGray, shape = CircleShape)
                                .padding(horizontal = 4.dp),
                            currentLives = currentLives
                        )
                    }

                    // award button
                    CustomIconButton(
                        modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
                            iconButtonSize = layoutCoordinates.size
                        },
                        icon = Icons.Default.Star,
                        iconPainter = painterResource(id = R.drawable.kid_star),
                        iconContentDescription = "Awards",
                        backgroundColor = MaterialTheme.colorScheme.secondary,
                        onClick = { onAwardClick() }
                    )

                    // settings button
                    CustomIconButton(
                        icon = Icons.Default.Settings,
                        iconContentDescription = "Settings",
                        backgroundColor = Color.LightGray,
                        onClick = { onSettingsClick() }
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun CustomTopBarPreview() {
    FlingoTheme {
        CustomTopBar(
            title = "Title",
            navigateUp = {},
            onSettingsClick = {},
            onAwardClick = {}
        )
    }
}