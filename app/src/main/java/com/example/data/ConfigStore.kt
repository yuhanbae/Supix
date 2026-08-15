package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ConfigStore(private val context: Context) {
    companion object {
        val ENDPOINTS_JSON = stringPreferencesKey("endpoints_json")
        val ACTIVE_ENDPOINT_ID = stringPreferencesKey("active_endpoint_id")
        val SYSTEM_PROMPT = stringPreferencesKey("system_prompt")
        
        const val DEFAULT_SYSTEM_PROMPT = "You are supix_ai, a highly advanced agentic AI. You have full system access, internet search capabilities, and you communicate concisely and professionally."
    }

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val listType = Types.newParameterizedType(List::class.java, ApiEndpoint::class.java)
    private val adapter = moshi.adapter<List<ApiEndpoint>>(listType)

    private val defaultEndpoints = listOf(
        ApiEndpoint(
            id = "default-local",
            name = "Local Llama",
            baseUrl = "http://10.0.2.2:8080/v1",
            apiKey = "",
            modelId = "llama-3-8b"
        )
    )

    val endpoints: Flow<List<ApiEndpoint>> = context.dataStore.data.map { prefs ->
        val json = prefs[ENDPOINTS_JSON]
        if (json.isNullOrEmpty()) defaultEndpoints else adapter.fromJson(json) ?: defaultEndpoints
    }

    val activeEndpointId: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[ACTIVE_ENDPOINT_ID] ?: "default-local"
    }

    val systemPrompt: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[SYSTEM_PROMPT] ?: DEFAULT_SYSTEM_PROMPT
    }

    suspend fun saveEndpoints(newEndpoints: List<ApiEndpoint>, activeId: String) {
        context.dataStore.edit { prefs ->
            prefs[ENDPOINTS_JSON] = adapter.toJson(newEndpoints)
            prefs[ACTIVE_ENDPOINT_ID] = activeId
        }
    }

    suspend fun saveSystemPrompt(prompt: String) {
        context.dataStore.edit { prefs ->
            prefs[SYSTEM_PROMPT] = prompt
        }
    }
}
