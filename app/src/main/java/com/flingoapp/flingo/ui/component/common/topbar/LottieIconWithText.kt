package com.flingoapp.flingo.ui.component.common.topbar

import androidx.annotation.RawRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
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
import com.flingoapp.flingo.ui.theme.FlingoTheme
import com.flingoapp.flingo.ui.toDp

@Composable
fun LottieIconWithText(
    modifier: Modifier = Modifier,
    lottieModifier: Modifier = Modifier,
    @RawRes lottieAnimation: Int,
    text: String,
    animationSpeed: Float = 1f,
    lottieAlignment: Alignment = Alignment.Center,
    lottieOnRight: Boolean = false
) {
    var textSize by remember { mutableStateOf(IntSize.Zero) }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(lottieAnimation))
        if (lottieOnRight) {
            Text(
                modifier = Modifier
                    .onGloballyPositioned { layoutCoordinates ->
                        textSize = layoutCoordinates.size
                    }
                    .padding(start = 16.dp),
                textAlign = TextAlign.Center,
                text = text,
                style = MaterialTheme.typography.headlineLarge,
                fontSize = 42.sp
            )
        }

        LottieAnimation(
            modifier = lottieModifier
                .height(textSize.height.toDp())
                .width(textSize.width.toDp()),
            composition = composition,
            iterations = LottieConstants.IterateForever,
            speed = animationSpeed,
            alignment = lottieAlignment
        )

        if (!lottieOnRight) {
            Text(
                modifier = Modifier
                    .onGloballyPositioned { layoutCoordinates ->
                        textSize = layoutCoordinates.size
                    }
                    .padding(end = 16.dp),
                textAlign = TextAlign.Center,
                text = text,
                style = MaterialTheme.typography.headlineLarge,
                fontSize = 42.sp
            )
        }
    }
}

@Preview
@Composable
private fun LottieIconWithTextPreview() {
    FlingoTheme {
        LottieIconWithText(
            lottieAnimation = R.raw.animation_fire_streak,
            text = "32",
            lottieOnRight = true
        )
    }
}