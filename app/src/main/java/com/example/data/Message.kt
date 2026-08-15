package com.example.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatMessage(
    val role: String, // "user", "assistant", "system"
    val content: String
)

@JsonClass(generateAdapter = true)
data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val stream: Boolean = false
)

@JsonClass(generateAdapter = true)
data class ChatResponse(
    val id: String?,
    val choices: List<Choice>?
)

@JsonClass(generateAdapter = true)
data class Choice(
    val message: ChatMessage?
)
