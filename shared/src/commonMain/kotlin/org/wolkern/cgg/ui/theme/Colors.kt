package org.wolkern.cgg.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private data class AppColors(
    val background: Color,
    val surface: Color,
    val border: Color,
    val mutedSurface: Color,
    val deadBackground: Color,
    val stupidBackground: Color,
    val khorneRed: Color,
    val tzeentchGold: Color,
    val bonusGreen: Color,
    val malusRed: Color,
    val textPrimary: Color,
    val textSecondary: Color
)

private val DarkColors = AppColors(
    background = Color(0xFF0F0F12),
    surface = Color(0xFF16161C),
    border = Color(0xFF2C2C35),
    mutedSurface = Color(0xFF23232A),
    deadBackground = Color(0xFF1A1111),
    stupidBackground = Color(0xFF2A0808),
    khorneRed = Color(0xFF8B0000),
    tzeentchGold = Color(0xFFD4AF37),
    bonusGreen = Color(0xFF4CAF50),
    malusRed = Color(0xFFE57373),
    textPrimary = Color(0xFFE0E0E6),
    textSecondary = Color(0xFF888894)
)

private val LightColors = AppColors(
    background = Color(0xFFF3F0E8),
    surface = Color(0xFFFFFDF7),
    border = Color(0xFFD8D0C0),
    mutedSurface = Color(0xFFE7E1D5),
    deadBackground = Color(0xFFEFE3E3),
    stupidBackground = Color(0xFFF5E6E2),
    khorneRed = Color(0xFF8B0000),
    tzeentchGold = Color(0xFFB58B21),
    bonusGreen = Color(0xFF2E7D32),
    malusRed = Color(0xFFB3261E),
    textPrimary = Color(0xFF29251F),
    textSecondary = Color(0xFF68645D)
)

private val LocalAppColors = staticCompositionLocalOf { DarkColors }

@Composable
fun AppTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    val appColors = if (darkTheme) DarkColors else LightColors
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = appColors.tzeentchGold,
            error = appColors.khorneRed,
            background = appColors.background,
            surface = appColors.surface,
            onBackground = appColors.textPrimary,
            onSurface = appColors.textPrimary
        )
    } else {
        lightColorScheme(
            primary = appColors.tzeentchGold,
            error = appColors.khorneRed,
            background = appColors.background,
            surface = appColors.surface,
            onBackground = appColors.textPrimary,
            onSurface = appColors.textPrimary
        )
    }

    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(colorScheme = colorScheme, content = content)
    }
}

val DarkBg: Color
    @Composable get() = LocalAppColors.current.background
val CardBg: Color
    @Composable get() = LocalAppColors.current.surface
val BorderColor: Color
    @Composable get() = LocalAppColors.current.border
val MutedSurface: Color
    @Composable get() = LocalAppColors.current.mutedSurface
val DeadBackground: Color
    @Composable get() = LocalAppColors.current.deadBackground
val StupidBackground: Color
    @Composable get() = LocalAppColors.current.stupidBackground
val KhorneRed: Color
    @Composable get() = LocalAppColors.current.khorneRed
val TzeentchGold: Color
    @Composable get() = LocalAppColors.current.tzeentchGold
val BonusGreen: Color
    @Composable get() = LocalAppColors.current.bonusGreen
val MalusRed: Color
    @Composable get() = LocalAppColors.current.malusRed
val TextLight: Color
    @Composable get() = LocalAppColors.current.textPrimary
val TextDark: Color
    @Composable get() = LocalAppColors.current.textSecondary
