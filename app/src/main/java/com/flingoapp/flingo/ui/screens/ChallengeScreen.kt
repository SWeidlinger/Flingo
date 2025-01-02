package com.flingoapp.flingo.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.flingoapp.flingo.data.models.book.page.PageDetails
import com.flingoapp.flingo.data.models.book.page.PageType
import com.flingoapp.flingo.ui.components.common.topbar.CustomChallengeTopBar
import com.flingoapp.flingo.ui.navigation.NavigationIntent
import com.flingoapp.flingo.ui.screens.challengeTypes.RemoveWordChallenge
import com.flingoapp.flingo.ui.theme.FlingoTheme
import com.flingoapp.flingo.viewmodels.main.MainIntent
import com.flingoapp.flingo.viewmodels.main.MainUiState

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
    val pages = mainUiState.currentChapter?.pages

    if (pages == null) {
        Text(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center),
            text = "No pages for chapter ${mainUiState.currentChapter?.title}"
        )
    } else {
        //TODO: remove when pager for multiple pages is implemented
        val currentPage = pages[0]

        Scaffold(topBar = {
            CustomChallengeTopBar(
                description = currentPage.description,
                hint = currentPage.hint,
                navigateUp = { onNavigate(NavigationIntent.Up()) }
            )
        }) { innerPadding ->

            when (currentPage.type) {
                PageType.REMOVE_WORD -> {
                    RemoveWordChallenge(
                        modifier = Modifier.padding(innerPadding),
                        mainUiState = mainUiState,
                        onNavigate = onNavigate,
                        pageDetails = currentPage.details as PageDetails.RemoveWordPageDetails
                    )
                }

                PageType.CONTEXT_BASED_QUESTIONS -> TODO()
                PageType.ORDER_STORY -> TODO()
                else -> {
                    Log.e("ChallengeScreen", "Invalid PageType")
                }
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