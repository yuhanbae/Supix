package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val ImmersiveColorScheme = darkColorScheme(
    primary = ImmersivePrimary,
    onPrimary = ImmersivePrimaryDark,
    background = ImmersiveBackground,
    onBackground = ImmersiveOnBackground,
    surface = ImmersiveBackground,
    onSurface = ImmersiveOnBackground,
    surfaceVariant = ImmersiveSurface,
    onSurfaceVariant = ImmersiveOnSurfaceVariant,
    outline = ImmersiveOutline
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force dark theme for Immersive UI
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = false, // Disable dynamic colors to enforce the theme
  content: @Composable () -> Unit,
) {
  val colorScheme = ImmersiveColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
