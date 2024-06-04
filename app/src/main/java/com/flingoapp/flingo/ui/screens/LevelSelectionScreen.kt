package com.flingoapp.flingo.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flingoapp.flingo.R
import com.flingoapp.flingo.data.viewmodels.main.MainIntent
import com.flingoapp.flingo.data.viewmodels.main.MainUiState
import com.flingoapp.flingo.ui.CustomPreview
import com.flingoapp.flingo.ui.components.common.CustomIconButton
import com.flingoapp.flingo.ui.navigation.NavigationIntent
import com.flingoapp.flingo.ui.theme.FlingoTheme

@Composable
fun LevelSelectionScreen(
    bookIndex: Int,
    mainUiState: MainUiState,
    onAction: (MainIntent) -> Unit,
    onNavigate: (NavigationIntent) -> Unit
) {
    val currentBook = mainUiState.userData?.books?.get(bookIndex)

    Scaffold(
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
        topBar = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currentBook?.title ?: "Book Title",
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineLarge
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CustomIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        iconContentDescription = "Back",
                        backgroundColor = Color.LightGray,
                        onClick = { onNavigate(NavigationIntent.NavigateUp()) }
                    )

                    Row {
                        CustomIconButton(
                            icon = Icons.Default.Star,
                            iconPainter = painterResource(id = R.drawable.kid_star),
                            iconContentDescription = "Settings",
                            backgroundColor = Color.LightGray,
                            onClick = { }
                        )

                        Spacer(modifier = Modifier.padding(12.dp))

                        CustomIconButton(
                            icon = Icons.Default.Settings,
                            iconContentDescription = "Back",
                            backgroundColor = Color.LightGray,
                            onClick = { }
                        )
                    }
                }
            }
        }) { innerPadding ->
        if (currentBook?.levels.isNullOrEmpty()) {
            Text(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                text = "No levels found for book $bookIndex",
            )
        } else {
            LazyRow(modifier = Modifier.padding(innerPadding)) {

            }
        }
    }
}

@CustomPreview
@Composable
private fun LevelSelectionScreenPreview() {
    FlingoTheme {
        LevelSelectionScreen(
            bookIndex = 2,
            mainUiState = MainUiState(),
            onAction = {},
            onNavigate = {}
        )
    }
}