package com.flingoapp.flingo.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.flingoapp.flingo.ui.CustomPreview
import com.flingoapp.flingo.ui.component.CustomHighlightedText
import com.flingoapp.flingo.ui.theme.FlingoTheme
import kotlinx.coroutines.delay

@Composable
fun TempScreen(modifier: Modifier = Modifier) {
    val text = listOf("Danke", "für", "eure", "Aufmerksamkeit!")
    var currentWordIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(currentWordIndex) {
        delay(1500)
        currentWordIndex++
    }

    CustomHighlightedText(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center),
        content = text,
        currentWordIndex = currentWordIndex.coerceAtMost(text.lastIndex),
        textStyle = MaterialTheme.typography.headlineLarge.copy(
            fontSize = 80.sp,
            fontWeight = FontWeight.Bold
        ),
        highlightColor = Color(0xFFE5683D),
        shiftInitialValue = -1f,
        shiftTargetValue = 1f,
        shiftAnimationDuration = 2000,
        tiltInitialValue = -1f,
        tiltTargetValue = 1f,
        tiltAnimationDuration = 2000,
        inflateHorizontalInitialValue = 10f,
        inflateHorizontalTargetValue = 15f,
        inflateVerticalInitialValue = 10f,
        inflateVerticalTargetValue = 15f,
        inflateAnimationDuration = 2000,
        cornerRadiusInitialValue = 20f,
        cornerRadiusTargetValue = 30f,
        cornerRadiusAnimationDuration = 2000
    )
}

@CustomPreview
@Composable
private fun TempScreenPreview() {
    FlingoTheme {
        TempScreen()
    }
}