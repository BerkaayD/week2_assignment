package com.example.week2_assignment.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ClusterColors = darkColorScheme(
    primary = Color(0xFF54D6BC), onPrimary = Color(0xFF00382D),
    primaryContainer = Color(0xFF183B38), onPrimaryContainer = Color(0xFFB4F5E6),
    secondary = Color(0xFFA9C7FF), background = Color(0xFF0B1220),
    onBackground = Color(0xFFE5EDF9), surface = Color(0xFF141F30),
    onSurface = Color(0xFFE5EDF9), onSurfaceVariant = Color(0xFF9DAEC5),
    outline = Color(0xFF53637A), outlineVariant = Color(0xFF293A50),
    error = Color(0xFFFFB4AB)
)

@Composable
fun DevClusterTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = ClusterColors, typography = Typography, content = content)
}
