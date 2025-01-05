package com.flingoapp.flingo.ui.screens.challengeContent

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flingoapp.flingo.data.models.book.page.PageDetails
import com.flingoapp.flingo.ui.navigation.NavigationIntent
import com.flingoapp.flingo.viewmodels.main.MainUiState

@Composable
fun QuizChallengeContent(
    modifier: Modifier = Modifier,
    mainUiState: MainUiState,
    onNavigate: (NavigationIntent) -> Unit,
    pageDetails: PageDetails.QuizPageDetails
) {

}