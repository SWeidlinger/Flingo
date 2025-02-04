package com.flingoapp.flingo.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flingoapp.flingo.ui.CustomPreview
import com.flingoapp.flingo.navigation.NavigationIntent
import com.flingoapp.flingo.ui.theme.FlingoTheme
import com.flingoapp.flingo.viewmodels.MainAction
import com.flingoapp.flingo.viewmodels.main.MainUiState

@Composable
fun ChallengeFinishedScreen(
    mainUiState: MainUiState,
    onAction: (MainAction) -> Unit,
    onNavigate: (NavigationIntent) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            
        }
    }
}

@CustomPreview
@Composable
private fun ChallengeFinishedScreenPreview() {
    FlingoTheme {
        ChallengeFinishedScreen(
            mainUiState = MainUiState(),
            onAction = {},
            onNavigate = {}
        )
    }
}