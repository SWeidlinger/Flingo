package com.flingoapp.flingo.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flingoapp.flingo.R
import com.flingoapp.flingo.data.models.book.Book
import com.flingoapp.flingo.ui.components.common.button.CustomElevatedButton
import com.flingoapp.flingo.ui.innerShadow
import com.flingoapp.flingo.ui.theme.FlingoTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookItem(
    modifier: Modifier = Modifier,
    itemSize: Dp,
    pagerState: PagerState,
    bookIndex: Int,
    currentBookItem: Book,
    onClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    CustomElevatedButton(
        modifier = modifier.size(itemSize),
        shape = RoundedCornerShape(50.dp),
        backgroundColor = Color(0xFFE0E0E0),
        elevation = 20.dp,
        animateButtonClick = bookIndex == pagerState.currentPage,
        isPressed = bookIndex != pagerState.currentPage,
        onClick = {
            if (bookIndex != pagerState.currentPage) {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(bookIndex)
                }
            } else {
                onClick()
            }
        },
        buttonContent = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 24.dp, start = 8.dp, end = 8.dp),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier.weight(2f),
                    painter = painterResource(id = R.drawable.flingo_red),
                    contentDescription = "Book Image Cover"
                )

                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 24.dp),
                    text = currentBookItem.title,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 48.sp,
                    ),
                    textAlign = TextAlign.Center
                )

                val noChaptersProvided = currentBookItem.chapters.isEmpty()
                val maxAmountChapters =
                    if (noChaptersProvided) "-" else currentBookItem.chapters.size.toString()
                val chaptersCompleted =
                    if (noChaptersProvided) "-" else currentBookItem.chapters.count { it.completed }.toString()

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$chaptersCompleted/$maxAmountChapters completed",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        textAlign = TextAlign.Center
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                            .height(35.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(50.dp))
                                .background(Color.White)
                                .innerShadow(
                                    shape = RoundedCornerShape(50.dp),
                                    color = Color.Black.copy(alpha = 0.5f),
                                    offsetY = 2.dp,
                                    offsetX = 2.dp,
                                    blur = 1.dp,
                                    spread = 2.dp
                                ),
                            contentAlignment = Alignment.CenterStart,
                        ) {

                        }

                        val completedPercentage = if (!noChaptersProvided) {
                            chaptersCompleted.toFloat() / maxAmountChapters.toFloat()
                        } else {
                            0f
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(completedPercentage)
                                .clip(RoundedCornerShape(50.dp))
                                .background(MaterialTheme.colorScheme.tertiary),
                            contentAlignment = Alignment.TopCenter
                        ) {
//                            Box(
//                                modifier = Modifier
//                                    .fillMaxHeight(0.15f)
//                                    .fillMaxWidth(0.8f)
//                                    .offset(y = 8.dp)
//                                    .clip(RoundedCornerShape(50.dp))
//                                    .background(Color.White.copy(alpha = 0.7f))
//                            )
                        }
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Preview(showBackground = true)
@Composable
private fun BookItemPreview() {
    FlingoTheme {
        BookItem(
            itemSize = 500.dp,
            pagerState = rememberPagerState(pageCount = { 3 }),
            bookIndex = 0,
            currentBookItem = Book(
                author = "Author",
                date = "Date",
                language = "Language",
                version = "Version",
                title = "Title",
                description = "Description",
                coverImage = "CoverImage",
                chapters = arrayListOf()
            ),
            onClick = {}
        )
    }
}