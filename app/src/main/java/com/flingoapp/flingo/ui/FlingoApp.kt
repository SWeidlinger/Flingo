package com.flingoapp.flingo.ui

import PageDetailsSelectionEntry
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
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.flingoapp.flingo.di.MainApplication
import com.flingoapp.flingo.di.viewModelFactory
import com.flingoapp.flingo.navigation.NavHostComposable
import com.flingoapp.flingo.navigation.NavigationDestination
import com.flingoapp.flingo.ui.component.button.CustomElevatedButton2
import com.flingoapp.flingo.ui.component.button.CustomElevatedTextButton2
import com.flingoapp.flingo.ui.component.button.CustomIconButton2
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

    val personalizationUiState by personalizationViewModel.uiState.collectAsState()
    val userUiState by userViewModel.uiState.collectAsState()

    FlingoTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            floatingActionButton = {
                //TODO: should probably be moved to individual screens
                val currentBackStackEntry by navHostController.currentBackStackEntryAsState()
                //don't show fab if not on predefined screens
                val currentVisibleScreen =
                    getCurrentScreen(currentBackStackEntry?.destination?.route)
                val isFabVisible = currentVisibleScreen != null

                var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

                val buttonText = when (currentVisibleScreen) {
                    CurrentVisibleScreen.SCREEN_CHAPTER_SELECTION -> "Neues Kapitel"
                    CurrentVisibleScreen.SCREEN_CHALLENGE -> "Neue Seite"
                    else -> if (selectedImageUri == null) "Buch auswählen" else "Neues Buch"
                }

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

                var showPageDetailsSelection by remember { mutableStateOf(false) }

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
                                                MainAction.PersonalizationAction.GenerateBookFromText(
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
                        { showPageDetailsSelection = !showPageDetailsSelection }
                    }

                    CurrentVisibleScreen.SCREEN_CHALLENGE -> {
                        //TODO: add different logic for readChapter
                        {
                            personalizationViewModel.onAction(
                                MainAction.PersonalizationAction.GeneratePage(
                                    sourceChapter = bookViewModel.getCurrentChapter(),
                                    sourceBook = bookViewModel.getCurrentBook()
                                )
                            )
                        }
                    }

                    null -> {
                        {}
                    }
                }

                AnimatedVisibility(
                    modifier = Modifier.padding(16.dp),
                    visible = isFabVisible,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (currentVisibleScreen == CurrentVisibleScreen.SCREEN_HOME) {
                            AnimatedVisibility(
                                visible = selectedImageUri != null,
                                enter = fadeIn() + scaleIn() + slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }),
                                exit = fadeOut() + scaleOut() + slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }),
                            ) {
                                CustomElevatedButton2(
                                    modifier = Modifier
                                        .height(fabSize.height.toDp())
                                        .width(fabSize.height.toDp())
                                        .then(
                                            if (isScanningText) {
                                                Modifier.animatedBorder(
                                                    strokeWidth = 3.dp,
                                                    shape = CircleShape,
                                                    durationMillis = 1500,
                                                    colors = listOf(
                                                        Color.Transparent,
                                                        FlingoColors.Text
                                                    )
                                                )
                                            } else {
                                                Modifier
                                            }
                                        ),
                                    shape = CircleShape,
                                    isPressed = isScanningText || personalizationUiState.isLoading,
                                    addOutline = true,
                                    elevation = 4.dp,
                                    backgroundColor = Color.White,
                                    onClick = {
                                        if (personalizationUiState.isLoading || isScanningText) return@CustomElevatedButton2

                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(
                                                ActivityResultContracts.PickVisualMedia.ImageOnly
                                            )
                                        )
                                    }
                                ) {
                                    AsyncImage(
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .clip(CircleShape),
                                        model = selectedImageUri,
                                        contentDescription = "Selected image"
                                    )
                                }
                            }
                        } else if (currentVisibleScreen == CurrentVisibleScreen.SCREEN_CHAPTER_SELECTION) {
                            AnimatedVisibility(
                                visible = showPageDetailsSelection,
                                enter = fadeIn() + scaleIn() + slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }),
                                exit = fadeOut() + scaleOut() + slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth })
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    PageDetailsSelectionEntry.entries.forEach { entry ->
                                        CustomIconButton2(
                                            modifier = Modifier.size(fabSize.height.toDp()),
                                            elevation = 4.dp,
                                            icon = painterResource(entry.iconRes),
                                            iconContentDescription = entry.name,
                                            backgroundColor = entry.backgroundColor,
                                            iconTint = entry.iconTint,
                                            onClick = {
                                                personalizationViewModel.onAction(
                                                    MainAction.PersonalizationAction.GenerateChapterFromText(
                                                        entry.pageType
                                                    )
                                                )
                                                showPageDetailsSelection = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        CustomElevatedTextButton2(
                            modifier = Modifier
                                .onGloballyPositioned {
                                    fabPosition = it.positionOnScreen()
                                    fabSize = it.size
                                }
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
                            elevation = 4.dp,
                            isPressed = personalizationUiState.isLoading || isScanningText,
                            backgroundColor = if (personalizationUiState.isError) FlingoColors.Error else Color.White,
                            text = if (personalizationUiState.isLoading) "Anfertigen..." else buttonText,
                            icon = personalizationUiState.currentModel.iconRes,
                            enabled = personalizationUiState.isConnectedToNetwork,
                            onClick = {
                                if (personalizationUiState.isLoading || isScanningText) return@CustomElevatedTextButton2

                                onClickAction()
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box {
                NavHostComposable(
                    modifier = Modifier
                        .padding(innerPadding)
                        .consumeWindowInsets(innerPadding),
                    navController = navHostController,
                    mainViewModel = mainViewModel,
                    bookViewModel = bookViewModel,
                    userViewModel = userViewModel,
                    personalizationViewModel = personalizationViewModel
                )

                val modelPerformance by MainApplication.appModule.genAiModule.modelPerformance.collectAsState()

                if (personalizationUiState.isDebug) {
                    Text(
                        modifier = Modifier
                            .padding(innerPadding)
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                            .fillMaxWidth(0.5f),
                        fontSize = 12.sp,
                        text = "Provider: ${personalizationUiState.currentModel.provider}\n" +
                                "Text model: ${MainApplication.appModule.genAiModule.currentTextModel}\n" +
                                "Image model: ${MainApplication.appModule.genAiModule.currentImageModel}\n" +
                                "Selected interests: ${userUiState.selectedInterests.joinToString(", ")}\n" +
                                "Selected image style: ${userUiState.selectedImageStyle}\n" +
                                "Model performance: $modelPerformance\n" +
                                "Generate images: ${personalizationUiState.generateImages}\n" +
                                "Last response time (s): ${
                                    personalizationUiState.lastResponseTime?.toDouble()?.div(1000.0)
                                }\n" +
                                "Used data: ${personalizationUiState.childName.toString()}, ${personalizationUiState.childAge.toString()}, ${
                                    userUiState.selectedInterests.joinToString(", ")
                                }\n" +
                                "Prompt:\n" +
                                "${personalizationUiState.usedPrompt}"

                        //TODO: add how many tokens were used
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

private fun getCurrentScreen(route: String?): CurrentVisibleScreen? {
    if (route == null) return null

    val homeRoute = NavigationDestination.Home::class.qualifiedName
    val chapterSelectionRoute = NavigationDestination.ChapterSelection::class.qualifiedName
    val chapterRoute = NavigationDestination.Chapter::class.qualifiedName

    return when {
        route == homeRoute -> CurrentVisibleScreen.SCREEN_HOME
        chapterSelectionRoute != null && route.startsWith(chapterSelectionRoute) -> CurrentVisibleScreen.SCREEN_CHAPTER_SELECTION
        chapterRoute != null && route.startsWith(chapterRoute) -> CurrentVisibleScreen.SCREEN_CHALLENGE
        else -> null
    }
}

private enum class CurrentVisibleScreen {
    SCREEN_HOME,
    SCREEN_CHAPTER_SELECTION,
    SCREEN_CHALLENGE
}