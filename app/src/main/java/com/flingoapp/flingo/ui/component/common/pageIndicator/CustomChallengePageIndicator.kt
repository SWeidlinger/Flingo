package com.flingoapp.flingo.ui.component.common.pageIndicator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.flingoapp.flingo.ui.component.common.button.CustomIconButton
import com.flingoapp.flingo.ui.theme.FlingoColors
import kotlinx.coroutines.launch

@Composable
fun CustomChallengePageIndicator(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    showPageControlButtons: Boolean
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val coroutineScope = rememberCoroutineScope()

        if (pagerState.pageCount > 1) {
            if (showPageControlButtons) {
                val isFirstPage by remember {
                    derivedStateOf { pagerState.currentPage == 0 }
                }

                CustomIconButton(
                    modifier = Modifier,
                    shape = RoundedCornerShape(24.dp),
                    icon = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                    iconContentDescription = "Previous Question",
                    backgroundColor = if (isFirstPage) Color.LightGray else FlingoColors.Primary,
                    enabled = !isFirstPage
                ) {
                    if (isFirstPage) return@CustomIconButton
                    val previousPage = pagerState.currentPage - 1
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(previousPage)
                    }
                }
            }

            CustomPageIndicator(
                modifier = Modifier,
                pagerState = pagerState
            )

            if (showPageControlButtons) {
                val isLastPage by remember {
                    derivedStateOf { pagerState.currentPage + 1 == pagerState.pageCount }
                }

                CustomIconButton(
                    modifier = Modifier,
                    shape = RoundedCornerShape(24.dp),
                    icon = Icons.AutoMirrored.Default.KeyboardArrowRight,
                    iconContentDescription = "Next Question",
                    backgroundColor = if (isLastPage) Color.LightGray else FlingoColors.Primary,
                    enabled = !isLastPage
                ) {
                    if (isLastPage) return@CustomIconButton
                    val nextPage = pagerState.currentPage + 1
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(nextPage)
                    }
                }
            }
        }
    }
}