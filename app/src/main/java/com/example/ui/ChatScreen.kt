package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChatMessage
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    onNavigateToSettings: () -> Unit
) {
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val isTtsEnabled by viewModel.isTtsEnabled.collectAsStateWithLifecycle()

    var inputText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "SYSTEM ACTIVE",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "supix_ai",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        IconButton(
                            onClick = { viewModel.toggleTts() },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isTtsEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                                contentDescription = "Toggle TTS",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        IconButton(
                            onClick = { viewModel.clearChat() },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Clear Chat",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        IconButton(
                            onClick = onNavigateToSettings,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                        ) {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (messages.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                "No messages yet. Configure your API key and endpoint in Settings.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(32.dp)
                            )
                        }
                    }
                }
                items(messages) { message ->
                    ChatBubble(message)
                }
                if (isLoading) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
                if (error != null) {
                    item {
                        Text(
                            text = error ?: "",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(8.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF211F26)) // Darker bottom nav area
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .padding(16.dp)
                    .navigationBarsPadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input"),
                    placeholder = { Text("Message AI...", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)) },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    )
                )
                Spacer(modifier = Modifier.width(12.dp))
                IconButton(
                    onClick = {
                        viewModel.sendMessage(inputText)
                        inputText = ""
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .testTag("send_button")
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Message",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.role == "user"
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        val bubbleShape = RoundedCornerShape(
            topStart = 24.dp,
            topEnd = 24.dp,
            bottomStart = if (isUser) 24.dp else 4.dp,
            bottomEnd = if (isUser) 4.dp else 24.dp
        )
        
        Box(
            modifier = Modifier
                .clip(bubbleShape)
                .then(
                    if (isUser) {
                        Modifier.background(MaterialTheme.colorScheme.primary)
                    } else {
                        Modifier
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF381E72),
                                        Color(0xFF1C1B1F)
                                    )
                                )
                            )
                            .border(1.dp, MaterialTheme.colorScheme.outline, bubbleShape)
                    }
                )
                .padding(16.dp)
                .widthIn(max = 280.dp)
        ) {
            Text(
                text = message.content,
                color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )
        }
    }
}
