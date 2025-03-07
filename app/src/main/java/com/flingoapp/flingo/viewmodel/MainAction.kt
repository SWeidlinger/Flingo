package com.flingoapp.flingo.viewmodel

import PageDetailsType
import android.content.Context
import com.flingoapp.flingo.data.model.Book
import com.flingoapp.flingo.data.model.Chapter
import com.flingoapp.flingo.data.model.genAi.GenAiModelPerformance
import com.flingoapp.flingo.data.model.genAi.GenAiProvider
import com.flingoapp.flingo.data.model.page.Page
import com.flingoapp.flingo.ui.screen.UserImageStyle
import com.flingoapp.flingo.ui.screen.UserInterest

/**
 * Main action
 *
 * @constructor Create empty Main action
 */
sealed interface MainAction {
    /**
     * User action
     *
     * @constructor Create empty User action
     */
    sealed interface UserAction : MainAction {
        data object IncreaseLives : UserAction
        data object DecreaseLives : UserAction
        data object RefillLives : UserAction

        /**
         * Select interest
         *
         * @property interest
         * @constructor Create empty Select interest
         */
        data class SelectInterest(val interest: UserInterest) : UserAction

        /**
         * Remove interest
         *
         * @property interest
         * @constructor Create empty Remove interest
         */
        data class RemoveInterest(val interest: UserInterest) : UserAction

        /**
         * Select image style
         *
         * @property imageStyle
         * @constructor Create empty Select image style
         */
        data class SelectImageStyle(val imageStyle: UserImageStyle) : UserAction

        /**
         * Fetch user
         *
         * @property userJson
         * @constructor Create empty Fetch user
         */
        data class FetchUser(val userJson: String) : UserAction

        /**
         * Switch user
         *
         * @property context
         * @constructor Create empty Switch user
         */
        data class SwitchUser(val context: Context) : UserAction
    }

    /**
     * Book action
     *
     * @constructor Create empty Book action
     */
    sealed interface BookAction : MainAction {
        /**
         * Select book
         *
         * @property bookId
         * @constructor Create empty Select book
         */
        data class SelectBook(val bookId: String) : BookAction

        /**
         * Select chapter
         *
         * @property chapterId
         * @constructor Create empty Select chapter
         */
        data class SelectChapter(val chapterId: String) : BookAction

        /**
         * Select page
         *
         * @property pageIndex
         * @constructor Create empty Select page
         */
        data class SelectPage(val pageIndex: Int) : BookAction
        data object CompleteChapter : BookAction

        /**
         * Complete page
         *
         * @property pageIndex
         * @constructor Create empty Complete page
         */
        data class CompletePage(val pageIndex: Int) : BookAction

        /**
         * Fetch books
         *
         * @property booksJson
         * @constructor Create empty Fetch books
         */
        data class FetchBooks(val booksJson: List<String>) : BookAction

        /**
         * Add book json
         *
         * @property bookJson
         * @property author
         * @constructor Create empty Add book json
         */
        data class AddBookJson(val bookJson: String, val author: String) : BookAction

        /**
         * Add book
         *
         * @property book
         * @property author
         * @constructor Create empty Add book
         */
        data class AddBook(val book: Book, val author: String) : BookAction

        /**
         * Add chapter json
         *
         * @property chapterJson
         * @property author
         * @property bookId
         * @constructor Create empty Add chapter json
         */
        data class AddChapterJson(val chapterJson: String, val author: String, val bookId: String) : BookAction

        /**
         * Add chapter
         *
         * @property chapter
         * @property author
         * @property bookId
         * @constructor Create empty Add chapter
         */
        data class AddChapter(val chapter: Chapter, val author: String, val bookId: String) : BookAction

        /**
         * Add page json
         *
         * @property pageJson
         * @property author
         * @property chapterId
         * @property bookId
         * @constructor Create empty Add page json
         */
        data class AddPageJson(val pageJson: String, val author: String, val chapterId: String, val bookId: String) : BookAction

        /**
         * Add page
         *
         * @property page
         * @property author
         * @property chapterId
         * @property bookId
         * @constructor Create empty Add page
         */
        data class AddPage(val page: Page, val author: String, val chapterId: String, val bookId: String) : BookAction

        /**
         * Add image
         *
         * @property imageUrl
         * @property author
         * @constructor Create empty Add image
         */
        data class AddImage(val imageUrl: String, val author: String) : BookAction
    }

    /**
     * Personalization action
     *
     * @constructor Create empty Personalization action
     */
    sealed interface PersonalizationAction : MainAction {
        /**
         * Generate book from text
         *
         * @property scannedText
         * @constructor Create empty Generate book from text
         */
        data class GenerateBookFromText(val scannedText: String) : PersonalizationAction

        /**
         * Generate chapter
         *
         * @property sourceChapter
         * @constructor Create empty Generate chapter
         */
        data class GenerateChapter(val sourceChapter: Chapter) : PersonalizationAction

        /**
         * Generate chapter from text
         *
         * @property pageDetailsType
         * @constructor Create empty Generate chapter from text
         */
        data class GenerateChapterFromText(val pageDetailsType: PageDetailsType) : PersonalizationAction

        /**
         * Generate page
         *
         * @property sourceChapter
         * @property sourceBook
         * @constructor Create empty Generate page
         */
        data class GeneratePage(val sourceChapter: Chapter, val sourceBook: Book) : PersonalizationAction

        /**
         * Change model
         *
         * @property model
         * @constructor Create empty Change model
         */
        data class ChangeModel(val model: GenAiProvider) : PersonalizationAction

        /**
         * Change model performance
         *
         * @property modelPerformance
         * @constructor Create empty Change model performance
         */
        data class ChangeModelPerformance(val modelPerformance: GenAiModelPerformance) : PersonalizationAction
        data object ToggleDebugMode : PersonalizationAction
        data object ToggleGenerateImages : PersonalizationAction

        /**
         * Generate image
         *
         * @property context
         * @constructor Create empty Generate image
         */
        data class GenerateImage(val context: String) : PersonalizationAction

        /**
         * Generate book
         *
         * @property book
         * @constructor Create empty Generate book
         */
        data class GenerateBook(val book: Book) : PersonalizationAction
    }
}
