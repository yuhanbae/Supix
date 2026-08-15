package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.ChatScreen
import com.example.ui.ChatViewModel
import com.example.ui.DashboardScreen
import com.example.ui.SettingsScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background
        ) {
          OmniClawApp()
        }
      }
    }
  }
}

@Composable
fun OmniClawApp() {
  val navController = rememberNavController()
  val chatViewModel: ChatViewModel = viewModel()
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = navBackStackEntry?.destination?.route

  Scaffold(
    bottomBar = {
      NavigationBar(
        containerColor = Color(0xFF211F26),
        contentColor = MaterialTheme.colorScheme.onBackground
      ) {
        NavigationBarItem(
          icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
          label = { Text("Core") },
          selected = currentRoute == "dashboard",
          onClick = {
            navController.navigate("dashboard") {
              popUpTo(navController.graph.startDestinationId) { saveState = true }
              launchSingleTop = true
              restoreState = true
            }
          },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            indicatorColor = Color(0xFF4A4458),
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
          )
        )
        NavigationBarItem(
          icon = { Icon(Icons.Default.Chat, contentDescription = "Agent") },
          label = { Text("Agent") },
          selected = currentRoute == "chat",
          onClick = {
            navController.navigate("chat") {
              popUpTo(navController.graph.startDestinationId) { saveState = true }
              launchSingleTop = true
              restoreState = true
            }
          },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            indicatorColor = Color(0xFF4A4458),
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
          )
        )
        NavigationBarItem(
          icon = { Icon(Icons.Default.Settings, contentDescription = "Config") },
          label = { Text("Config") },
          selected = currentRoute == "settings",
          onClick = {
            navController.navigate("settings") {
              popUpTo(navController.graph.startDestinationId) { saveState = true }
              launchSingleTop = true
              restoreState = true
            }
          },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            indicatorColor = Color(0xFF4A4458),
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
          )
        )
      }
    }
  ) { innerPadding ->
    NavHost(
      navController = navController,
      startDestination = "dashboard",
      modifier = Modifier.padding(innerPadding)
    ) {
      composable("dashboard") {
        DashboardScreen(
          viewModel = chatViewModel,
          onNavigateToChat = { navController.navigate("chat") },
          onNavigateToSettings = { navController.navigate("settings") }
        )
      }
      composable("chat") {
        ChatScreen(
          viewModel = chatViewModel,
          onNavigateToSettings = { navController.navigate("settings") }
        )
      }
      composable("settings") {
        SettingsScreen(
          onNavigateBack = { navController.popBackStack() }
        )
      }
    }
  }
}
