package com.flingoapp.flingo.data.model

import PageDetails
import PageDetailsType
import com.flingoapp.flingo.data.model.page.Feedback
import com.flingoapp.flingo.data.model.page.Page

object MockData {
    val pageDetailsRemoveWord = PageDetails.RemoveWord(
        content = "Content",
        answer = "Answer",
        type = PageDetailsType.REMOVE_WORD
    )

    val pageDetailsQuizSingleChoice = PageDetails.Quiz(
        type = PageDetailsType.QUIZ,
        quizType = PageDetails.Quiz.QuizType.SINGLE_CHOICE,
        question = "Question",
        referenceTextTitle = "Reference Text Title",
        referenceText = "Reference Text",
        answers = arrayListOf(
            PageDetails.Quiz.Answer(
                id = 1,
                answer = "Answer 1",
                isCorrect = true
            ),
            PageDetails.Quiz.Answer(
                id = 2,
                answer = "Answer 2",
                isCorrect = false
            )
        )
    )

    val pageDetailsQuizTrueOrFalse = PageDetails.Quiz(
        type = PageDetailsType.QUIZ,
        quizType = PageDetails.Quiz.QuizType.TRUE_OR_FALSE,
        question = "Question",
        referenceTextTitle = "Reference Text Title",
        referenceText = "Reference Text",
        answers = arrayListOf(
            PageDetails.Quiz.Answer(
                id = 1,
                answer = "Answer 1",
                isCorrect = true
            ),
            PageDetails.Quiz.Answer(
                id = 2,
                answer = "Answer 2",
                isCorrect = false
            )
        )
    )

    val pageDetailsOrderStory = PageDetails.OrderStory(
        type = PageDetailsType.ORDER_STORY,
        content = arrayListOf(
            PageDetails.OrderStory.Content(
                id = 1,
                text = "Content 1"
            ),
            PageDetails.OrderStory.Content(
                id = 2,
                text = "Content 2"
            ),
            PageDetails.OrderStory.Content(
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
        details = pageDetailsOrderStory
    )

    val chapter = Chapter(
        id = "1",
        title = "This is a chapter",
        type = ChapterType.CHALLENGE,
        description = "This is a description",
        positionOffset = 0.0f,
        isCompleted = false,
        pages = arrayListOf(page, page.copy(id = "2"), page.copy(id = "3"))
    )

    val book = Book(
        author = "Author",
        date = "2021-09-01",
        language = "English",
        version = "1.0",
        title = "This is a book",
        description = "This is a description",
        coverImage = "Cover Image",
        chapters = arrayListOf(chapter, chapter.copy(id = "2"), chapter.copy(id = "3"))
    )
}