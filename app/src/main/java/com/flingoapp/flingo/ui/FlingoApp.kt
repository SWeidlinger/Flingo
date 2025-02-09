package com.flingoapp.flingo.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeGesturesPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.flingoapp.flingo.di.MainApplication
import com.flingoapp.flingo.di.viewModelFactory
import com.flingoapp.flingo.navigation.NavHostComposable
import com.flingoapp.flingo.ui.theme.FlingoTheme
import com.flingoapp.flingo.viewmodel.BookViewModel
import com.flingoapp.flingo.viewmodel.MainViewModel
import com.flingoapp.flingo.viewmodel.PersonalizationViewModel
import com.flingoapp.flingo.viewmodel.UserViewModel

/**
 * Top level composable of the app, used to initialize the navigation graph
 *
 * @param mainViewModel
 * @param navHostController
 */
@Composable
fun FlingoApp(
    mainViewModel: MainViewModel,
    bookViewModel: BookViewModel,
    userViewModel: UserViewModel,
    personalizationViewModel: PersonalizationViewModel = viewModel<PersonalizationViewModel>(
        factory = viewModelFactory {
            PersonalizationViewModel(
                genAiModule = MainApplication.appModule.genAiModule,
                bookViewModel = bookViewModel,
                userViewModel = userViewModel
            )
        }
    ),
    navHostController: NavHostController = rememberNavController()
) {
    FlingoTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .safeGesturesPadding()
        ) { innerPadding ->
            Box {
                NavHostComposable(
                    modifier = Modifier.padding(innerPadding),
                    navController = navHostController,
                    mainViewModel = mainViewModel,
                    bookViewModel = bookViewModel,
                    userViewModel = userViewModel,
                    personalizationViewModel = personalizationViewModel
                )

                val personalizationUiState by personalizationViewModel.uiState.collectAsState()

                if (personalizationUiState.isDebug) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                            .fillMaxWidth(0.5f),
                        fontSize = 12.sp,
                        text = "Provider: ${personalizationUiState.currentModel.provider}\n" +
                                "Model: ${personalizationUiState.currentModel.model}\n" +
                                "Last Response time (s): ${
                                    personalizationUiState.lastResponseTime?.toDouble()?.div(1000.0)
                                }\n" +
                                "Used data: ${personalizationUiState.childName.toString()}, ${personalizationUiState.childAge.toString()}, ${personalizationUiState.childInterest.toString()}\n" +
                                "Prompt:\n" +
                                "${personalizationUiState.usedPrompt}"

                        //TODO: add how many tokens used
                    )
                }
            }
        }
    }
}