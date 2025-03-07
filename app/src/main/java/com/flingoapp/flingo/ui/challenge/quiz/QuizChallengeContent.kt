package com.flingoapp.flingo.ui.challenge.quiz

import PageDetails
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flingoapp.flingo.data.model.MockData
import com.flingoapp.flingo.navigation.NavigationAction
import com.flingoapp.flingo.ui.AutoResizableText
import com.flingoapp.flingo.ui.CustomPreview
import com.flingoapp.flingo.ui.component.button.ButtonProgressAnimation
import com.flingoapp.flingo.ui.theme.FlingoColors
import com.flingoapp.flingo.ui.theme.FlingoTheme
import com.flingoapp.flingo.viewmodel.MainAction
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

/**
 * Quiz challenge content
 *
 * @param modifier
 * @param onNavigate
 * @param onAction
 * @param pageDetails
 * @param taskDefinitionTopBarWidth
 * @param onPageCompleted
 * @receiver
 * @receiver
 * @receiver
 */
@Composable
fun QuizChallengeContent(
    modifier: Modifier = Modifier,
    onNavigate: (NavigationAction) -> Unit,
    onAction: (MainAction) -> Unit,
    pageDetails: PageDetails.Quiz,
    taskDefinitionTopBarWidth: Dp,
    onPageCompleted: (score: Int) -> Unit
) {
    var textBoxBorderColor by remember { mutableStateOf(FlingoColors.Text) }
    var isAnswerCorrect by remember { mutableStateOf(false) }
    var latestTouchPointOffset by remember { mutableStateOf(Offset.Zero) }

    //TODO: remove after testing
    var buttonProgressAnimation by remember { mutableStateOf(ButtonProgressAnimation.entries.random()) }

    Box(modifier = Modifier.pointerInput(Unit) {
        // get position of last press to move possible confetti source to that position
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent()
                val position = event.changes.first().position
                latestTouchPointOffset = position
            }
        }
    }) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .width(taskDefinitionTopBarWidth)
                    .weight(1f)
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .border(
                        width = 4.dp,
                        color = textBoxBorderColor,
                        shape = RoundedCornerShape(24.dp)
                    )
            ) {
                AutoResizableText(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 16.dp)
                        .clickable {
                            //TODO: remove just used for testing reasons
                            val currentAnimationIndex =
                                ButtonProgressAnimation.entries.find { it == buttonProgressAnimation }?.ordinal
                                    ?: 0
                            val nextAnimationIndex =
                                (currentAnimationIndex + 1) % ButtonProgressAnimation.entries.size

                            buttonProgressAnimation =
                                ButtonProgressAnimation.entries[nextAnimationIndex]

                            Log.e(
                                "QuizChallengeContent",
                                "Current Button animation:$buttonProgressAnimation"
                            )
                        },
                    text = pageDetails.question,
                    style = MaterialTheme.typography.headlineLarge,
                    color = FlingoColors.Text,
                    fontSize = 48.sp
                )
            }

            when (pageDetails.quizType) {
                PageDetails.Quiz.QuizType.TRUE_OR_FALSE -> {
                    QuizContentTrueOrFalse(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(taskDefinitionTopBarWidth)
                            .weight(1f),
                        pageDetails = pageDetails,
                        onAction = onAction,
                        onQuestionAnswered = { isCorrectAnswer ->
                            if (!isCorrectAnswer) {
                                onAction(MainAction.UserAction.DecreaseLives)
                            }
                            isAnswerCorrect = isCorrectAnswer
                            textBoxBorderColor =
                                if (isCorrectAnswer) {
                                    FlingoColors.Success
                                } else {
                                    FlingoColors.Error
                                }
                            //TODO: implement score calculation
                            onPageCompleted(0)
                        }
                    )
                }

                PageDetails.Quiz.QuizType.SINGLE_CHOICE -> {
                    QuizContentSingleChoice(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(taskDefinitionTopBarWidth)
                            .weight(2f),
                        pageDetails = pageDetails,
                        onAction = onAction,
                        buttonProgressAnimation = buttonProgressAnimation,
                        onQuestionAnswered = { isCorrectAnswer ->
                            if (!isCorrectAnswer) {
                                onAction(MainAction.UserAction.DecreaseLives)
                            }
                            isAnswerCorrect = isCorrectAnswer
                            textBoxBorderColor =
                                if (isCorrectAnswer) {
                                    FlingoColors.Success
                                } else {
                                    FlingoColors.Error
                                }
                            //TODO: implement score calculation
                            onPageCompleted(0)
                        }
                    )
                }
            }
        }

        if (isAnswerCorrect) {
            //TODO: emission of this could be changed
            KonfettiView(
                modifier = Modifier.fillMaxSize(),
                parties = listOf(
                    Party(
                        position = Position.Absolute(
                            latestTouchPointOffset.x,
                            latestTouchPointOffset.y
                        ),
                        emitter = Emitter(duration = 1000, TimeUnit.MILLISECONDS).perSecond(
                            500
                        )
                    )
                )
            )
        }
    }
}

@CustomPreview
@Composable
private fun QuizChallengeContentSingleChoicePreview() {
    FlingoTheme {
        QuizChallengeContent(
            modifier = Modifier.fillMaxSize(),
            onNavigate = {},
            onAction = {},
            pageDetails = MockData.pageDetailsQuizSingleChoice,
            taskDefinitionTopBarWidth = 1000.dp,
            onPageCompleted = {}
        )
    }
}

@CustomPreview
@Composable
private fun QuizChallengeContentTrueOrFalsePreview() {
    FlingoTheme {
        QuizChallengeContent(
            modifier = Modifier.fillMaxSize(),
            onNavigate = {},
            onAction = {},
            pageDetails = MockData.pageDetailsQuizTrueOrFalse,
            taskDefinitionTopBarWidth = 1000.dp,
            onPageCompleted = {}
        )
    }
}