package com.flingoapp.flingo.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.flingoapp.flingo.data.viewmodels.main.MainIntent
import com.flingoapp.flingo.data.viewmodels.main.MainUiState
import com.flingoapp.flingo.ui.navigation.NavigationIntent
import com.flingoapp.flingo.ui.theme.FlingoTheme
import kotlinx.coroutines.flow.StateFlow

@Composable
fun LevelSelectionScreen(
    bookIndex: Int,
    mainUiStateFlow: StateFlow<MainUiState>,
    onAction: (MainIntent) -> Unit,
    onNavigate: (NavigationIntent) -> Unit
) {
    val mainUiState by mainUiStateFlow.collectAsState()

    val levelList = mainUiState.userData?.books?.get(bookIndex)?.levels

    if (levelList.isNullOrEmpty()) {
        Text(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center),
            text = "No levels found for book $bookIndex",
        )
    } else {
        Text(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center),
            text = "Level Selection Screen with ${levelList.size} levels",
        )
    }
}

@Preview
@Composable
private fun LevelSelectionScreenPreview() {
    FlingoTheme {

    }
}