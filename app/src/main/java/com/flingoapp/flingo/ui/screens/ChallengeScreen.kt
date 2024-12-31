package com.flingoapp.flingo.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flingoapp.flingo.ui.components.common.button.CustomElevatedButton
import com.flingoapp.flingo.ui.components.common.button.CustomElevatedTextButton
import com.flingoapp.flingo.ui.components.common.topbar.CustomChallengeTopBar
import com.flingoapp.flingo.ui.navigation.NavigationIntent
import com.flingoapp.flingo.ui.theme.FlingoColors
import com.flingoapp.flingo.ui.theme.FlingoTheme
import com.flingoapp.flingo.viewmodels.main.MainIntent
import com.flingoapp.flingo.viewmodels.main.MainUiState
import kotlinx.coroutines.delay
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

/**
 * Challenge screen used to display the different kind of challenges
 *
 * @param mainUiState
 * @param onAction
 * @param onNavigate
 * @receiver
 * @receiver
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChallengeScreen(
    mainUiState: MainUiState,
    onAction: (MainIntent) -> Unit,
    onNavigate: (NavigationIntent) -> Unit
) {
    //TODO: add pager so more than one page can be shown
    val pages = mainUiState.currentChapter?.pages ?: return

//    val state: KonfettiViewModel.State by viewModel.state.observeAsState(
//        KonfettiViewModel.State.Idle,
//    )

    var currentSelectedWord by remember { mutableStateOf("") }
    var isCorrectAnswer: Boolean? by remember { mutableStateOf(null) }
    var buttonColor by remember { mutableStateOf(FlingoColors.Primary) }
    var isContinueButtonEnabled by remember { mutableStateOf(true) }
    var continueButtonText by remember { mutableStateOf("Fertig") }
    var continueButtonPressed by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = isCorrectAnswer) {
        if (isCorrectAnswer == false) {
            buttonColor = FlingoColors.Error
            isContinueButtonEnabled = false
            delay(1000)
            currentSelectedWord = ""
            buttonColor = FlingoColors.Primary
            isCorrectAnswer = null
            isContinueButtonEnabled = true
        } else if (isCorrectAnswer == true) {
            continueButtonPressed = true
            delay(1000)
            continueButtonPressed = false
            continueButtonText = "Weiter gehts!"
        }
    }

    Scaffold(topBar = {
        CustomChallengeTopBar(
            description = pages[0].description ?: "Chapter Title",
            hint = pages[0].hint,
            navigateUp = { onNavigate(NavigationIntent.Up()) }
        )
    }) { innerPadding ->
        Box {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FlowRow(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 64.dp)
                        .wrapContentSize(Alignment.Center),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    val words = pages[0].content.split(" ")

                    words.forEach { word ->
                        CustomElevatedTextButton(
                            text = word,
                            isPressed = word == currentSelectedWord,
                            pressedColor = buttonColor,
                            addOutline = word != currentSelectedWord,
                            textColor = if (word == currentSelectedWord) Color.White else FlingoColors.Text,
                            isTextStrikethrough = word == currentSelectedWord,
                            onClick = {
                                if (isCorrectAnswer == null) {
                                    currentSelectedWord = word
                                }
                            }
                        )

                        Spacer(modifier = Modifier.padding(5.dp))
                    }
                }

                CustomElevatedButton(
                    elevation = 10.dp,
                    shape = CircleShape,
                    onClick = {
                        if (isCorrectAnswer == true) {
                            mainUiState.currentChapter.isCompleted = true
                            onNavigate(NavigationIntent.Up())
                        } else if (currentSelectedWord != "") {
                            if (currentSelectedWord.removeSuffix(",") == pages[0].answer) {
                                buttonColor = FlingoColors.Success
                                isCorrectAnswer = true
                            } else {
                                isCorrectAnswer = false
                            }
                        }
                    },
                    isPressed = continueButtonPressed,
                    backgroundColor = buttonColor,
                    buttonContent = {
                        Text(
                            modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp),
                            text = continueButtonText,
                            color = Color.White,
                            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 38.sp)
                        )
                    }
                )
            }

            if (isCorrectAnswer == true) {
                KonfettiView(
                    modifier = Modifier
                        .fillMaxHeight(0.45f)
                        .fillMaxWidth(0.2f)
                        .align(Alignment.BottomCenter),
                    parties = listOf(
                        Party(
                            spread = 100,
                            angle = 270,
                            emitter = Emitter(duration = 750, TimeUnit.MILLISECONDS).perSecond(150)
                        )
                    )
                )
            }
        }
    }
}

@Preview
@Composable
private fun ChallengeScreenPreview() {
    FlingoTheme {

    }
}