package com.flingoapp.flingo.ui

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.flingoapp.flingo.di.MainApplication
import com.flingoapp.flingo.di.viewModelFactory
import com.flingoapp.flingo.navigation.NavHostComposable
import com.flingoapp.flingo.navigation.NavigationDestination
import com.flingoapp.flingo.ui.component.button.CustomElevatedTextButton2
import com.flingoapp.flingo.ui.theme.FlingoColors
import com.flingoapp.flingo.ui.theme.FlingoTheme
import com.flingoapp.flingo.viewmodel.BookViewModel
import com.flingoapp.flingo.viewmodel.MainAction
import com.flingoapp.flingo.viewmodel.MainViewModel
import com.flingoapp.flingo.viewmodel.PersonalizationViewModel
import com.flingoapp.flingo.viewmodel.UserViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.delay
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.io.IOException
import java.util.concurrent.TimeUnit

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
                userViewModel = userViewModel,
                connectivityObserver = MainApplication.connectivityObserver,
                personalizationRepository = MainApplication.personalizationRepository
            )
        }
    ),
    navHostController: NavHostController = rememberNavController()
) {
    var shootConfetti by remember { mutableStateOf(false) }
    var fabPosition by remember { mutableStateOf(Offset.Unspecified) }
    var fabSize by remember { mutableStateOf(IntSize.Zero) }

    val context = LocalContext.current

    FlingoTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            floatingActionButton = {
                val currentBackStackEntry by navHostController.currentBackStackEntryAsState()
                //don't show fab if not on predefined screens
                val currentVisibleScreen =
                    getCurrentScreen(currentBackStackEntry?.destination?.route)
                val isFabVisible = currentVisibleScreen != null

                val buttonText = when (currentVisibleScreen) {
                    CurrentVisibleScreen.SCREEN_CHAPTER_SELECTION -> "Neues Kapitel"
                    CurrentVisibleScreen.SCREEN_CHALLENGE -> "Neue Seite"
                    else -> "Neues Buch"
                }

                val personalizationUiState by personalizationViewModel.uiState.collectAsState()

                var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
                val photoPickerLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.PickVisualMedia(),
                    onResult = { uri ->
                        selectedImageUri = uri
                    }
                )

                LaunchedEffect(selectedImageUri) {
                    Log.e("FlingoApp", "$selectedImageUri")
                }

                LaunchedEffect(personalizationUiState.isSuccess) {
                    if (personalizationUiState.isSuccess) {
                        shootConfetti = true
                    }
                }

                var isScanningText by remember { mutableStateOf(false) }

                val onClickAction: () -> Unit = when (currentVisibleScreen) {
                    CurrentVisibleScreen.SCREEN_HOME -> {
                        {
                            if (selectedImageUri == null) {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            } else {
                                //TODO: add OCR and extract text
                                var imageToOcr: InputImage? = null
                                try {
                                    imageToOcr =
                                        InputImage.fromFilePath(context, selectedImageUri!!)
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }

                                isScanningText = true

                                imageToOcr?.let {
                                    TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                                        .process(it)
                                        .addOnSuccessListener { result ->
                                            isScanningText = false

                                            Log.e("FlingoApp", result.text)

                                            personalizationViewModel.onAction(
                                                MainAction.PersonalizationAction.GenerateBook(
                                                    scannedText = result.text
                                                )
                                            )
                                        }.addOnFailureListener {
                                            isScanningText = false

                                            Toast.makeText(
                                                context,
                                                "OCR failed",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }
                            }
                        }
                    }

                    CurrentVisibleScreen.SCREEN_CHAPTER_SELECTION -> {
                        { personalizationViewModel.onAction(MainAction.PersonalizationAction.GenerateChapter) }
                    }

                    CurrentVisibleScreen.SCREEN_CHALLENGE -> {
                        { personalizationViewModel.onAction(MainAction.PersonalizationAction.GeneratePage) }
                    }

                    null -> {
                        {}
                    }
                }

                AnimatedVisibility(
                    modifier = Modifier
                        .padding(16.dp)
                        .onGloballyPositioned {
                            fabPosition = it.positionOnScreen()
                            fabSize = it.size
                        },
                    visible = isFabVisible,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    CustomElevatedTextButton2(
                        modifier = Modifier
                            .then(
                                if (personalizationUiState.isLoading) {
                                    Modifier.animatedBorder(
                                        strokeWidth = 3.dp,
                                        shape = CircleShape,
                                        durationMillis = 1500
                                    )
                                } else {
                                    Modifier
                                }
                            ),
                        textModifier = Modifier.padding(horizontal = 8.dp),
                        fontSize = 24.sp,
                        shape = CircleShape,
                        elevation = 6.dp,
                        isPressed = personalizationUiState.isLoading,
                        backgroundColor = if (personalizationUiState.isError) FlingoColors.Error else Color.White,
                        text = buttonText,
                        icon = personalizationUiState.currentModel.iconRes,
                        enabled = personalizationUiState.isConnectedToNetwork,
                        onClick = onClickAction
                    )
                }
            }
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
                                "Model: ${personalizationUiState.currentModel.textModel}\n" +
                                "Last response time (s): ${
                                    personalizationUiState.lastResponseTime?.toDouble()?.div(1000.0)
                                }\n" +
                                "Used data: ${personalizationUiState.childName.toString()}, ${personalizationUiState.childAge.toString()}, ${personalizationUiState.childInterest.toString()}\n" +
                                "Prompt:\n" +
                                "${personalizationUiState.usedPrompt}"

                        //TODO: add how many tokens used
                    )
                }

                if (shootConfetti) {
                    LaunchedEffect(Unit) {
                        delay(3000)
                        shootConfetti = false
                    }

                    KonfettiView(
                        modifier = Modifier
                            .fillMaxSize(),
                        parties = listOf(
                            Party(
                                position = Position.Absolute(
                                    x = fabPosition.x + fabSize.width / 2,
                                    y = fabPosition.y
                                ),
                                emitter = Emitter(
                                    duration = 1000,
                                    timeUnit = TimeUnit.MILLISECONDS
                                ).perSecond(200),
                                spread = 90,
                                angle = -90
                            )
                        )
                    )
                }
            }
        }
    }
}

//TODO: rework this
private fun getCurrentScreen(route: String?): CurrentVisibleScreen? {
    return if (route == NavigationDestination.Home::class.qualifiedName) {
        CurrentVisibleScreen.SCREEN_HOME
    } else if (NavigationDestination.ChapterSelection::class.qualifiedName?.let {
            route?.startsWith(it)
        } == true) {
        CurrentVisibleScreen.SCREEN_CHAPTER_SELECTION
    } else if (NavigationDestination.Chapter::class.qualifiedName?.let {
            route?.startsWith(it)
        } == true) {
        CurrentVisibleScreen.SCREEN_CHALLENGE
    } else {
        null
    }
}

private enum class CurrentVisibleScreen {
    SCREEN_HOME,
    SCREEN_CHAPTER_SELECTION,
    SCREEN_CHALLENGE
}