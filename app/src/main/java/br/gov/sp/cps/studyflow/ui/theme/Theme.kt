package br.gov.sp.cps.studyflow.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = RoxoPrimary,
    onPrimary = Color.White,
    primaryContainer = RoxoLightContainer,
    onPrimaryContainer = RoxoOnContainer,
    secondary = LilasSecondary,
    onSecondary = GrafiteDark,
    secondaryContainer = LilasContainer,
    onSecondaryContainer = LilasOnContainer,
    tertiary = SoftPeachText,
    tertiaryContainer = SoftPeachCard,
    onTertiaryContainer = SoftPeachText,
    background = LavandaBackground,
    onBackground = GrafiteDark,
    surface = SurfaceLight,
    onSurface = GrafiteDark,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = Color(0xFF5A5675)
)

private val DarkColorScheme = darkColorScheme(
    primary = LilasSecondary,
    onPrimary = GrafiteDark,
    primaryContainer = Color(0xFF383359),
    onPrimaryContainer = TextWhite,
    secondary = RoxoPrimary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF423D66),
    onSecondaryContainer = TextWhite,
    tertiary = Color(0xFFFFB4A9),
    background = GrafiteBackground,
    onBackground = TextWhite,
    surface = GrafiteSurface,
    onSurface = TextWhite,
    surfaceVariant = GrafiteSurfaceVariant,
    onSurfaceVariant = Color(0xFFD4CEE8)
)

@Composable
fun StudyflowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
