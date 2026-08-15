package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ApiEndpoint
import com.example.data.ConfigStore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val configStore = remember { ConfigStore(context) }
    val scope = rememberCoroutineScope()

    val endpoints by configStore.endpoints.collectAsStateWithLifecycle(emptyList())
    val activeEndpointId by configStore.activeEndpointId.collectAsStateWithLifecycle("")
    val systemPrompt by configStore.systemPrompt.collectAsStateWithLifecycle("")

    var promptInput by remember(systemPrompt) { mutableStateOf(systemPrompt) }
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Config", fontWeight = FontWeight.Medium) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // System Prompt Section
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "System Settings",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    OutlinedTextField(
                        value = promptInput,
                        onValueChange = { promptInput = it },
                        label = { Text("Agent System Prompt") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                    Button(
                        onClick = { scope.launch { configStore.saveSystemPrompt(promptInput) } },
                        modifier = Modifier.align(Alignment.End),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Save Prompt")
                    }
                }
            }

            // API Endpoints Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "API Endpoints",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Endpoint", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            items(endpoints) { endpoint ->
                val isActive = endpoint.id == activeEndpointId
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(
                            width = if (isActive) 2.dp else 1.dp,
                            color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable {
                            scope.launch { configStore.saveEndpoints(endpoints, endpoint.id) }
                        }
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = endpoint.name,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            if (isActive) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Active", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Text(text = "Model: ${endpoint.modelId}", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                        Text(text = "URL: ${endpoint.baseUrl}", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                        
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            if (endpoints.size > 1) {
                                TextButton(
                                    onClick = {
                                        val newList = endpoints.filter { it.id != endpoint.id }
                                        val newActive = if (isActive) newList.first().id else activeEndpointId
                                        scope.launch { configStore.saveEndpoints(newList, newActive) }
                                    },
                                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var baseUrl by remember { mutableStateOf("") }
        var apiKey by remember { mutableStateOf("") }
        var modelId by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add API Endpoint", color = MaterialTheme.colorScheme.primary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = name, onValueChange = { name = it },
                        label = { Text("Name (e.g. OpenAI, LM Studio)") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = baseUrl, onValueChange = { baseUrl = it },
                        label = { Text("Base URL") },
                        placeholder = { Text("http://10.0.2.2:8080/v1") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = apiKey, onValueChange = { apiKey = it },
                        label = { Text("API Key") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = modelId, onValueChange = { modelId = it },
                        label = { Text("Model ID") },
                        placeholder = { Text("gpt-4") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank() && baseUrl.isNotBlank() && modelId.isNotBlank()) {
                            val newEndpoint = ApiEndpoint(name = name, baseUrl = baseUrl, apiKey = apiKey, modelId = modelId)
                            val newList = endpoints + newEndpoint
                            scope.launch {
                                configStore.saveEndpoints(newList, newEndpoint.id)
                                showAddDialog = false
                            }
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            textContentColor = MaterialTheme.colorScheme.onBackground
        )
    }
}
