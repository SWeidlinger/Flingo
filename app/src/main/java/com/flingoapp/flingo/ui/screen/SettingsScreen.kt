package com.flingoapp.flingo.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flingoapp.flingo.data.model.genAi.GenAiModelPerformance
import com.flingoapp.flingo.data.model.genAi.GenAiProvider
import com.flingoapp.flingo.di.MainApplication
import com.flingoapp.flingo.navigation.NavigationAction
import com.flingoapp.flingo.ui.component.button.CustomElevatedTextButton2
import com.flingoapp.flingo.ui.component.button.CustomIconButton
import com.flingoapp.flingo.ui.theme.FlingoColors
import com.flingoapp.flingo.viewmodel.MainAction
import com.flingoapp.flingo.viewmodel.PersonalizationUiState

@Composable
fun SettingsScreen(
    onAction: (MainAction) -> Unit,
    onNavigate: (NavigationAction) -> Unit,
    personalizationUiState: PersonalizationUiState
) {
    Scaffold(topBar = {
        CustomIconButton(
            modifier = Modifier.padding(top = 16.dp, start = 16.dp),
            icon = Icons.AutoMirrored.Default.ArrowBack,
            iconContentDescription = "Go back",
            onClick = {
                onNavigate(NavigationAction.Up())
            }
        )
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(36.dp),
            verticalArrangement = Arrangement.spacedBy(64.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Row(modifier = Modifier.weight(1f)) {
                    GenAiProvider.entries.forEachIndexed { index, item ->
                        val isSelected = personalizationUiState.currentModel == item

                        CustomElevatedTextButton2(
                            modifier = Modifier.weight(1f),
                            text = item.provider,
                            shape = if (index == 0) RoundedCornerShape(
                                topStartPercent = 20,
                                bottomStartPercent = 20
                            ) else RoundedCornerShape(
                                topEndPercent = 20,
                                bottomEndPercent = 20
                            ),
                            elevation = 8.dp,
                            pressedColor = if (isSelected) FlingoColors.Primary else Color.White,
                            textColor = if (isSelected) Color.White else FlingoColors.Text,
                            isPressed = isSelected,
                            fontSize = 36.sp
                        ) {
                            onAction(MainAction.PersonalizationAction.ChangeModel(item))
                        }
                    }
                }

                val modelPerformance by MainApplication.appModule.genAiModule.modelPerformance.collectAsState()

                Row(modifier = Modifier.weight(1f)) {
                    GenAiModelPerformance.entries.forEachIndexed { index, item ->
                        val isSelected = modelPerformance == item

                        CustomElevatedTextButton2(
                            modifier = Modifier.weight(1f),
                            text = item.displayName,
                            shape = if (index == 0) RoundedCornerShape(
                                topStartPercent = 20,
                                bottomStartPercent = 20
                            ) else RoundedCornerShape(
                                topEndPercent = 20,
                                bottomEndPercent = 20
                            ),
                            elevation = 8.dp,
                            pressedColor = if (isSelected) FlingoColors.Primary else Color.White,
                            textColor = if (isSelected) Color.White else FlingoColors.Text,
                            isPressed = isSelected,
                            fontSize = 36.sp
                        ) {
                            onAction(MainAction.PersonalizationAction.ChangeModelPerformance(item))
                        }
                    }
                }
            }

            Box((Modifier.fillMaxSize())) {
                CustomElevatedTextButton2(
                    modifier = Modifier.fillMaxSize(),
                    textModifier = Modifier.align(Alignment.Center),
                    text = "Generate images",
                    elevation = 8.dp,
                    pressedColor = if (personalizationUiState.generateImages) FlingoColors.Primary else Color.White,
                    textColor = if (personalizationUiState.generateImages) Color.White else FlingoColors.Text,
                    isPressed = personalizationUiState.generateImages,
                    fontSize = 60.sp
                ) {
                    onAction(MainAction.PersonalizationAction.ToggleGenerateImages)
                }
            }
        }
    }
}