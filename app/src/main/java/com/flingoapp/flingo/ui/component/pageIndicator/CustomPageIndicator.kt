@file:OptIn(ExperimentalFoundationApi::class)

package com.flingoapp.flingo.ui.component.pageIndicator

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.flingoapp.flingo.data.model.MockData
import com.flingoapp.flingo.data.model.book.page.Page
import com.flingoapp.flingo.ui.CustomPreview
import com.flingoapp.flingo.ui.theme.FlingoColors
import com.flingoapp.flingo.ui.theme.FlingoTheme
import com.flingoapp.flingo.ui.toDp
import kotlinx.coroutines.launch

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
    pages: List<Page>,
    minSize: Dp = 16.dp,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    unselectedColor: Color = FlingoColors.LightGray
) {
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier.wrapContentHeight(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        pages.forEachIndexed { index, page ->
            val isCurrentPage = pagerState.currentPage == index
            val secondaryColor = if (page.isCompleted) FlingoColors.Success else unselectedColor
            val color = if (isCurrentPage) selectedColor else secondaryColor

            var indicatorHeight by remember { mutableIntStateOf(0) }

            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
            ) {
                androidx.compose.animation.AnimatedVisibility(visible = isCurrentPage) {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = "${index + 1}",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                }

                //TODO: add animation
                Box(
                    modifier = Modifier
                        .background(color = color, shape = CircleShape)
                        .width(indicatorHeight.toDp())
                        .sizeIn(
                            minWidth = minSize,
                            minHeight = if (isCurrentPage) minSize * 2f else minSize
                        )
                        .onGloballyPositioned { layoutCoordinates ->
                            indicatorHeight = layoutCoordinates.size.height
                        }
                ) {
                    if (isCurrentPage) {
                        Text(
                            modifier = Modifier
                                .padding(8.dp)
                                .align(Alignment.Center),
                            text = "${index + 1}",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@CustomPreview
@Composable
private fun CustomPageIndicatorPreview() {
    FlingoTheme {
        MockData.chapter.pages?.let {
            CustomPageIndicator(
                pages = it,
                pagerState = rememberPagerState(pageCount = { 3 })
            )
        }
    }
}
