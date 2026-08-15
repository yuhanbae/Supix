package com.example.api

import com.example.data.ChatRequest
import com.example.data.ChatResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url

interface ApiService {
    @POST
    suspend fun createChatCompletion(
        @Url url: String,
        @Header("Authorization") authHeader: String?,
        @Body request: ChatRequest
    ): ChatResponse
}
