package com.flingoapp.flingo.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeGesturesPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.flingoapp.flingo.ui.navigation.NavHostComposable
import com.flingoapp.flingo.ui.theme.FlingoColors
import com.flingoapp.flingo.ui.theme.FlingoTheme
import com.flingoapp.flingo.viewmodels.main.MainViewModel
import dev.omkartenkale.explodable.Explodable
import dev.omkartenkale.explodable.ExplosionAnimationSpec
import dev.omkartenkale.explodable.rememberExplosionController
import kotlinx.coroutines.delay

/**
 * Top level composable of the app, used to initialize the navigation graph
 *
 * @param mainViewModel
 * @param navHostController
 */
@Composable
fun FlingoApp(
    mainViewModel: MainViewModel,
    navHostController: NavHostController = rememberNavController()
) {
    FlingoTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .safeGesturesPadding()
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize()) {
                NavHostComposable(
                    modifier = Modifier.padding(innerPadding),
                    navController = navHostController,
                    mainViewModel = mainViewModel
                )

                val explosionController = rememberExplosionController()
                //TODO: move to right place

                var isLivesVisible by remember { mutableStateOf(true) }

                Explodable(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(16.dp)
                        .align(Alignment.BottomStart),
                    controller = explosionController,
                    animationSpec = ExplosionAnimationSpec(
                        explosionPower = 5f,
                        shakeDurationMs = 100,
                        explosionDurationMs = 1000
                    ),
                    onExplode = {
                        explosionController.reset()
                        isLivesVisible = true
                    }
                ) {
                    AnimatedVisibility(
                        visible = isLivesVisible,
                        enter = scaleIn(),
                        exit = ExitTransition.None
                    ) {
                        Row(
                            modifier = Modifier
                                .background(
                                    color = Color.LightGray,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(vertical = 8.dp, horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            val mainUiState by mainViewModel.uiState.collectAsStateWithLifecycle()

                            if (mainUiState.userData != null) {
                                var currentDisplayedLives by remember {
                                    mutableIntStateOf(
                                        mainUiState.userData!!.currentLives
                                    )
                                }

                                LaunchedEffect(mainUiState.userData!!.currentLives) {
                                    if (currentDisplayedLives != mainUiState.userData!!.currentLives) {
                                        explosionController.explode()
                                        delay(1000)
                                        currentDisplayedLives = mainUiState.userData!!.currentLives
                                        isLivesVisible = false
                                    }
                                }

                                var textSize by remember { mutableStateOf(IntSize.Zero) }

                                Text(
                                    modifier = Modifier
                                        .onGloballyPositioned { layoutCoordinates ->
                                            textSize = layoutCoordinates.size
                                        },
                                    style = MaterialTheme.typography.headlineLarge,
                                    text = currentDisplayedLives.toString(),
                                    fontSize = 42.sp
                                )

                                Spacer(modifier = Modifier.padding(4.dp))

                                Icon(
                                    modifier = Modifier
                                        .height(textSize.height.toDp())
                                        .width(textSize.width.toDp() * 1.6f),
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Lives",
                                    tint = FlingoColors.Error.darken(0.1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}