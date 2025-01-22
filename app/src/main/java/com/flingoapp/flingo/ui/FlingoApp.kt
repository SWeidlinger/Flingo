package com.flingoapp.flingo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeGesturesPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.flingoapp.flingo.ui.navigation.NavHostComposable
import com.flingoapp.flingo.ui.theme.FlingoColors
import com.flingoapp.flingo.ui.theme.FlingoTheme
import com.flingoapp.flingo.viewmodels.main.MainViewModel

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

                //TODO: move to right place
                Row(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(16.dp)
                        .align(Alignment.BottomStart)
                        .background(
                            color = Color.LightGray,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val mainUiState by mainViewModel.uiState.collectAsStateWithLifecycle()

                    if (mainUiState.userData != null) {
                        Text(
                            modifier = Modifier.padding(8.dp),
                            style = MaterialTheme.typography.headlineMedium,
                            text = mainUiState.userData!!.currentLives.toString()
                        )

                        Icon(
                            modifier = Modifier.padding(end = 8.dp),
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