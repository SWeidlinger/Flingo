package com.flingoapp.flingo.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flingoapp.flingo.navigation.NavigationIntent
import com.flingoapp.flingo.ui.theme.FlingoColors

@Composable
fun StreakAndStarsScreen(
    onNavigate: (NavigationIntent) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .background(
                    color = FlingoColors.LightGray,
                    shape = RoundedCornerShape(topStartPercent = 20, topEndPercent = 20)
                ),
            onClick = {
                onNavigate(NavigationIntent.Up())
            },
            content = {
                Icon(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = "Go back",
                )
            }
        )
    }
    Text(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center),
        text = "StreakAndStarsScreen"
    )
}