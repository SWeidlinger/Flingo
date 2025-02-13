package com.flingoapp.flingo.ui.challenge.quiz

import PageDetails
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flingoapp.flingo.data.model.MockData
import com.flingoapp.flingo.ui.AutoResizableText
import com.flingoapp.flingo.ui.CustomPreview
import com.flingoapp.flingo.ui.component.button.ButtonProgressAnimation
import com.flingoapp.flingo.ui.component.button.CustomElevatedButton
import com.flingoapp.flingo.ui.theme.FlingoColors
import com.flingoapp.flingo.ui.theme.FlingoTheme
import com.flingoapp.flingo.ui.toDp
import com.flingoapp.flingo.viewmodel.MainAction

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuizContentSingleChoice(
    modifier: Modifier = Modifier,
    pageDetails: PageDetails.Quiz,
    onAction: (MainAction) -> Unit,
    buttonProgressAnimation: ButtonProgressAnimation = ButtonProgressAnimation.LEFT_TO_RIGHT,
    onQuestionAnswered: (correctAnswer: Boolean) -> Unit,
) {
    var flowRowHeight by remember { mutableIntStateOf(0) }

    var isQuestionAnswered by remember { mutableStateOf(false) }
    var chosenAnswer by remember { mutableIntStateOf(-1) }

    FlowRow(
        modifier = modifier.onGloballyPositioned { layoutCoordinates ->
            flowRowHeight = layoutCoordinates.size.height
        },
        maxItemsInEachRow = 2,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.SpaceAround,
        maxLines = 2
    ) {
        pageDetails.answers.forEachIndexed { index, answer ->
            //wrapped in box in order to stop customElevatedButton from changing from modifiers
            Box(
                modifier = Modifier
                    .height(if (pageDetails.answers.size > 2) (flowRowHeight / 2).toDp() else flowRowHeight.toDp())
                    .weight(1f)
                    .padding(vertical = 4.dp)
            ) {
                CustomElevatedButton(
                    modifier = Modifier
                        .fillMaxSize(),
                    shape = RoundedCornerShape(24.dp),
                    buttonPressDurationMilli = 2000,
                    addOutline = true,
                    backgroundColor = Color.White,
                    pressedColor =
                    when {
                        isQuestionAnswered && chosenAnswer == index -> {
                            if (answer.isCorrect) FlingoColors.Success
                            else FlingoColors.Error
                        }

                        isQuestionAnswered -> Color.LightGray
                        else -> FlingoColors.Primary
                    },
                    isPressed = isQuestionAnswered,
                    progressAnimationType = buttonProgressAnimation,
                    elevation = 12.dp,
                    onClick = {
                        isQuestionAnswered = true
                        chosenAnswer = index
                        onQuestionAnswered(answer.isCorrect)
                    },
                    buttonContent = {
                        AutoResizableText(
                            modifier = Modifier
                                .fillMaxSize()
                                .wrapContentSize(Alignment.Center),
                            text = answer.answer,
                            fontSize = 48.sp,
                            color = FlingoColors.Text
                        )
                    }
                )
            }
        }
    }
}

@CustomPreview
@Composable
private fun QuizContentSingleChoicePreview2() {
    FlingoTheme {
        QuizContentSingleChoice(
            modifier = Modifier.fillMaxSize(),
            pageDetails = MockData.pageDetailsQuizSingleChoice.copy(
                answers = arrayListOf(
                    PageDetails.Quiz.Answer(id = 1, "Answer 1", true),
                    PageDetails.Quiz.Answer(id = 2, "Answer 2", false),
                )
            ),
            onAction = {},
            onQuestionAnswered = { _ -> }
        )
    }
}

@CustomPreview
@Composable
private fun QuizContentSingleChoicePreview3() {
    FlingoTheme {
        QuizContentSingleChoice(
            modifier = Modifier.fillMaxSize(),
            pageDetails = MockData.pageDetailsQuizSingleChoice.copy(
                answers = arrayListOf(
                    PageDetails.Quiz.Answer(id = 1, "Answer 1", true),
                    PageDetails.Quiz.Answer(id = 2, "Answer 2", false),
                    PageDetails.Quiz.Answer(id = 3, "Answer 3", false),
                )
            ),
            onAction = {},
            onQuestionAnswered = { _ -> }
        )
    }
}

@CustomPreview
@Composable
private fun QuizContentSingleChoicePreview4() {
    FlingoTheme {
        QuizContentSingleChoice(
            modifier = Modifier.fillMaxSize(),
            pageDetails = MockData.pageDetailsQuizSingleChoice.copy(
                answers = arrayListOf(
                    PageDetails.Quiz.Answer(id = 1, "Answer 1", true),
                    PageDetails.Quiz.Answer(id = 2, "Answer 2", false),
                    PageDetails.Quiz.Answer(id = 3, "Answer 3", false),
                    PageDetails.Quiz.Answer(id = 4, "Answer 4", false),
                )
            ),
            onAction = {},
            onQuestionAnswered = { _ -> }
        )
    }
}