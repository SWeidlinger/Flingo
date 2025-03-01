package com.flingoapp.flingo.data.model.genAi

import com.google.firebase.vertexai.type.Schema

//alternative approach for handling the vertex AI schema not dynamic
sealed interface VertexAiSchema {
    data object SplitText : VertexAiSchema {
        // Define the schema for a single text part.
        private val textPartSchema = Schema.string(description = "A single part of the text.")

        // Define the schema for the array of text parts.
        private val textPartsArraySchema = Schema.array(
            items = textPartSchema,
            description = "An array consisting of individual text segments."
        )

        val splitTextSchema = Schema.obj(
            properties = mapOf(
                "title" to Schema.string(
                    description = "The title of the story."
                ),
                "text_parts" to textPartsArraySchema
            ),
            // Since both properties are required, we leave optionalProperties empty.
            optionalProperties = emptyList(),
            description = "Schema for split_text"
        )
    }

    data object ImagePrompts : VertexAiSchema {
        // Schema for a single image prompt.
        private val imagePromptSchema = Schema.string(
            description = "A single image prompt for the corresponding text part."
        )

        // Combined object schema for "image_prompts".
        val schema: Schema = Schema.obj(
            properties = mapOf(
                "image_prompts" to Schema.array(
                    items = imagePromptSchema,
                    description = "An array consisting of the individual image prompts."
                )
            ),
            // "image_prompts" is required so we leave optionalProperties empty.
            optionalProperties = emptyList(),
            description = "Schema for image_prompts"
        )
    }

    data object PageDetailsRemoveWord : VertexAiSchema {
        // Schema for each sentence item in the "sentences" array.
        private val sentenceItemSchema = Schema.obj(
            properties = mapOf(
                "sentence" to Schema.string(
                    description = "The sentence including the unnecessary word."
                ),
                "answer" to Schema.string(
                    description = "The unnecessary word which was added to the sentence."
                )
            ),
            // Both "sentence" and "answer" are required.
            optionalProperties = emptyList(),
            description = "Schema for each sentence item in pagedetails_remove_word"
        )

        // Schema for the overall object.
        val schema: Schema = Schema.obj(
            properties = mapOf(
                "chapterTitle" to Schema.string(
                    description = "The title for this type of exercise and chapter."
                ),
                "taskDefinition" to Schema.string(
                    description = "The definition of the what should be done."
                ),
                "sentences" to Schema.array(
                    items = sentenceItemSchema,
                    description = "A list of the different sentences where always one word in an individual sentence is not necessary."
                )
            ),
            // All properties are required as per the JSON.
            optionalProperties = emptyList(),
            description = "Schema for pagedetails_remove_word"
        )
    }

    data object PageDetailsQuiz : VertexAiSchema {
        // Schema for each answer item in the "answers" array.
        private val answerItemSchema = Schema.obj(
            properties = mapOf(
                "answer" to Schema.string(
                    description = "The text of the answer."
                ),
                "isCorrect" to Schema.boolean(
                    description = "Indicates if this answer is the correct one."
                )
            ),
            // Both "answer" and "isCorrect" are required.
            optionalProperties = emptyList(),
            description = "Schema for each answer item in pagedetails_quiz"
        )

        // Schema for each question item in the "questions" array.
        private val questionItemSchema = Schema.obj(
            properties = mapOf(
                "question" to Schema.string(
                    description = "The given question about the story."
                ),
                "answers" to Schema.array(
                    items = answerItemSchema,
                    description = "A list of available answers to the question."
                )
            ),
            // Both "question" and "answers" are required.
            optionalProperties = emptyList(),
            description = "Schema for each question item in pagedetails_quiz"
        )

        // Combined object schema for "pagedetails_quiz".
        val schema: Schema = Schema.obj(
            properties = mapOf(
                "chapterTitle" to Schema.string(
                    description = "The title for this type of exercise and chapter."
                ),
                "taskDefinition" to Schema.string(
                    description = "The definition of the what should be done."
                ),
                "questions" to Schema.array(
                    items = questionItemSchema,
                    description = "A list of different questions about the story."
                )
            ),
            // All properties are required.
            optionalProperties = emptyList(),
            description = "Schema for pagedetails_quiz"
        )
    }

    data object PageDetailsOrderStory : VertexAiSchema {
        // Schema for a single snippet.
        private val snippetSchema = Schema.obj(
            properties = mapOf(
                "id" to Schema.integer(
                    description = "Unique identifier for each snippet."
                ),
                "text" to Schema.string(
                    description = "The text content of the snippet."
                )
            ),
            // Both "id" and "text" are required.
            optionalProperties = emptyList(),
            description = "Schema for each snippet in a story object."
        )

        // Schema for the "snippets" array within a story object.
        private val snippetsArraySchema = Schema.array(
            items = snippetSchema,
            description = "An array of story snippets."
        )

        // Schema for the "correctOrder" array which represents the correct order of snippet IDs.
        private val correctOrderArraySchema = Schema.array(
            items = Schema.integer(
                description = "Identifier of a snippet."
            ),
            description = "An array representing the correct order of the story snippets by their IDs."
        )

        // Schema for each story object in the "content" array.
        private val storyContentSchema = Schema.obj(
            properties = mapOf(
                "snippets" to snippetsArraySchema,
                "correctOrder" to correctOrderArraySchema
            ),
            // Both "snippets" and "correctOrder" are required.
            optionalProperties = emptyList(),
            description = "Schema for each story object in the content array."
        )

        // Combined object schema for "pagedetails_order_story".
        val schema: Schema = Schema.obj(
            properties = mapOf(
                "chapterTitle" to Schema.string(
                    description = "The title for this type of exercise and chapter."
                ),
                "taskDefinition" to Schema.string(
                    description = "The instruction to arrange the story in the correct order."
                ),
                "content" to Schema.array(
                    items = storyContentSchema,
                    description = "An array of story objects."
                )
            ),
            // All properties are required.
            optionalProperties = emptyList(),
            description = "Schema for pagedetails_order_story"
        )
    }
}