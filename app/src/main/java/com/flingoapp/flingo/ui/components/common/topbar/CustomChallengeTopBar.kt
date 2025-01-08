package com.flingoapp.flingo.ui.components.common.topbar

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flingoapp.flingo.ui.components.common.button.CustomElevatedTextButton
import com.flingoapp.flingo.ui.components.common.button.CustomIconButton
import com.flingoapp.flingo.ui.theme.FlingoTheme
import java.util.Locale

/**
 * Custom challenge top bar, used for challenges as it offers text to speech functionality for reading the
 * description of the level out loud
 *
 * @param modifier
 * @param taskDefinition
 * @param hint
 * @param navigateUp
 * @receiver
 */
@Composable
fun CustomChallengeTopBar(
    modifier: Modifier = Modifier,
    taskDefinition: String,
    hint: String,
    navigateUp: () -> Unit,
    taskDefinitionWidth: (Int) -> Unit = { },
) {
    val tts = rememberTextToSpeech()
    var isSpeaking by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // back navigation
        CustomIconButton(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            iconContentDescription = "Back",
            backgroundColor = Color.LightGray,
            onClick = { navigateUp() }
        )

        CustomElevatedTextButton(
            modifier = Modifier
                .weight(1f)
                .onGloballyPositioned { layoutCoordinates ->
                    taskDefinitionWidth(layoutCoordinates.size.width)
                },
            elevation = 5.dp,
            text = taskDefinition,
            showSpeakerIcon = true,
            onClick = {
                isSpeaking = false
                if (tts.value?.isSpeaking == true) {
                    tts.value?.stop()
                    isSpeaking = false
                } else {
                    tts.value?.speak(
                        taskDefinition, TextToSpeech.QUEUE_FLUSH, null, ""
                    )
                    isSpeaking = true
                }
            }
        )

        CustomIconButton(
            icon = Icons.Default.QuestionMark,
            iconContentDescription = "Hint",
            backgroundColor = MaterialTheme.colorScheme.secondary,
            onClick = {
                isSpeaking = false
                if (tts.value?.isSpeaking == true) {
                    tts.value?.stop()
                    isSpeaking = false
                } else {
                    tts.value?.speak(
                        hint, TextToSpeech.QUEUE_FLUSH, null, ""
                    )
                    isSpeaking = true
                }
            }
        )
    }
}

/**
 * Remember text to speech, used to handle the text to speech object, as well as creating and disposing of it
 *
 * @return TextToSpeech object
 */
@Composable
fun rememberTextToSpeech(): MutableState<TextToSpeech?> {
    val context = LocalContext.current
    val tts = remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(context) {
        val textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.value?.language = Locale.GERMAN
            }
        }
        tts.value = textToSpeech

        onDispose {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }

    return tts
}


@Preview(showBackground = true)
@Composable
private fun CustomChallengeTopBarPreview() {
    FlingoTheme {
        CustomChallengeTopBar(
            taskDefinition = "Lies den Satz und klicke auf das unpassende Wort!",
            hint = "",
            navigateUp = { /*TODO*/ }
        )
    }
}