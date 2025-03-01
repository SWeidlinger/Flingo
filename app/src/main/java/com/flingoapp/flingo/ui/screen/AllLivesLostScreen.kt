package com.flingoapp.flingo.ui.screen

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.rive.runtime.kotlin.core.Rive
import com.flingoapp.flingo.R
import com.flingoapp.flingo.navigation.NavigationAction
import com.flingoapp.flingo.ui.CustomPreview
import com.flingoapp.flingo.ui.RiveAnimation
import com.flingoapp.flingo.ui.component.button.CustomElevatedButton
import com.flingoapp.flingo.ui.theme.FlingoTheme
import com.flingoapp.flingo.viewmodel.MainAction
import kotlin.math.sqrt

@Composable
fun AllLivesLostScreen(
    onAction: (MainAction) -> Unit,
    onNavigate: (NavigationAction) -> Unit
) {
    val context = LocalContext.current
    Rive.init(context)

    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    val accelerometer = remember {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    val shakeThreshold = 13f
    var lastShakeTime by remember { mutableLongStateOf(0L) }

    var shakeCounter by remember { mutableIntStateOf(-1) }

    LaunchedEffect(shakeCounter) {
        Log.e("AllLivesLostScreen", "Shake counter: $shakeCounter")
    }

    DisposableEffect(sensorManager, accelerometer) {
        val sensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    if (it.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                        val (x, y, z) = it.values
                        val acceleration = sqrt(x * x + y * y + z * z)
                        val currentTime = System.currentTimeMillis()
                        // Only process every 100ms to limit event frequency
                        if (currentTime - lastShakeTime > 200 && acceleration > shakeThreshold) {
                            lastShakeTime = currentTime
                            shakeCounter++
                        }
                    }
                }
            }

            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}
        }
        sensorManager.registerListener(sensorListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        onDispose {
            sensorManager.unregisterListener(sensorListener)
        }
    }

    Box(contentAlignment = Alignment.Center) {
        Row {
            repeat(5) { index ->
                RiveAnimation(
                    modifier = Modifier.weight(1f),
                    resId = R.raw.rive_heart,
                    update = { animation ->
                        if (index == shakeCounter) {
                            animation.play("like")
                        }
                    }
                )
            }
        }

        //to stop the click event to propagate to rive animation and trigger animation on click
        Box(modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = {}
            )
        )

        Text(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 24.dp)
                .wrapContentSize(Alignment.TopCenter),
            text = "Oh nein! Deine Herzen sind alle weg!",
            style = MaterialTheme.typography.headlineLarge
        )

        AnimatedVisibility(
            enter = scaleIn() + fadeIn(),
            exit = scaleOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
            visible = shakeCounter >= 4
        ) {
            CustomElevatedButton(
                elevation = 10.dp,
                shape = CircleShape,
                onClick = {
                    onAction(MainAction.UserAction.RefillLives)
                    onNavigate(NavigationAction.Up())
                },
                buttonContent = {
                    Text(
                        modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp),
                        text = "Weiter gehts!",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 38.sp)
                    )
                }
            )
        }

        AnimatedVisibility(
            visible = shakeCounter < 4,
            enter = EnterTransition.None,
            exit = fadeOut(),
        ) {
            Text(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 36.dp)
                    .wrapContentSize(Alignment.BottomCenter),
                text = "Schüttel dein Tablet, um neue Herzen zu bekommen!",
                style = MaterialTheme.typography.headlineLarge
            )
        }
    }
}

@CustomPreview
@Composable
private fun AllLivesLostScreenPreview() {
    FlingoTheme {
        AllLivesLostScreen(
            onAction = {},
            onNavigate = {}
        )
    }
}