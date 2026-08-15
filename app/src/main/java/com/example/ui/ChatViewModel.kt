package com.example.ui

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.ApiService
import com.example.data.ApiEndpoint
import com.example.data.ChatMessage
import com.example.data.ChatRequest
import com.example.data.ConfigStore
import com.example.data.ConsoleLog
import com.example.data.LogType
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.Locale

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val configStore = ConfigStore(application)
    
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openai.com/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    val activeEndpoint: StateFlow<ApiEndpoint?> = combine(configStore.endpoints, configStore.activeEndpointId) { endpoints, activeId ->
        endpoints.find { it.id == activeId } ?: endpoints.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _consoleLogs = MutableStateFlow<List<ConsoleLog>>(
        listOf(
            ConsoleLog("[SYSTEM] supix_ai backend initialized...", LogType.SYSTEM),
            ConsoleLog("[SYSTEM] Ready for inference connections.", LogType.SYSTEM)
        )
    )
    val consoleLogs: StateFlow<List<ConsoleLog>> = _consoleLogs.asStateFlow()

    private var tts: TextToSpeech? = null
    var isTtsEnabled = MutableStateFlow(true)

    init {
        tts = TextToSpeech(application) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
            }
        }
    }

    override fun onCleared() {
        tts?.stop()
        tts?.shutdown()
        super.onCleared()
    }

    fun addConsoleLog(message: String, type: LogType) {
        val current = _consoleLogs.value.toMutableList()
        current.add(ConsoleLog(message, type))
        if (current.size > 50) current.removeAt(0) // Keep last 50 logs
        _consoleLogs.value = current
    }

    fun sendMessage(content: String) {
        if (content.isBlank()) return

        val userMessage = ChatMessage(role = "user", content = content)
        _messages.value = _messages.value + userMessage
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val endpointConfig = activeEndpoint.value
                if (endpointConfig == null) {
                    _error.value = "No API Endpoint configured"
                    _isLoading.value = false
                    return@launch
                }
                
                val baseUrl = endpointConfig.baseUrl.trimEnd('/')
                val apiKey = endpointConfig.apiKey.trim()
                val modelId = endpointConfig.modelId.trim()
                val systemPrompt = configStore.systemPrompt.first().trim()

                addConsoleLog("> Routing prompt to $modelId...", LogType.USER)

                val endpoint = "$baseUrl/chat/completions"
                val authHeader = if (apiKey.isNotEmpty()) "Bearer $apiKey" else null

                val messagesToSend = mutableListOf<ChatMessage>()
                if (systemPrompt.isNotEmpty()) {
                    messagesToSend.add(ChatMessage(role = "system", content = systemPrompt))
                }
                messagesToSend.addAll(_messages.value)

                val request = ChatRequest(
                    model = modelId,
                    messages = messagesToSend
                )

                val startTime = System.currentTimeMillis()
                
                val response = apiService.createChatCompletion(
                    url = endpoint,
                    authHeader = authHeader,
                    request = request
                )

                val latency = System.currentTimeMillis() - startTime

                val assistantContent = response.choices?.firstOrNull()?.message?.content
                if (assistantContent != null) {
                    val assistantMessage = ChatMessage(role = "assistant", content = assistantContent)
                    _messages.value = _messages.value + assistantMessage
                    addConsoleLog("[AGENT] Generation complete. Latency: ${latency}ms", LogType.AGENT)
                    
                    if (isTtsEnabled.value) {
                        tts?.speak(assistantContent, TextToSpeech.QUEUE_FLUSH, null, null)
                    }
                } else {
                    _error.value = "Empty response from API"
                    addConsoleLog("[ERROR] Empty response from API", LogType.ERROR)
                }

            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                addConsoleLog("[ERROR] ${e.message}", LogType.ERROR)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun toggleTts() {
        isTtsEnabled.value = !isTtsEnabled.value
        val state = if(isTtsEnabled.value) "enabled" else "disabled"
        addConsoleLog("[SYSTEM] Text-to-Speech $state.", LogType.SYSTEM)
    }
    
    fun clearChat() {
        _messages.value = emptyList()
        _error.value = null
        addConsoleLog("[SYSTEM] Agent memory cleared.", LogType.SYSTEM)
    }
}
