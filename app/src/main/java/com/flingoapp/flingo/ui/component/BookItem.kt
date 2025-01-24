package com.flingoapp.flingo.ui.component

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flingoapp.flingo.data.models.book.Book
import com.flingoapp.flingo.ui.component.common.button.CustomElevatedButton
import com.flingoapp.flingo.ui.innerShadow
import com.flingoapp.flingo.ui.theme.FlingoColors
import com.flingoapp.flingo.ui.theme.FlingoTheme
import kotlinx.coroutines.launch


/**
 * Book item used to display the different books on the [com.flingoapp.flingo.ui.screen.HomeScreen]
 *
 * @param modifier
 * @param itemSize
 * @param pagerState
 * @param bookIndex
 * @param currentBookItem
 * @param onClick
 */
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
    val context = LocalContext.current

    val noChaptersProvided by remember { derivedStateOf { (currentBookItem.chapters.isEmpty()) } }

    CustomElevatedButton(
        modifier = modifier.size(itemSize),
        shape = RoundedCornerShape(50.dp),
        backgroundColor = FlingoColors.LightGray,
        elevation = 20.dp,
        animateButtonClick = bookIndex == pagerState.currentPage,
        isPressed = bookIndex != pagerState.currentPage || noChaptersProvided,
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
            Box(contentAlignment = Alignment.Center) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 24.dp, start = 8.dp, end = 8.dp)
                        .blur(if (noChaptersProvided) 10.dp else 0.dp),
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val coverImageResourceId = context.resources.getIdentifier(
                        currentBookItem.coverImage ?: "flingo_red",
                        "drawable",
                        context.packageName
                    )

                    Image(
                        modifier = Modifier.weight(2f),
                        painter = painterResource(id = coverImageResourceId),
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

                    val maxAmountChapters =
                        if (noChaptersProvided) "-" else currentBookItem.chapters.size.toString()
                    val chaptersCompleted =
                        if (noChaptersProvided) "-" else currentBookItem.chapters.count { it.isCompleted }
                            .toString()

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$chaptersCompleted/$maxAmountChapters abgeschlossen",
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
                                    .background(FlingoColors.Success),
                                contentAlignment = Alignment.TopCenter
                            ) {
                                // progress bar shine effect
//                                Box(
//                                    modifier = Modifier
//                                        .fillMaxHeight(0.15f)
//                                        .fillMaxWidth(0.8f)
//                                        .offset(y = 8.dp)
//                                        .clip(RoundedCornerShape(50.dp))
//                                        .background(Color.White.copy(alpha = 0.7f))
//                                )
                            }
                        }
                    }
                }

                if (noChaptersProvided) {
                    Icon(
                        modifier = Modifier.size((itemSize.value / 1.75).dp),
                        tint = Color.Black,
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Chapter Locked"
                    )
                }
            }
        }
    )
}

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