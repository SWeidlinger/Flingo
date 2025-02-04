package com.flingoapp.flingo.data.models

import com.flingoapp.flingo.data.models.book.Book
import com.flingoapp.flingo.data.models.book.Chapter
import com.flingoapp.flingo.data.models.book.Feedback
import com.flingoapp.flingo.data.models.book.page.Page
import com.flingoapp.flingo.data.models.book.page.PageDetails
import com.flingoapp.flingo.data.models.book.page.PageType

object MockData {
    val pageDetailsRemoveWord = PageDetails.RemoveWordPageDetails(
        content = "Content",
        answer = "Answer"
    )

    val pageDetailsQuizSingleChoice = PageDetails.QuizPageDetails(
        quizType = PageDetails.QuizPageDetails.Companion.QuizType.SINGLE_CHOICE,
        question = "Question",
        referenceTextTitle = "Reference Text Title",
        referenceText = "Reference Text",
        answers = arrayListOf(
            PageDetails.QuizPageDetails.Companion.Answer(
                id = 1,
                answer = "Answer 1",
                isCorrect = true
            ),
            PageDetails.QuizPageDetails.Companion.Answer(
                id = 2,
                answer = "Answer 2",
                isCorrect = false
            )
        )
    )

    val pageDetailsQuizTrueOrFalse = PageDetails.QuizPageDetails(
        quizType = PageDetails.QuizPageDetails.Companion.QuizType.TRUE_OR_FALSE,
        question = "Question",
        referenceTextTitle = "Reference Text Title",
        referenceText = "Reference Text",
        answers = arrayListOf(
            PageDetails.QuizPageDetails.Companion.Answer(
                id = 1,
                answer = "Answer 1",
                isCorrect = true
            ),
            PageDetails.QuizPageDetails.Companion.Answer(
                id = 2,
                answer = "Answer 2",
                isCorrect = false
            )
        )
    )

    val pageDetailsOrderStory = PageDetails.OrderStoryPageDetails(
        content = arrayListOf(
            PageDetails.OrderStoryPageDetails.Companion.Content(
                id = 1,
                text = "Content 1"
            ),
            PageDetails.OrderStoryPageDetails.Companion.Content(
                id = 2,
                text = "Content 2"
            ),
            PageDetails.OrderStoryPageDetails.Companion.Content(
                id = 3,
                text = "Content 3"
            )
        ),
        correctOrder = arrayListOf(1, 2, 3),
        referenceTextTitle = "Reference Text Title",
        referenceText = "Reference Text"
    )

    val page = Page(
        id = "1",
        description = "This is a page",
        isCompleted = false,
        difficulty = "easy",
        hint = "This is a hint",
        timeLimit = 10,
        score = 10,
        feedback = Feedback(
            correct = "This is correct",
            incorrect = "This is incorrect"
        ),
        taskDefinition = "This is a task definition",
        type = PageType.ORDER_STORY,
        details = pageDetailsOrderStory
    )

    val chapter = Chapter(
        id = "1",
        title = "This is a chapter",
        type = com.flingoapp.flingo.data.models.book.ChapterType.CHALLENGE,
        description = "This is a description",
        positionOffset = 0.0f,
        isCompleted = false,
        pages = arrayListOf(page, page, page)
    )

    val book = Book(
        author = "Author",
        date = "2021-09-01",
        language = "English",
        version = "1.0",
        title = "This is a book",
        description = "This is a description",
        coverImage = "Cover Image",
        chapters = arrayListOf(chapter, chapter, chapter)
    )
}