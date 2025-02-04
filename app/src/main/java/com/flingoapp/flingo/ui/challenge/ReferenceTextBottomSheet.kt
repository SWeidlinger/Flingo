package com.flingoapp.flingo.ui.challenge

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.flingoapp.flingo.data.models.MockData
import com.flingoapp.flingo.data.models.book.page.Page
import com.flingoapp.flingo.data.models.book.page.PageDetails
import com.flingoapp.flingo.ui.CustomPreview
import com.flingoapp.flingo.ui.theme.FlingoColors
import com.flingoapp.flingo.ui.theme.FlingoTheme
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferenceTextBottomSheet(
    modifier: Modifier = Modifier,
    currentPage: Page,
    watchTime: Int = 10,
) {
    val referenceTitle: String
    val referenceText: String

    when (currentPage.details) {
        is PageDetails.QuizPageDetails -> {
            referenceTitle = currentPage.details.referenceTextTitle
            referenceText = currentPage.details.referenceText
        }

        is PageDetails.OrderStoryPageDetails -> {
            referenceTitle = currentPage.details.referenceTextTitle
            referenceText = currentPage.details.referenceText
        }

        else -> return
    }

    var showReferenceText by remember { mutableStateOf(false) }

    var openTime by remember { mutableFloatStateOf(watchTime.toFloat()) }

    LaunchedEffect(openTime) {
        if (showReferenceText && openTime > 0) {
            delay(100)
            openTime += 0.1f
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            showReferenceText = false
        }
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 48.dp)
                    .clickable {
                        showReferenceText = !showReferenceText
                    }
                    .background(
                        color = FlingoColors.LightGray,
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    )
                    .padding(8.dp)

            ) {
                Crossfade(showReferenceText) { showArrow ->
                    Icon(
                        modifier = Modifier.padding(16.dp),
                        imageVector = if (showArrow) Icons.Default.ArrowDownward else Icons.AutoMirrored.Default.Sort,
                        contentDescription = "Back",
                    )
                }
            }

            if (showReferenceText) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(
                            color = FlingoColors.LightGray,
                            shape = RoundedCornerShape(
                                topStart = 20.dp,
                                topEnd = 20.dp
                            )
                        ),
                    text = referenceText,
                    color = FlingoColors.Text,
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(35.dp)
                        .background(
                            color = FlingoColors.LightGray,
                            shape = RoundedCornerShape(50.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth((openTime / watchTime).toFloat())
                            .clip(RoundedCornerShape(50.dp))
                            .background(FlingoColors.Success)
                    )
                }
            }
        }
    }
}

@CustomPreview
@Composable
private fun ReferenceTextBottomSheetPreview() {
    FlingoTheme {
        ReferenceTextBottomSheet(
            currentPage = MockData.page
        )
    }
}