package com.flingoapp.flingo.ui.component.topbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.flingoapp.flingo.R
import com.flingoapp.flingo.ui.CustomPreview
import com.flingoapp.flingo.ui.component.button.CustomIconButton
import com.flingoapp.flingo.ui.theme.FlingoTheme

/**
 * Custom reading top bar
 *
 * @param modifier
 * @param title
 * @param navigateUp
 * @param pageCount
 * @param pagerState
 * @param wordIndexList
 * @param wordCountList
 * @param onAwardClick
 * @param onSettingsClick
 * @param onSettingsLongClick
 * @receiver
 * @receiver
 * @receiver
 * @receiver
 */
@Composable
fun CustomReadingTopBar(
    modifier: Modifier = Modifier,
    title: String,
    navigateUp: () -> Unit,
    pageCount: Int,
    pagerState: PagerState,
    wordIndexList: List<Int>,
    wordCountList: List<Int>,
    onAwardClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onSettingsLongClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 16.dp),
        contentAlignment = Alignment.Center
    ) {
//        Text(
//            text = title,
//            modifier = Modifier
//                .fillMaxWidth()
//                .wrapContentWidth(Alignment.CenterHorizontally),
//            textAlign = TextAlign.Center,
//            style = MaterialTheme.typography.headlineLarge
//        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // back navigation
            CustomIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                iconContentDescription = "Back",
                backgroundColor = Color.LightGray,
                onClick = { navigateUp() }
            )

            ReadingProgressBar(
                modifier = Modifier
                    .weight(1f)
                    .height(75.dp),
                pageCount = pageCount,
                pagerState = pagerState,
                wordIndexList = wordIndexList,
                wordCountList = wordCountList
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                var iconButtonSize by remember { mutableStateOf(IntSize.Zero) }

                // award button
                CustomIconButton(
                    modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
                        iconButtonSize = layoutCoordinates.size
                    },
                    icon = Icons.Default.Star,
                    iconPainter = painterResource(id = R.drawable.kid_star),
                    iconContentDescription = "Awards",
                    backgroundColor = MaterialTheme.colorScheme.secondary,
                    onClick = { onAwardClick() }
                )

                // settings button
                CustomIconButton(
                    icon = Icons.Default.Settings,
                    iconContentDescription = "Settings",
                    backgroundColor = Color.LightGray,
                    onClick = { onSettingsClick() },
                    onLongClick = { onSettingsLongClick() }
                )
            }
        }
    }
}


@CustomPreview
@Composable
private fun CustomReadingTopBarPreview() {
    FlingoTheme {
        CustomReadingTopBar(
            title = "Title",
            navigateUp = {},
            onSettingsClick = {},
            pageCount = 3,
            pagerState = rememberPagerState { 3 },
            wordIndexList = listOf(100, 50, 0),
            wordCountList = listOf(100, 100, 100),
            onAwardClick = {}
        )
    }
}