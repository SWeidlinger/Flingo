package com.flingoapp.flingo.ui.component.topbar

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.flingoapp.flingo.ui.CustomPreview
import com.flingoapp.flingo.ui.component.button.CustomElevatedButton2
import com.flingoapp.flingo.ui.theme.FlingoColors
import com.flingoapp.flingo.ui.theme.FlingoTheme
import kotlinx.coroutines.launch

@Composable
fun ReadingProgressBar(
    modifier: Modifier = Modifier,
    pageCount: Int,
    pagerState: PagerState,
    wordIndexList: List<Int>,
    wordCountList: List<Int>
) {
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        repeat(pageCount) { index ->
            val progressAnimation by animateFloatAsState(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                targetValue = (wordIndexList[index].toFloat() / wordCountList[index])
            )

            ReadingProgressBarSection(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                isPressed = pagerState.targetPage == index,
                progress = progressAnimation,
                indexPage = index + 1,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
    }
}

@Composable
fun ReadingProgressBarSection(
    modifier: Modifier = Modifier,
    isPressed: Boolean,
    progress: Float,
    indexPage: Int,
    onClick: () -> Unit
) {
    CustomElevatedButton2(
        modifier = modifier,
        buttonAlignment = Alignment.CenterStart,
        elevation = 6.dp,
        shape = RoundedCornerShape(50),
        onClick = onClick,
        isPressed = isPressed,
        backgroundColor = if (progress > 0.95f) FlingoColors.Success else FlingoColors.LightGray,
        buttonContent = {
            Box(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .background(
                            FlingoColors.Success,
                            shape = RoundedCornerShape(
                                topEndPercent = 50,
                                bottomEndPercent = 50
                            )
                        )
                        .fillMaxWidth(progress)
                )

                Text(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(
                            Alignment.Center
                        ),
                    style = MaterialTheme.typography.headlineLarge,
                    color = if (progress > 0.5f) Color.White else FlingoColors.Text,
                    text = indexPage.toString()
                )
            }
        }
    )
}

@CustomPreview
@Composable
private fun ReadingProgressBarPreview() {
    FlingoTheme {
        ReadingProgressBar(
            modifier = Modifier.height(50.dp),
            pageCount = 3,
            pagerState = rememberPagerState { 3 },
            wordIndexList = listOf(100, 50, 0, 0, 0),
            wordCountList = listOf(100, 100, 100, 100, 100)
        )
    }
}