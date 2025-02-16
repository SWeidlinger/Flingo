package com.flingoapp.flingo.ui.screen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flingoapp.flingo.data.model.GenAiModel
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
        Row(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxHeight()
                .fillMaxWidth(0.5f)
                .padding(36.dp)
        ) {
            GenAiModel.entries.forEachIndexed { index, item ->
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
    }
}