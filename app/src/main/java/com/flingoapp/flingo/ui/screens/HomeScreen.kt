package com.flingoapp.flingo.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flingoapp.flingo.data.viewmodels.main.MainIntent
import com.flingoapp.flingo.data.viewmodels.main.MainUiState
import com.flingoapp.flingo.ui.CustomPreview
import com.flingoapp.flingo.ui.theme.FlingoTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    mainUiStateFlow: StateFlow<MainUiState>,
    onAction: (MainIntent) -> Unit
) {
    val mainUiState by mainUiStateFlow.collectAsState()

    val gameConceptList = listOf("Concept 1", "Concept 2", "Concept 3")
    val gameVariationList = listOf("1", "2", "3")

    val pagerState = rememberPagerState(pageCount = { gameConceptList.size })

    HorizontalPager(
        modifier = Modifier.fillMaxSize(),
        state = pagerState
    ) { page ->
        when (page) {
            1 -> {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 50.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    gameVariationList.forEach {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.LightGray)
                                .size(300.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 54.sp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            else -> {
                Column(verticalArrangement = Arrangement.Center) {
                    Text(
                        modifier = Modifier.fillMaxSize(),
                        text = "Work in Progress!",
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 54.sp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@CustomPreview
@Composable
private fun HomeScreenPreview() {
    FlingoTheme {
        HomeScreen(
            mainUiStateFlow = MutableStateFlow(MainUiState()),
            onAction = {}
        )
    }
}