package com.example.data

import com.squareup.moshi.JsonClass
import java.util.UUID

@JsonClass(generateAdapter = true)
data class ApiEndpoint(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val baseUrl: String,
    val apiKey: String,
    val modelId: String
)
