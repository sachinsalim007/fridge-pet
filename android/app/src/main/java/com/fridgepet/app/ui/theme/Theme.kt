package com.fridgepet.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val PantryCream = Color(0xFFFBF8F3)
val MilkSurface = Color(0xFFFFFFFF)
val SagePrimary = Color(0xFF1F6E58)
val MintContainer = Color(0xFFD8F3E5)
val TerracottaSecondary = Color(0xFFC2593F)
val ApricotWarn = Color(0xFFD97706)
val ApricotContainer = Color(0xFFFEF3C7)
val BerryError = Color(0xFFBE123C)
val BerryContainer = Color(0xFFFFE4E6)
val InkCharcoal = Color(0xFF1C2522)
val MutedSlate = Color(0xFF5A6863)
val SoftOutline = Color(0xFFE5DFD5)

private val FridgePetColors = lightColorScheme(
    primary = SagePrimary,
    onPrimary = Color.White,
    primaryContainer = MintContainer,
    onPrimaryContainer = Color(0xFF0A382B),
    secondary = TerracottaSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFCE8E2),
    onSecondaryContainer = Color(0xFF5D2214),
    tertiary = ApricotWarn,
    tertiaryContainer = ApricotContainer,
    error = BerryError,
    errorContainer = BerryContainer,
    background = PantryCream,
    onBackground = InkCharcoal,
    surface = MilkSurface,
    onSurface = InkCharcoal,
    surfaceVariant = Color(0xFFF3EEE5),
    onSurfaceVariant = MutedSlate,
    outline = SoftOutline,
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
)

@Composable
fun FridgePetTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = FridgePetColors,
        typography = FridgePetTypography,
        content = content,
    )
}
