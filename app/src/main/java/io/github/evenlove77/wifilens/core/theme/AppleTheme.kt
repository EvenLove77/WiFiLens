package io.github.evenlove77.wifilens.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Apple 风格颜色令牌，可在任意 Composable 中通过 LocalAppleColors.current 访问
 */
data class AppleColorTokens(
    val background: androidx.compose.ui.graphics.Color = BackgroundDark,
    val surface: androidx.compose.ui.graphics.Color = SurfaceDark,
    val surfaceVariant: androidx.compose.ui.graphics.Color = SurfaceVariant,
    val primary: androidx.compose.ui.graphics.Color = AppleBlue,
    val secondary: androidx.compose.ui.graphics.Color = AppleCyan,
    val glassBg: androidx.compose.ui.graphics.Color = GlassBackground,
    val glassBorder: androidx.compose.ui.graphics.Color = GlassBorder,
    val textPrimary: androidx.compose.ui.graphics.Color = TextPrimary,
    val textSecondary: androidx.compose.ui.graphics.Color = TextSecondary,
    val textTertiary: androidx.compose.ui.graphics.Color = TextTertiary,
    val success: androidx.compose.ui.graphics.Color = SuccessGreen,
    val warning: androidx.compose.ui.graphics.Color = WarningOrange,
    val error: androidx.compose.ui.graphics.Color = ErrorRed,
)

val LocalAppleColors = staticCompositionLocalOf { AppleColorTokens() }

// Material3 暗色方案映射到 Apple 色板
private val AppleDarkColorScheme = darkColorScheme(
    primary = AppleBlue,
    secondary = AppleCyan,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceVariant,
    onPrimary = TextPrimary,
    onSecondary = TextPrimary,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    onError = TextPrimary,
)

/**
 * WiFiLens Apple 主题
 * - 强制深色模式（后续设置页面可切换）
 * - 不使用 Material3 dynamic color
 * - 注入自定义 AppleColorTokens
 */
@Composable
fun AppleTheme(
    darkTheme: Boolean = true, // 默认深色
    content: @Composable () -> Unit
) {
    val colorScheme = AppleDarkColorScheme
    val tokens = AppleColorTokens()

    CompositionLocalProvider(LocalAppleColors provides tokens) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppleTypography,
            content = content
        )
    }
}
