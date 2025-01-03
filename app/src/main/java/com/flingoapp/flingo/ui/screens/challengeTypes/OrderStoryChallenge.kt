package com.flingoapp.flingo.ui.screens.challengeTypes

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun OrderStoryChallenge(modifier: Modifier = Modifier) {
    //TODO
    Text(
        "This is a order story challenge!",
        modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    )
}