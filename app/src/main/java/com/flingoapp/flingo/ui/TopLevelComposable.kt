package com.flingoapp.flingo.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.flingoapp.flingo.ui.navigation.NavHostComposable
import com.flingoapp.flingo.viewmodels.main.MainViewModel

@Composable
fun TopLevelComposable(
    mainViewModel: MainViewModel,
    navHostController: NavHostController = rememberNavController()
) {
    Scaffold() { padding ->
        NavHostComposable(
            modifier = Modifier.padding(padding),
            navController = navHostController,
            mainViewModel = mainViewModel
        )
    }
}