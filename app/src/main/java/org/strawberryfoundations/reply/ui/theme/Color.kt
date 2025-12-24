package org.strawberryfoundations.reply.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt


val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFE57373),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFFB75D5D),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFFFF8A65),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFB26A5E),
    onSecondaryContainer = Color.White,
    tertiary = Color(0xFFF06292),
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFFB2557A),
    onTertiaryContainer = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = Color.White,
    background = Color.Black,
    onBackground = Color.White,
    error = Color(0xFFCF6679),
    onError = Color.Black,
    errorContainer = Color(0xFFB00020),
    onErrorContainer = Color.White,
    outline = Color(0xFFBCAAA4),
    outlineVariant = Color(0xFF8D6E63),
    surfaceContainerLow = Color(0xFF2D2323),
    surfaceContainer = Color(0xFF2D2323),
    surfaceContainerHigh = Color(0xFF2D2323),
    inversePrimary = Color(0xFFE57373),
    surface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFF3A3A3A),
    surfaceTint = Color(0xFFE57373),
    inverseSurface = Color.White,
    inverseOnSurface = Color.Black,
    scrim = Color.Black,
    surfaceBright = Color(0xFF3A3A3A),
    surfaceDim = Color(0xFF121212),
    surfaceContainerHighest = Color(0xFF3A3A3A),
    surfaceContainerLowest = Color(0xFF0F0F0F),
    primaryFixed = Color(0xFFE57373),
    primaryFixedDim = Color(0xFFB75D5D),
    onPrimaryFixed = Color.Black,
    onPrimaryFixedVariant = Color.Black,
    secondaryFixed = Color(0xFFFF8A65),
    secondaryFixedDim = Color(0xFFB26A5E),
    onSecondaryFixed = Color.Black,
    onSecondaryFixedVariant = Color.Black,
    tertiaryFixed = Color(0xFFF06292),
    tertiaryFixedDim = Color(0xFFB2557A),
    onTertiaryFixed = Color.Black,
    onTertiaryFixedVariant = Color.Black,
)

val LightColorScheme = lightColorScheme(
    primary = Color(0xFFE57373),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFFB75D5D),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFFFF8A65),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFB26A5E),
    onSecondaryContainer = Color.White,
    tertiary = Color(0xFFF06292),
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFFB2557A),
    onTertiaryContainer = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = Color.White,
    background = Color.Black,
    onBackground = Color.White,
    error = Color(0xFFCF6679),
    onError = Color.Black,
    errorContainer = Color(0xFFB00020),
    onErrorContainer = Color.White,
    outline = Color(0xFFBCAAA4),
    outlineVariant = Color(0xFF8D6E63),
    surfaceContainerLow = Color(0xFF2D2323),
    surfaceContainer = Color(0xFF2D2323),
    surfaceContainerHigh = Color(0xFF2D2323),
    inversePrimary = Color(0xFFE57373),
    surface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFF3A3A3A),
    surfaceTint = Color(0xFFE57373),
    inverseSurface = Color.White,
    inverseOnSurface = Color.Black,
    scrim = Color.Black,
    surfaceBright = Color(0xFF3A3A3A),
    surfaceDim = Color(0xFF121212),
    surfaceContainerHighest = Color(0xFF3A3A3A),
    surfaceContainerLowest = Color(0xFF0F0F0F),
    primaryFixed = Color(0xFFE57373),
    primaryFixedDim = Color(0xFFB75D5D),
    onPrimaryFixed = Color.Black,
    onPrimaryFixedVariant = Color.Black,
    secondaryFixed = Color(0xFFFF8A65),
    secondaryFixedDim = Color(0xFFB26A5E),
    onSecondaryFixed = Color.Black,
    onSecondaryFixedVariant = Color.Black,
    tertiaryFixed = Color(0xFFF06292),
    tertiaryFixedDim = Color(0xFFB2557A),
    onTertiaryFixed = Color.Black,
    onTertiaryFixedVariant = Color.Black,
)


fun colorToHex(color: Color): String =
    "#%02X%02X%02X".format(
        (color.red * 255).toInt(),
        (color.green * 255).toInt(),
        (color.blue * 255).toInt()
    )

fun hexToColor(hex: String): Color =
    runCatching { Color(hex.toColorInt()) }.getOrDefault(Color.LightGray)

fun darkenColor(color: Color, factor: Float = 0.85f): Color {
    return Color(
        red = (color.red * factor).coerceIn(0f, 1f),
        green = (color.green * factor).coerceIn(0f, 1f),
        blue = (color.blue * factor).coerceIn(0f, 1f),
        alpha = color.alpha
    )
}