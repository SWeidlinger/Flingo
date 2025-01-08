package com.flingoapp.flingo.ui.components.challengeContent

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flingoapp.flingo.data.models.book.page.PageDetails
import com.flingoapp.flingo.ui.components.challengeContent.quizContent.QuizContentSingleChoice
import com.flingoapp.flingo.ui.components.challengeContent.quizContent.QuizContentTrueOrFalse
import com.flingoapp.flingo.ui.navigation.NavigationIntent
import com.flingoapp.flingo.ui.theme.FlingoColors
import com.flingoapp.flingo.viewmodels.main.MainIntent
import com.flingoapp.flingo.viewmodels.main.MainUiState

@Composable
fun QuizChallengeContent(
    modifier: Modifier = Modifier,
    mainUiState: MainUiState,
    onNavigate: (NavigationIntent) -> Unit,
    onAction: (MainIntent) -> Unit,
    pageDetails: PageDetails.QuizPageDetails,
    taskDefinitionTopBarWidth: Dp
) {
    var textBoxBorderColor by remember { mutableStateOf(FlingoColors.Text) }

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
            //TODO: improve this animatedBorder

//                .animatedBorder(
//                    strokeWidth = 5.dp,
//                    shape = RoundedCornerShape(24.dp),
//                    durationMillis = 10000
//                )
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = pageDetails.question,
                color = FlingoColors.Text,
                fontSize = 36.sp
            )
        }

        when (pageDetails.quizType) {
            PageDetails.QuizPageDetails.Companion.QuizType.TRUE_OR_FALSE -> {
                QuizContentTrueOrFalse(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(taskDefinitionTopBarWidth)
                        .weight(1f),
                    pageDetails = pageDetails,
                    mainUiState = mainUiState,
                    onAction = onAction,
                    onQuestionAnswered = { isCorrectAnswer ->
                        textBoxBorderColor =
                            if (isCorrectAnswer) {
                                FlingoColors.Success
                            } else {
                                FlingoColors.Error
                            }
                    }
                )
            }

            PageDetails.QuizPageDetails.Companion.QuizType.SINGLE_CHOICE -> {
                QuizContentSingleChoice(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(taskDefinitionTopBarWidth)
                        .weight(2f),
                    pageDetails = pageDetails,
                    mainUiState = mainUiState,
                    onAction = onAction
                )
            }
        }
    }
}