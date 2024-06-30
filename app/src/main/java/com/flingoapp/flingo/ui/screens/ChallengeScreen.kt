package com.flingoapp.flingo.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flingoapp.flingo.ui.components.common.button.CustomElevatedButton
import com.flingoapp.flingo.ui.components.common.button.CustomElevatedTextButton
import com.flingoapp.flingo.ui.components.common.topbar.CustomChallengeTopBar
import com.flingoapp.flingo.ui.navigation.NavigationIntent
import com.flingoapp.flingo.ui.theme.FlingoPrimary
import com.flingoapp.flingo.ui.theme.FlingoText
import com.flingoapp.flingo.ui.theme.FlingoTheme
import com.flingoapp.flingo.viewmodels.main.MainIntent
import com.flingoapp.flingo.viewmodels.main.MainUiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChallengeScreen(
    mainUiState: MainUiState,
    onAction: (MainIntent) -> Unit,
    onNavigate: (NavigationIntent) -> Unit
) {
    //TODO: add pager so more than one page can be shown
    val pages = mainUiState.currentChapter?.pages ?: return

    Scaffold(topBar = {
        CustomChallengeTopBar(
            description = pages[0].description ?: "Chapter Title",
            hint = pages[0].hint,
            navigateUp = { onNavigate(NavigationIntent.NavigateUp()) }
        )
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var currentSelectedWord by remember { mutableStateOf("") }

            FlowRow(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 64.dp)
                    .wrapContentSize(Alignment.Center),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                val words = pages[0].content.split(" ")

                words.forEach { word ->
                    CustomElevatedTextButton(
                        text = word,
                        isPressed = word == currentSelectedWord,
                        pressedColor = FlingoPrimary,
                        addOutline = word != currentSelectedWord,
                        textColor = if (word == currentSelectedWord) Color.White else FlingoText,
                        isTextStrikethrough = word == currentSelectedWord,
                        onClick = { currentSelectedWord = word }
                    )

                    Spacer(modifier = Modifier.padding(5.dp))
                }
            }

            CustomElevatedButton(
                elevation = 10.dp,
                shape = CircleShape,
                onClick = {
                    if (currentSelectedWord == pages[0].answer) {
                        //TODO: show congratulations popup
                    } else {
                        currentSelectedWord = ""
                    }
                },
                buttonContent = {
                    Text(
                        modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp),
                        text = "Fertig",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 38.sp)
                    )
                }
            )
        }
    }
}

@Preview
@Composable
private fun ChallengeScreenPreview() {
    FlingoTheme {

    }
}