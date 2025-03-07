package com.flingoapp.flingo.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.navOptions
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.flingoapp.flingo.R
import com.flingoapp.flingo.navigation.NavigationAction
import com.flingoapp.flingo.navigation.NavigationDestination
import com.flingoapp.flingo.ui.CustomPreview
import com.flingoapp.flingo.ui.component.button.CustomElevatedButton
import com.flingoapp.flingo.ui.theme.FlingoTheme
import com.flingoapp.flingo.viewmodel.BookUiState
import com.flingoapp.flingo.viewmodel.MainAction

/**
 * Challenge finished screen
 *
 * @param bookUiState
 * @param onAction
 * @param onNavigate
 * @receiver
 * @receiver
 */
@Composable
fun ChallengeFinishedScreen(
    bookUiState: BookUiState,
    onAction: (MainAction) -> Unit,
    onNavigate: (NavigationAction) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .wrapContentSize(Alignment.TopCenter)
                    .padding(top = 100.dp),
                verticalArrangement = Arrangement.spacedBy(36.dp)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Toll gemacht!",
                    style = MaterialTheme.typography.headlineLarge,
                    fontSize = 100.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Du hast das Kapitel abgeschlossen!",
                    style = MaterialTheme.typography.headlineLarge,
                    fontSize = 70.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )
            }

            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animation_celebration))

            LottieAnimation(
                modifier = Modifier.align(Alignment.BottomStart),
                composition = composition,
                iterations = LottieConstants.IterateForever
            )

            LottieAnimation(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .rotate(-90f),
                composition = composition,
                iterations = LottieConstants.IterateForever
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp)
            ) {
                CustomElevatedButton(
                    elevation = 10.dp,
                    shape = CircleShape,
                    onClick = {
                        onAction(MainAction.BookAction.CompleteChapter)

                        if (bookUiState.currentBookId != null) {
                            onNavigate(
                                NavigationAction.Screen(
                                    destination = NavigationDestination.ChapterSelection(
                                        bookId = bookUiState.currentBookId
                                    ),
                                    navOptions = navOptions {
                                        popUpTo(NavigationDestination.Home) {
                                            inclusive = false
                                        }
                                    }
                                )
                            )
                        }
                    },
                    buttonContent = {
                        Text(
                            modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp),
                            text = "Weiter gehts!",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 38.sp)
                        )
                    }
                )
            }
        }
    }
}

@CustomPreview
@Composable
private fun ChallengeFinishedScreenPreview() {
    FlingoTheme {
        ChallengeFinishedScreen(
            bookUiState = BookUiState(),
            onAction = {},
            onNavigate = {}
        )
    }
}