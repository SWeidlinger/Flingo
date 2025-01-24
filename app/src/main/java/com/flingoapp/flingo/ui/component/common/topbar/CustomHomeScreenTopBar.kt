package com.flingoapp.flingo.ui.component.common.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.flingoapp.flingo.R
import com.flingoapp.flingo.ui.component.common.button.CustomElevatedTextButton
import com.flingoapp.flingo.ui.component.common.button.CustomIconButton
import com.flingoapp.flingo.ui.theme.FlingoColors
import com.flingoapp.flingo.ui.theme.FlingoTheme
import com.flingoapp.flingo.ui.toDp

/**
 * Custom home screen top bar, used specifically for the HomeScreen, currently not showing the streak as it
 * is not implemented as of now
 *
 * @param modifier
 * @param userName
 * @param onUserClick
 * @param onSettingsClick
 * @param onAwardClick
 */
@Composable
fun CustomHomeScreenTopBar(
    modifier: Modifier = Modifier,
    userName: String,
    currentStreak: Int,
    onUserClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAwardClick: () -> Unit
) {
    var iconButtonSize by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // user profile button
            CustomElevatedTextButton(
                fontSize = 32,
                text = userName,
                onClick = { onUserClick() },
                elevation = 6.dp
            )

            var textSize by remember { mutableStateOf(IntSize.Zero) }

            Row(
                modifier = Modifier
                    .background(
                        color = FlingoColors.LightGray,
                        shape = RoundedCornerShape(100.dp)
                    )
                    .height(iconButtonSize.height.toDp())
                    .padding(start = 12.dp, end = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animation_fire_streak))
                LottieAnimation(
                    modifier = Modifier
                        .width(textSize.width.toDp() * 1.2f)
                        .height(textSize.height.toDp() * 1.2f)
                        .offset(0.dp, (-8).dp),
                    composition = composition,
                    iterations = LottieConstants.IterateForever,
                    speed = 0.25f,
                    alignment = Alignment.TopCenter
                )

                Text(
                    modifier = Modifier
                        .onGloballyPositioned { layoutCoordinates ->
                            textSize = layoutCoordinates.size
                        },
                    textAlign = TextAlign.Center,
                    text = currentStreak.toString(),
                    style = MaterialTheme.typography.headlineLarge,
                    fontSize = 42.sp
                )

                //TODO: could be used in other parts of the application
//            val context = LocalContext.current
//            Rive.init(context)
//            RiveAnimation(
//                modifier = Modifier.fillMaxSize(),
//                resId = R.raw.rive_flame
//            )
            }

            Row(
                modifier = Modifier
                    .onGloballyPositioned { layoutCoordinates ->
                        iconButtonSize = layoutCoordinates.size
                    },
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // award button
                CustomIconButton(
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


@Preview(showBackground = true)
@Composable
private fun CustomHomeScreenTopBarPreview() {
    FlingoTheme {
        CustomHomeScreenTopBar(
            userName = "User",
            currentStreak = 5,
            onUserClick = {},
            onSettingsClick = {},
            onAwardClick = {}
        )
    }
}