package com.flingoapp.flingo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flingoapp.flingo.data.network.GenAiModel
import com.flingoapp.flingo.di.GenAiModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PersonalizationUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isSuccess: Boolean = false,
    val currentModel: GenAiModel = GenAiModel.OPEN_AI
)

class PersonalizationViewModel(
    private val genAiModule: GenAiModule,
    //should be handled differently
    private val bookViewModel: BookViewModel,
    private val userViewModel: UserViewModel,
) : ViewModel() {
    companion object {
        private const val TAG = "PersonalizationViewModel"

        private const val BOOK_TEST_PROMPT_1 =
            "You are a well-known children books author and you have been given the task to adapt this JSON to the preferences of the child. The child is 7 years old and is called Jakob. He really likes superheros. He wants to improve his reading ability. Adapt the following JSON so it fits his needs, do not add any new fields or any new game modes just adapt it to the needs of Jakob. The goal of this book is to improve the learning ability, and it should also be personalized to the user. The texts have to be in german.\n" +
                    "\n" +
                    "{ \"author\": \"sebastian\", \"date\": \"2025-06-01\", \"language\": \"de\", \"version\": \"1.0\", \"title\": \"Beispieltextbuch Wunderwelt Sprache\", \"description\": \"Diese Buch stellt die einzelnen Spielkonzepte vor. Welche nicht angepasst werden und 1:1 von dem Schulbuch Wunderwelt Sprache übernommen worden sind.\", \"coverImage\": \"book_cover_wunderwelt_sprache\", \"chapters\": [ { \"chapterId\": \"1\", \"chapterTitle\": \"Leseteil\", \"chapterDescription\": \"Lies die Geschichte.\", \"chapterCoverImage\": \"\", \"chapterPositionOffset\": 0.2, \"chapterType\": \"read\", \"chapterCompleted\": false, \"pages\": [ { \"pageId\": \"1\", \"pageDescription\": \"\", \"pageCompleted\": false, \"difficulty\": \"medium\", \"hint\": \"\", \"timeLimit\": null, \"score\": null, \"feedback\": { \"correct\": \"\", \"incorrect\": \"\" }, \"taskDefinition\": \"\", \"pageType\": \"read\", \"pageDetails\": { \"pageType\": \"read\", \"content\": \"Der weltberühmte Zirkus Marissimo lagerte einmal in einem kleinen Dorf in Italien.\", \"images\": [ \"circus_image\" ] } }, { \"pageId\": \"2\", \"pageDescription\": \"\", \"pageCompleted\": false, \"difficulty\": \"medium\", \"hint\": \"\", \"timeLimit\": null, \"score\": null, \"feedback\": { \"correct\": \"\", \"incorrect\": \"\" }, \"taskDefinition\": \"\", \"pageType\": \"read\", \"pageDetails\": { \"pageType\": \"read\", \"content\": \"In dem Dorf lebte auch eine kleines Pony namens Peppi, das sehr unglücklich war.\", \"images\": [ \"pony\" ] } }, { \"pageId\": \"3\", \"pageDescription\": \"\", \"pageCompleted\": false, \"difficulty\": \"easy\", \"hint\": \"\", \"timeLimit\": null, \"score\": null, \"feedback\": { \"correct\": \"\", \"incorrect\": \"\" }, \"taskDefinition\": \"\", \"pageType\": \"read\", \"pageDetails\": { \"pageType\": \"read\", \"content\": \"Es hatte früher einem alten Mann gehört.\", \"images\": [ \"old_man\" ] } } ] }]}"

        private const val BOOK_TEST_PROMPT_2 =
            "You are a well-known children books author and you have been given the task to adapt this JSON to the preferences of the child. The child is 7 years old and is called Jakob. He really likes superheros. He wants to improve his reading ability. Adapt the following JSON so it fits his needs, do not add any new fields or any new game modes just adapt it to the needs of Jakob. The goal of this book is to improve the learning ability, and it should also be personalized to the user. The texts have to be in german.\n" +
                    "\n" +
                    "{\n" +
                    "  \"author\": \"sebastian\",\n" +
                    "  \"date\": \"2025-06-01\",\n" +
                    "  \"language\": \"de\",\n" +
                    "  \"version\": \"1.0\",\n" +
                    "  \"title\": \"Beispieltextbuch Wunderwelt Sprache\",\n" +
                    "  \"description\": \"Diese Buch stellt die einzelnen Spielkonzepte vor. Welche nicht angepasst werden und 1:1 von dem Schulbuch Wunderwelt Sprache übernommen worden sind.\",\n" +
                    "  \"coverImage\": \"book_cover_wunderwelt_sprache\",\n" +
                    "  \"chapters\": [\n" +
                    "    {\n" +
                    "      \"chapterId\": \"2\",\n" +
                    "      \"chapterTitle\": \"Spielkonzept 1: Überflüssiges Wort\",\n" +
                    "      \"chapterDescription\": \"Streiche die überflüssen und sinnstörenden Wörter durch. (Wunderwelt Sprache 3 - Lesen und Sprechen - S. 30)\",\n" +
                    "      \"chapterCoverImage\": \"\",\n" +
                    "      \"chapterPositionOffset\": 0.6,\n" +
                    "      \"chapterType\": \"challenge\",\n" +
                    "      \"chapterCompleted\": false,\n" +
                    "      \"pages\": [\n" +
                    "        {\n" +
                    "          \"pageId\": \"1\",\n" +
                    "          \"pageDescription\": \"\",\n" +
                    "          \"pageCompleted\": false,\n" +
                    "          \"difficulty\": \"\",\n" +
                    "          \"hint\": \"\",\n" +
                    "          \"timeLimit\": null,\n" +
                    "          \"score\": null,\n" +
                    "          \"feedback\": {},\n" +
                    "          \"taskDefinition\": \"Streiche die überflüssen und sinnstörenden Wörter durch.\",\n" +
                    "          \"pageType\": \"remove_word\",\n" +
                    "          \"pageDetails\": {\n" +
                    "            \"pageType\": \"remove_word\",\n" +
                    "            \"content\": \"Maulwürfe werfen fangen häufig Erdhügel auf.\",\n" +
                    "            \"answer\": \"fangen\"\n" +
                    "          }\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"pageId\": \"2\",\n" +
                    "          \"pageDescription\": \"\",\n" +
                    "          \"pageCompleted\": false,\n" +
                    "          \"difficulty\": \"\",\n" +
                    "          \"hint\": \"\",\n" +
                    "          \"timeLimit\": null,\n" +
                    "          \"score\": null,\n" +
                    "          \"feedback\": {},\n" +
                    "          \"taskDefinition\": \"Streiche die überflüssen und sinnstörenden Wörter durch.\",\n" +
                    "          \"pageType\": \"remove_word\",\n" +
                    "          \"pageDetails\": {\n" +
                    "            \"pageType\": \"remove_word\",\n" +
                    "            \"content\": \"Ein Orchester ist eine Gruppe von Musikern Astronauten, die unter der Leitung eines Dirigenten spielen.\",\n" +
                    "            \"answer\": \"Astronauten\"\n" +
                    "          }\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"pageId\": \"3\",\n" +
                    "          \"pageDescription\": \"\",\n" +
                    "          \"pageCompleted\": false,\n" +
                    "          \"difficulty\": \"\",\n" +
                    "          \"hint\": \"\",\n" +
                    "          \"timeLimit\": null,\n" +
                    "          \"score\": null,\n" +
                    "          \"feedback\": {},\n" +
                    "          \"taskDefinition\": \"Streiche die überflüssen und sinnstörenden Wörter durch.\",\n" +
                    "          \"pageType\": \"remove_word\",\n" +
                    "          \"pageDetails\": {\n" +
                    "            \"pageType\": \"remove_word\",\n" +
                    "            \"content\": \"Der Papagei ist ein großer Sessel Vogel, der ein leuchtend farbiges Federkleid besitzt.\",\n" +
                    "            \"answer\": \"Sessel\"\n" +
                    "          }\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"pageId\": \"4\",\n" +
                    "          \"pageDescription\": \"\",\n" +
                    "          \"pageCompleted\": false,\n" +
                    "          \"difficulty\": \"\",\n" +
                    "          \"hint\": \"\",\n" +
                    "          \"timeLimit\": null,\n" +
                    "          \"score\": null,\n" +
                    "          \"feedback\": {},\n" +
                    "          \"taskDefinition\": \"Streiche die überflüssen und sinnstörenden Wörter durch.\",\n" +
                    "          \"pageType\": \"remove_word\",\n" +
                    "          \"pageDetails\": {\n" +
                    "            \"pageType\": \"remove_word\",\n" +
                    "            \"content\": \"Die Preiselbeere ist ein wild brüllender wachsender Zwergstrauch im Gebirge und auf der Heide.\",\n" +
                    "            \"answer\": \"brüllender\"\n" +
                    "          }\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"pageId\": \"5\",\n" +
                    "          \"pageDescription\": \"\",\n" +
                    "          \"pageCompleted\": false,\n" +
                    "          \"difficulty\": \"\",\n" +
                    "          \"hint\": \"\",\n" +
                    "          \"timeLimit\": null,\n" +
                    "          \"score\": null,\n" +
                    "          \"feedback\": {},\n" +
                    "          \"taskDefinition\": \"Streiche die überflüssen und sinnstörenden Wörter durch.\",\n" +
                    "          \"pageType\": \"remove_word\",\n" +
                    "          \"pageDetails\": {\n" +
                    "            \"pageType\": \"remove_word\",\n" +
                    "            \"content\": \"Riesen sind in Märchen und Sagen wichtige kleine Persönlichkeiten.\",\n" +
                    "            \"answer\": \"kleine\"\n" +
                    "          }\n" +
                    "        }\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}"

    }

    private val _uiState = MutableStateFlow(PersonalizationUiState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: MainAction.PersonalizationAction) {
        when (action) {
            MainAction.PersonalizationAction.GenerateBook -> generateBook()
            is MainAction.PersonalizationAction.ChangeModel -> changeModel(action.model)
        }
    }

    private fun buildPersonalizedPrompt() {
        val name = userViewModel.uiState.value.name
        val age = userViewModel.uiState.value.age
        val interests = userViewModel.uiState.value.selectedInterests

        //TODO: implement
    }

    private fun changeModel(model: GenAiModel){
        genAiModule.setModelRepository(model)
        updateUiState(_uiState.value.copy(currentModel = model))
    }

    private fun generateBook() {
        viewModelScope.launch {
            updateUiState(PersonalizationUiState(isLoading = true))

            genAiModule.repository.getResponse(BOOK_TEST_PROMPT_2)
                .onFailure { error ->
                    errorHandling(error)
                }
                .onSuccess { book ->
                    Log.e(TAG, book)
                    updateUiState(_uiState.value.copy(isLoading = false, isSuccess = true))
                    bookViewModel.onAction(MainAction.BookAction.AddBook(book))
                }
        }
    }

    private fun errorHandling(error: Throwable) {
        Log.e(TAG, "Error: ${error.message}")
        updateUiState(_uiState.value.copy(isLoading = false, isError = true))
    }

    /**
     * Update ui state
     *
     * @param newUiState
     */
    private fun updateUiState(newUiState: PersonalizationUiState) {
        _uiState.update { newUiState }
    }
}