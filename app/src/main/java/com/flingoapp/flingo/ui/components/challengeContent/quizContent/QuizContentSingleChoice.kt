package com.flingoapp.flingo.ui.components.challengeContent.quizContent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.flingoapp.flingo.data.models.book.page.PageDetails
import com.flingoapp.flingo.ui.components.common.button.ButtonProgressAnimationType
import com.flingoapp.flingo.ui.components.common.button.CustomElevatedButton
import com.flingoapp.flingo.ui.pxToDp
import com.flingoapp.flingo.ui.theme.FlingoColors
import com.flingoapp.flingo.viewmodels.main.MainIntent
import com.flingoapp.flingo.viewmodels.main.MainUiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuizContentSingleChoice(
    modifier: Modifier = Modifier,
    mainUiState: MainUiState,
    pageDetails: PageDetails.QuizPageDetails,
    onAction: (MainIntent) -> Unit,
    onQuestionAnswered: (correctAnswer: Boolean) -> Unit
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
        val buttonProgressAnimationType = remember { ButtonProgressAnimationType.entries.random() }

        pageDetails.answers.forEachIndexed { index, answer ->
            //wrapped in box in order to stop customElevatedButton from changing from modifiers
            Box(
                modifier = Modifier
                    .height((flowRowHeight / (pageDetails.answers.size / 2)).pxToDp())
                    .fillMaxSize()
                    .weight(1f)
                    .fillMaxRowHeight()
                    .padding(vertical = 4.dp)
            ) {
                var buttonHeight by remember { mutableIntStateOf(0) }

                val density = LocalDensity.current
                val fontSize by remember {
                    derivedStateOf {
                        with(density) {
                            (buttonHeight / 2).toSp()
                        }
                    }
                }

                CustomElevatedButton(
                    modifier = Modifier
                        .fillMaxSize()
                        .onGloballyPositioned {
                            buttonHeight = it.size.height
                        },
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
                    progressAnimationType = buttonProgressAnimationType,
                    elevation = 12.dp,
                    onClick = {
                        isQuestionAnswered = true
                        chosenAnswer = index
                        onQuestionAnswered(answer.isCorrect)
                    },
                    buttonContent = {
                        Text(
                            text = answer.answer,
                            fontSize = fontSize,
                            color = FlingoColors.Text
                        )
                    }
                )
            }
        }
    }
}