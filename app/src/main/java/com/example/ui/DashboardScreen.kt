package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.LogType

@Composable
fun DashboardScreen(
    viewModel: ChatViewModel,
    onNavigateToChat: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val consoleLogs by viewModel.consoleLogs.collectAsStateWithLifecycle()

    val activeEndpoint by viewModel.activeEndpoint.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
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
                    onClick = { /* TODO: Vulkan settings */ },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                ) {
                    Icon(
                        Icons.Default.Speed,
                        contentDescription = "Vulkan",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(
                    onClick = { /* TODO: Power */ },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                ) {
                    Icon(
                        Icons.Default.Bolt,
                        contentDescription = "Power",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Model Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF381E72),
                                Color(0xFF1C1B1F)
                            )
                        )
                    )
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(bottom = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFB6FFB1))
                                )
                                Text(
                                    text = activeEndpoint?.name?.uppercase() ?: "NO ENDPOINT",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Text(
                                text = activeEndpoint?.modelId ?: "Select Model",
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = activeEndpoint?.baseUrl ?: "No Connection",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }
                        Button(
                            onClick = onNavigateToSettings,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color(0xFF381E72)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("SWAP", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatBox("GPU Use", "84%", Modifier.weight(1f))
                        StatBox("Latency", "12ms", Modifier.weight(1f))
                        StatBox("Tokens", "42/s", Modifier.weight(1f))
                    }
                }
            }

            // Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ToolCard(
                    title = "Web Agent",
                    subtitle = "Google Search Enabled",
                    icon = Icons.Default.Public,
                    modifier = Modifier.weight(1f)
                )
                ToolCard(
                    title = "Deployments",
                    subtitle = "Vercel & GitHub",
                    icon = Icons.Default.Terminal,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ToolCard(
                    title = "Telegram",
                    subtitle = "Bridge Connected",
                    icon = Icons.Default.Send,
                    modifier = Modifier.weight(1f)
                )
                ToolCard(
                    title = "Add Skill",
                    subtitle = "Create Agent Tools",
                    icon = Icons.Default.AddCircle,
                    modifier = Modifier.weight(1f)
                )
            }

            // Console
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "AGENT CONSOLE",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "v2.4.0-stable",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF000000))
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    androidx.compose.foundation.lazy.LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        reverseLayout = true
                    ) {
                        items(consoleLogs.reversed()) { log ->
                            val color = when(log.type) {
                                LogType.SYSTEM -> Color(0xFFB6FFB1)
                                LogType.USER -> Color(0xCCFFFFFF)
                                LogType.AGENT -> MaterialTheme.colorScheme.primary
                                LogType.ERROR -> MaterialTheme.colorScheme.error
                            }
                            Text(
                                text = log.message,
                                color = color,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun StatBox(label: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x801C1B1F))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        Column {
            Text(
                text = label.uppercase(),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                fontSize = 9.sp
            )
            Text(
                text = value,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
fun ToolCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .clickable { /* TODO */ }
            .padding(16.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF4A4458))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
