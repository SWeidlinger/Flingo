package com.flingoapp.flingo.ui.component.challengeContent

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flingoapp.flingo.data.models.book.page.PageDetails
import com.flingoapp.flingo.ui.component.common.button.CustomElevatedButton
import com.flingoapp.flingo.ui.component.common.button.CustomElevatedTextButton
import com.flingoapp.flingo.ui.navigation.NavigationIntent
import com.flingoapp.flingo.ui.theme.FlingoColors
import com.flingoapp.flingo.viewmodels.main.MainIntent
import com.flingoapp.flingo.viewmodels.main.MainUiState
import kotlinx.coroutines.delay
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RemoveWordChallengeContent(
    modifier: Modifier = Modifier,
    mainUiState: MainUiState,
    onNavigate: (NavigationIntent) -> Unit,
    onAction: (MainIntent) -> Unit,
    pageDetails: PageDetails.RemoveWordPageDetails,
    onPageCompleted: (score: Int) -> Unit
) {
    var currentSelectedWord by remember { mutableStateOf("") }
    var isCorrectAnswer: Boolean? by remember { mutableStateOf(null) }
    var buttonColor by remember { mutableStateOf(FlingoColors.Primary) }
    var continueButtonText by remember { mutableStateOf("Fertig") }
    var continueButtonPressed by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = isCorrectAnswer) {
        if (isCorrectAnswer == false) {
            continueButtonPressed = true
            buttonColor = FlingoColors.Error
            delay(1000)
            currentSelectedWord = ""
            buttonColor = FlingoColors.Primary
            isCorrectAnswer = null
            continueButtonPressed = false
        } else if (isCorrectAnswer == true) {
            continueButtonPressed = true
            delay(1000)
            continueButtonPressed = false
            continueButtonText = "Weiter gehts!"
        }
    }

    Box {
        Column(
            modifier = modifier
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
                val words = pageDetails.content.split(" ")

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

                    Spacer(modifier = Modifier.padding(4.dp))
                }
            }

            CustomElevatedButton(
                elevation = 10.dp,
                shape = CircleShape,
                onClick = {
                    if (isCorrectAnswer == true) {
                        mainUiState.currentChapter?.isCompleted = true
                        onNavigate(NavigationIntent.Up())
                    } else if (currentSelectedWord != "") {
                        if (currentSelectedWord.removeSuffix(",") == pageDetails.answer) {
                            buttonColor = FlingoColors.Success
                            isCorrectAnswer = true
                        } else {
                            isCorrectAnswer = false
                            onAction(MainIntent.OnUserLiveDecrease)
                        }
                    }
                },
                isPressed = continueButtonPressed,
                backgroundColor = buttonColor,
                enabled = currentSelectedWord != "",
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