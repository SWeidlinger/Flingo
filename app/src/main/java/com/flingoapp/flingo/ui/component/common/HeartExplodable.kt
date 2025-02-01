package com.flingoapp.flingo.ui.component.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.flingoapp.flingo.R
import com.flingoapp.flingo.ui.CustomPreview
import com.flingoapp.flingo.ui.component.common.topbar.LottieIconWithText
import com.flingoapp.flingo.ui.theme.FlingoTheme
import dev.omkartenkale.explodable.Explodable
import dev.omkartenkale.explodable.ExplosionAnimationSpec
import dev.omkartenkale.explodable.rememberExplosionController
import kotlinx.coroutines.delay

@Composable
fun HeartExplodable(
    modifier: Modifier = Modifier,
    currentLives: Int,
    animationSpeed: Float = 0.3f
) {
    val explosionController = rememberExplosionController()
    var isLivesVisible by remember { mutableStateOf(true) }

    Explodable(
        modifier = modifier,
        controller = explosionController,
        animationSpec = ExplosionAnimationSpec(
            explosionPower = 5f,
            shakeDurationMs = 100,
            explosionDurationMs = 1000
        ),
        onExplode = {
            explosionController.reset()
            isLivesVisible = true
        }
    ) {
        AnimatedVisibility(
            visible = isLivesVisible,
            enter = scaleIn(),
            exit = ExitTransition.None
        ) {
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                var currentDisplayedLives by remember {
                    mutableIntStateOf(currentLives)
                }

                LaunchedEffect(currentLives) {
                    if (currentDisplayedLives != currentLives) {
                        explosionController.explode()
                        delay(1000)
                        currentDisplayedLives = currentLives
                        isLivesVisible = false
                    }
                }

                LottieIconWithText(
                    lottieAnimation = R.raw.animation_heartbeat,
                    text = currentDisplayedLives.toString(),
                    lottieModifier = Modifier.padding(start = 4.dp, end = 6.dp),
                    animationSpeed = animationSpeed,
                    lottieOnRight = true
                )
            }
        }
        AnimatedVisibility(
            visible = !isLivesVisible,
            enter = EnterTransition.None,
            exit = fadeOut() + scaleOut()
        ) {
            //placeholder if lives are not visible to not cause content shift
            Row(
                modifier = modifier.alpha(0f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                LottieIconWithText(
                    lottieAnimation = R.raw.animation_heartbeat,
                    text = currentLives.toString(),
                    lottieModifier = Modifier.padding(start = 4.dp, end = 6.dp),
                    animationSpeed = 0f,
                    lottieOnRight = true
                )
            }
        }
    }
}

@CustomPreview
@Composable
private fun HeartIconPreview() {
    FlingoTheme {
        HeartExplodable(
            currentLives = 3
        )
    }
}