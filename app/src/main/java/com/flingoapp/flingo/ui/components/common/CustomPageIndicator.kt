@file:OptIn(ExperimentalFoundationApi::class)

package com.flingoapp.flingo.ui.components.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flingoapp.flingo.ui.pxToDp
import com.flingoapp.flingo.ui.theme.FlingoTheme

/**
 * Custom page indicator, used for displaying the amount of pages in a chapter
 *
 * @param modifier
 * @param pagerState
 * @param selectedColor
 * @param unselectedColor
 */
@Composable
fun CustomPageIndicator(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    unselectedColor: Color = Color.LightGray
) {
    Row(
        modifier
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pagerState.pageCount) { iteration ->
            val isCurrentPage = pagerState.currentPage == iteration
            val color = if (isCurrentPage) selectedColor else unselectedColor

            var indicatorHeight by remember { mutableIntStateOf(0) }

            //TODO: add animation
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(CircleShape)
                    .background(color)
                    .width(indicatorHeight.pxToDp())
                    .sizeIn(minWidth = 16.dp, minHeight = 16.dp)
                    .onGloballyPositioned { layoutCoordinates ->
                        indicatorHeight = layoutCoordinates.size.height
                    }
            ) {
                if (isCurrentPage) {
                    Text(
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.Center),
                        text = "${iteration + 1}",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CustomPageIndicatorPreview() {
    FlingoTheme {
        CustomPageIndicator(
            pagerState = rememberPagerState(pageCount = { 3 })
        )
    }
}
