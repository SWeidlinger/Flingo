package com.flingoapp.flingo.ui.component.topbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flingoapp.flingo.R
import com.flingoapp.flingo.ui.CustomPreview
import com.flingoapp.flingo.ui.component.button.CustomElevatedTextButton2
import com.flingoapp.flingo.ui.component.button.CustomIconButton
import com.flingoapp.flingo.ui.theme.FlingoTheme

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
    userProfileImage: String?,
    currentStreak: Int,
    currentLives: Int,
    onStreakClick: () -> Unit,
    onUserClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onSettingsLongClick: () -> Unit,
    onAwardClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(end = 24.dp, bottom = 16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val profileImage = when (userProfileImage) {
                "flingo_pink" -> painterResource(id = R.drawable.flingo_pink)
                else -> painterResource(id = R.drawable.flingo_orange)
            }

            // user profile button
            CustomElevatedTextButton2(
                textModifier = Modifier
                    .padding(4.dp)
                    .padding(horizontal = 4.dp),
                shape = RoundedCornerShape(
                    topEndPercent = 100,
                    bottomEndPercent = 100
                ),
                fontSize = 32.sp,
                text = userName,
                onClick = { onUserClick() },
                elevation = 6.dp,
                profileImage = profileImage
            )

            var iconButtonSize by remember { mutableStateOf(IntSize.Zero) }
            var lottieIconSize by remember { mutableStateOf(IntSize.Zero) }

//            Row(
//                modifier = Modifier
//                    .height(iconButtonSize.height.toDp())
//                    .background(
//                        color = FlingoColors.LightGray,
//                        shape = RoundedCornerShape(bottomStartPercent = 20, bottomEndPercent = 20)
//                    )
//                    .clickable { onStreakClick() }
//            ) {
//                LottieIconWithText(
//                    modifier = Modifier
//                        .onGloballyPositioned {
//                            lottieIconSize = it.size
//                        }
//                        .fillMaxHeight(),
//                    lottieModifier = Modifier
//                        .offset(0.dp, (-8).dp),
//                    lottieAnimation = R.raw.animation_fire_streak,
//                    animationSpeed = 0.3f,
//                    lottieOnRight = false,
//                    text = currentStreak.toString()
//                )
//
//                VerticalDivider(
//                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
//                    thickness = 2.dp,
//                    color = FlingoColors.Text.lighten(0.75f)
//                )
//
//                LottieIconWithText(
//                    modifier = Modifier
//                        .fillMaxHeight()
//                        .width(lottieIconSize.width.toDp()),
//                    lottieModifier = Modifier
//                        .offset(0.dp, (-2).dp)
//                        .padding(start = 8.dp),
//                    animationSpeed = 0.3f,
//                    lottieAnimation = R.raw.animation_heartbeat,
//                    lottieOnRight = true,
//                    text = currentLives.toString(),
//                )
//            }

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
                    onClick = { onSettingsClick() },
                    onLongClick = { onSettingsLongClick() }
                )
            }
        }
    }
}


@CustomPreview
@Composable
private fun CustomHomeScreenTopBarPreview() {
    FlingoTheme {
        CustomHomeScreenTopBar(
            userName = "User",
            currentStreak = 5,
            currentLives = 3,
            onUserClick = {},
            onSettingsClick = {},
            onSettingsLongClick = {},
            onStreakClick = {},
            onAwardClick = {},
            userProfileImage = "flingo_pink"
        )
    }
}