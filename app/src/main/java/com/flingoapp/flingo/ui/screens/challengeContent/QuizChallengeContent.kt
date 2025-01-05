package com.flingoapp.flingo.ui.screens.challengeContent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flingoapp.flingo.data.models.book.page.PageDetails
import com.flingoapp.flingo.ui.components.common.button.CustomElevatedTextButton
import com.flingoapp.flingo.ui.navigation.NavigationIntent
import com.flingoapp.flingo.viewmodels.main.MainUiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuizChallengeContent(
    modifier: Modifier = Modifier,
    mainUiState: MainUiState,
    onNavigate: (NavigationIntent) -> Unit,
    pageDetails: PageDetails.QuizPageDetails,
    pagerState: PagerState
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .background(
                    Color.LightGray,
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = pageDetails.question,
                fontSize = 36.sp
            )
        }

        when (pageDetails.quizType) {
            PageDetails.QuizPageDetails.Companion.QuizType.TRUE_OR_FALSE -> {

            }

            PageDetails.QuizPageDetails.Companion.QuizType.SINGLE_CHOICE -> {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    maxItemsInEachRow = 2,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    pageDetails.answers.forEachIndexed { index, answer ->
                        CustomElevatedTextButton(
                            text = answer.answer,
                            onClick = {}
                        )
                    }
                }
            }
        }
    }
}