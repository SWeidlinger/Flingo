@file:OptIn(ExperimentalFoundationApi::class)

package com.flingoapp.flingo.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flingoapp.flingo.ui.theme.FlingoTheme


@Composable
fun GameConceptPageIndicator(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    gameConceptList: List<String>
) {
    Row(
        modifier
            .wrapContentHeight()
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pagerState.pageCount) { iteration ->
            val isCurrentPage = pagerState.currentPage == iteration
            val color = if (isCurrentPage) MaterialTheme.colorScheme.primary else Color.LightGray

            //TODO: add animation
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(CircleShape)
                    .background(color)
                    .sizeIn(minWidth = 16.dp, minHeight = 16.dp)
            ) {
                if (isCurrentPage) {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = gameConceptList[iteration],
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GameConceptPageIndicatorPreview() {
    FlingoTheme {
        GameConceptPageIndicator(
            pagerState = rememberPagerState(pageCount = { 3 }),
            gameConceptList = listOf("Game Concept 1", "Game Concept 2", "Game Concept 3")
        )
    }
}
