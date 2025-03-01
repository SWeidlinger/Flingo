@file:OptIn(ExperimentalLayoutApi::class)

package com.flingoapp.flingo.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flingoapp.flingo.R
import com.flingoapp.flingo.navigation.NavigationAction
import com.flingoapp.flingo.ui.CustomPreview
import com.flingoapp.flingo.ui.component.button.CustomElevatedButton2
import com.flingoapp.flingo.ui.component.button.CustomElevatedTextButton
import com.flingoapp.flingo.ui.component.button.CustomElevatedTextButton2
import com.flingoapp.flingo.ui.theme.FlingoColors
import com.flingoapp.flingo.ui.theme.FlingoTheme
import com.flingoapp.flingo.viewmodel.MainAction
import com.flingoapp.flingo.viewmodel.UserUiState

@Composable
fun UserScreen(
    userUiState: UserUiState,
    onAction: (MainAction) -> Unit,
    onNavigate: (NavigationAction) -> Unit
) {
    val context = LocalContext.current

    val availableInterests = listOf(
        "Roboter",
        "Piraten",
        "Superhelden",
        "Monster",
        "Weltraum",
        "Magie",
        "Meereswelt",
        "Sport"
    )

    val availableImageStyles = listOf(
        "Comic",
        "Realistisch"
    )

    Scaffold(topBar = {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            CustomElevatedTextButton(
                modifier = Modifier.align(Alignment.TopEnd),
                fontSize = 32,
                elevation = 6.dp,
                shape = RoundedCornerShape(
                    topStartPercent = 20,
                    bottomStartPercent = 20
                ),
                onClick = { onNavigate(NavigationAction.Up()) },
                text = "Zurück"
            )
        }
    }) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 32.dp)
                .padding(bottom = 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                val profileImage = when (userUiState.profileImage) {
                    "flingo_pink" -> painterResource(id = R.drawable.flingo_pink)
                    else -> painterResource(id = R.drawable.flingo_orange)
                }

                CustomElevatedButton2(
                    shape = RoundedCornerShape(20),
                    backgroundColor = Color.White,
                    addOutline = true,
                    buttonContent = {
                        Column(
                            modifier = Modifier
                                .padding(32.dp)
                                .padding(horizontal = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                modifier = Modifier
                                    .weight(2f),
                                painter = profileImage,
                                contentDescription = "Profile Image"
                            )

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                            ) {
                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentSize(Alignment.Center)
                                        .padding(bottom = 8.dp),
                                    text = userUiState.name,
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontSize = 56.sp
                                )

                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentSize(Alignment.Center),
                                    text = "Alter: ${userUiState.age}",
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentSize(Alignment.Center),
                                    text = "Sprache: ${userUiState.language}",
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    },
                    onClick = {
                        onAction(MainAction.UserAction.SwitchUser(context = context))
                    }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(horizontal = 40.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .weight(2f)
                        .padding(bottom = 48.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.Center)
                            .padding(bottom = 16.dp),
                        text = "Wähle deine Interessen aus!",
                        style = MaterialTheme.typography.headlineLarge,
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxSize(),
                        maxItemsInEachRow = 2,
                        maxLines = 4,
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        UserInterest.entries.forEach { interest ->
                            val isSelected = userUiState.selectedInterests.contains(interest.displayName)

                            CustomElevatedTextButton2(
                                modifier = Modifier.weight(1f),
                                elevation = 6.dp,
                                fill = true,
                                fontSize = 36.sp,
                                text = interest.displayName,
                                pressedColor = if (isSelected) FlingoColors.Primary else Color.White,
                                textColor = if (isSelected) Color.White else FlingoColors.Text,
                                addOutline = !isSelected,
                                isPressed = isSelected,
                                onClick = {
                                    if (isSelected) {
                                        onAction(MainAction.UserAction.RemoveInterest(interest))
                                    } else {
                                        onAction(MainAction.UserAction.SelectInterest(interest))
                                    }
                                }
                            )
                        }
                    }
                }

                Column(modifier = Modifier) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.Center)
                            .padding(bottom = 16.dp),
                        text = "Wähle den Stil der Bilder aus!",
                        style = MaterialTheme.typography.headlineLarge,
                    )

                    FlowRow(
                        modifier = Modifier,
                        maxItemsInEachRow = 2,
                        maxLines = 1,
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        UserImageStyle.entries.forEach { imageStyle ->
                            val isSelected = userUiState.selectedImageStyle.contains(imageStyle.displayName)

                            CustomElevatedTextButton2(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                elevation = 6.dp,
                                fill = true,
                                fontSize = 36.sp,
                                text = imageStyle.displayName,
                                pressedColor = if (isSelected) FlingoColors.Primary else Color.White,
                                textColor = if (isSelected) Color.White else FlingoColors.Text,
                                addOutline = !isSelected,
                                isPressed = isSelected,
                                onClick = {
                                    if (!isSelected) {
                                        onAction(MainAction.UserAction.SelectImageStyle(imageStyle))
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

enum class UserInterest(val displayName: String, val prompt: String) {
    ROBOTS(displayName = "Roboter", prompt = "Roboter"),
    PIRATES(displayName = "Piraten", prompt = "Piraten"),
    SUPERHEROS(displayName = "Superhelden", prompt = "Superhelden"),
    MONSTER(displayName = "Monster", prompt = "Monster"),
    SPACE(displayName = "Weltraum", prompt = "Weltraum"),
    MAGIC(displayName = "Magie", prompt = "Magie"),
    SEAWORLD(displayName = "Meereswelt", prompt = "Meereswelt"),
    SPORT(displayName = "Sport", prompt = "Sport")
}

enum class UserImageStyle(val displayName: String, val prompt: String) {
    COMIC(displayName = "Comic", prompt = "Comic"),
    REALISTIC(displayName = "Realistisch", prompt = "Realistisch")
}

@CustomPreview
@Composable
private fun InterestSelectionScreenPreview() {
    FlingoTheme {
        UserScreen(
            userUiState = UserUiState(),
            onAction = {},
            onNavigate = {}
        )
    }
}