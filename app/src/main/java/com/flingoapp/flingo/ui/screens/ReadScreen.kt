package com.flingoapp.flingo.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flingoapp.flingo.ui.components.common.CustomTopBar
import com.flingoapp.flingo.ui.navigation.NavigationIntent
import com.flingoapp.flingo.ui.theme.FlingoTheme
import com.flingoapp.flingo.viewmodels.main.MainIntent
import com.flingoapp.flingo.viewmodels.main.MainUiState

@Composable
fun ReadScreen(
    mainUiState: MainUiState,
    onAction: (MainIntent) -> Unit,
    onNavigate: (NavigationIntent) -> Unit
) {
    Scaffold(topBar = {
        CustomTopBar(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            title = mainUiState.currentChapter?.title ?: "Chapter Title",
            navigateUp = { onNavigate(NavigationIntent.NavigateUp()) },
            onSettingsClick = {},
            onAwardClick = {}
        )
    }) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {

        }
    }
}

@Preview
@Composable
private fun ReadScreenPreview() {
    FlingoTheme {

    }
}