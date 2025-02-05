@file:OptIn(ExperimentalLayoutApi::class)

package com.flingoapp.flingo.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.flingoapp.flingo.ui.CustomPreview
import com.flingoapp.flingo.ui.component.button.CustomElevatedTextButton
import com.flingoapp.flingo.ui.component.topbar.CustomTopBar
import com.flingoapp.flingo.navigation.NavigationIntent
import com.flingoapp.flingo.ui.theme.FlingoColors
import com.flingoapp.flingo.ui.theme.FlingoTheme
import com.flingoapp.flingo.viewmodel.MainAction
import com.flingoapp.flingo.viewmodel.UserUiState

@Composable
fun InterestSelectionScreen(
    userUiState: UserUiState,
    onAction: (MainAction) -> Unit,
    onNavigate: (NavigationIntent) -> Unit
) {
    //TODO: include icons for the individual interests
    val availableInterests = listOf(
        "Roboter",
        "Piraten",
        "Dinosaurier",
        "Monster",
        "Weltraum",
        "Magie",
        "Meereswelt",
        "Sport"
    )

    Scaffold(topBar = {
        CustomTopBar(
            title = "Wähle deine Interesse aus!",
            hideAdditionalInformation = true,
            navigateUp = { onNavigate(NavigationIntent.Up()) },
            onSettingsClick = {},
            onAwardClick = {}
        )
    }) { innerPadding ->
        FlowRow(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 36.dp, vertical = 24.dp)
                .padding(bottom = 36.dp),
            maxItemsInEachRow = 2,
            maxLines = 4,
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            availableInterests.forEach { interest ->
                val isSelected = userUiState.selectedInterests.contains(interest)

                CustomElevatedTextButton(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxRowHeight(),
                    elevation = 6.dp,
                    fill = true,
                    text = interest,
                    pressedColor = if (isSelected) FlingoColors.Primary else Color.White,
                    textColor = if (isSelected) Color.White else FlingoColors.Text,
                    addOutline = !isSelected,
                    isPressed = isSelected,
                    onClick = {
                        onAction(MainAction.UserAction.SelectInterest(interest))
                    }
                )
            }
        }
    }
}

@CustomPreview
@Composable
private fun InterestSelectionScreenPreview() {
    FlingoTheme {
        InterestSelectionScreen(
            userUiState = UserUiState(),
            onAction = {},
            onNavigate = {}
        )
    }
}