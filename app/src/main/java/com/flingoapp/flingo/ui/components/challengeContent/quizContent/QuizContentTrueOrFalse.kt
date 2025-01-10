package com.flingoapp.flingo.ui.components.challengeContent.quizContent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
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
import com.flingoapp.flingo.data.models.book.page.PageDetails
import com.flingoapp.flingo.ui.components.common.button.CustomIconButton
import com.flingoapp.flingo.ui.pxToDp
import com.flingoapp.flingo.ui.theme.FlingoColors
import com.flingoapp.flingo.viewmodels.main.MainIntent
import com.flingoapp.flingo.viewmodels.main.MainUiState

@Composable
fun QuizContentTrueOrFalse(
    modifier: Modifier = Modifier,
    mainUiState: MainUiState,
    pageDetails: PageDetails.QuizPageDetails,
    onAction: (MainIntent) -> Unit,
    onQuestionAnswered: (correctAnswer: Boolean) -> Unit
) {
    var availableButtonHeight by remember { mutableIntStateOf(0) }
    //null == no button selected
    var isButtonTrueSelected: Boolean? by remember { mutableStateOf(null) }
    var isQuestionAnswered by remember { mutableStateOf(false) }
    //since only answer is correct for this quiz type
    val correctAnswer = pageDetails.answers.find { it.isCorrect }

    Row(
        modifier = modifier
            .onGloballyPositioned { layoutCoordinates ->
                availableButtonHeight = layoutCoordinates.size.height
            }
            .wrapContentSize(Alignment.Center),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(availableButtonHeight.pxToDp() / 2)
    ) {
        pageDetails.answers.forEachIndexed { index, answer ->
            val isButtonTrue = index == 0

            //TODO: could maybe use an animated border and long press to be selected
            CustomIconButton(
                modifier = Modifier,
                size = availableButtonHeight.pxToDp(),
                shape = CircleShape,
                elevation = 12.dp,
                backgroundColor =
                if (isQuestionAnswered) {
                    if (isButtonTrueSelected == true && isButtonTrue) {
                        FlingoColors.Success
                    } else if (isButtonTrueSelected == false && !isButtonTrue) {
                        FlingoColors.Error
                    } else {
                        Color.LightGray
                    }
                } else {
                    if (isButtonTrue) {
                        FlingoColors.Success
                    } else {
                        FlingoColors.Error
                    }
                },
                icon = if (isButtonTrue) Icons.Default.ThumbUp else Icons.Default.ThumbDown,
                iconScale = 0.6f,
                iconContentDescription = "Button ${if (isButtonTrue) "true" else "false"}",
                enabled = !isQuestionAnswered,
                onClick = {
                    if (isQuestionAnswered) return@CustomIconButton
                    isQuestionAnswered = true
                    isButtonTrueSelected = isButtonTrue

                    onQuestionAnswered(correctAnswer?.id == answer.id)
                }
            )
        }
    }
}