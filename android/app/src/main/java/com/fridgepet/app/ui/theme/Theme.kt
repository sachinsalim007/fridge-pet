package com.fridgepet.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Stitch "Fresh Sprout" Design System Tokens (Project ID: 8343919699699103156)
val FrostCanvas = Color(0xFFF4F8F5)
val PureSurface = Color(0xFFFFFFFF)
val SagePrimary = Color(0xFF1F6B4F)
val MintContainer = Color(0xFFD8F3E5)
val DarkSageOnContainer = Color(0xFF002115)
val WarmAmber = Color(0xFFF2A93B)
val ApricotContainer = Color(0xFFFEF3C7)
val AmberText = Color(0xFF995D00)
val CoralDanger = Color(0xFFE5604D)
val CoralContainer = Color(0xFFFFE4E6)
val CoralText = Color(0xFFB83220)
val InkSlate = Color(0xFF14201B)
val MutedSage = Color(0xFF6B7C74)
val SubtleBorder = Color(0xFFBFC9C1)
val SurfaceContainerLow = Color(0xFFE9F7EE)

private val FridgePetColors = lightColorScheme(
    primary = SagePrimary,
    onPrimary = Color.White,
    primaryContainer = MintContainer,
    onPrimaryContainer = DarkSageOnContainer,
    secondary = WarmAmber,
    onSecondary = Color.White,
    secondaryContainer = ApricotContainer,
    onSecondaryContainer = AmberText,
    tertiary = CoralDanger,
    tertiaryContainer = CoralContainer,
    onTertiaryContainer = CoralText,
    error = CoralDanger,
    errorContainer = CoralContainer,
    onError = Color.White,
    onErrorContainer = CoralText,
    background = FrostCanvas,
    onBackground = InkSlate,
    surface = PureSurface,
    onSurface = InkSlate,
    surfaceVariant = SurfaceContainerLow,
    onSurfaceVariant = MutedSage,
    outline = SubtleBorder,
)

private val FridgePetTypography = Typography(
    headlineLarge = TextStyle(
        fontWeight = FontWeight.ExtraBold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = (-0.5).sp,
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.3).sp,
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 19.sp,
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        lineHeight = 14.sp,
    ),
)

@Composable
fun FridgePetTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = FridgePetColors,
        typography = FridgePetTypography,
        content = content,
    )
}
