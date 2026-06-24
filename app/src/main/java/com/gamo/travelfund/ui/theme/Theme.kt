package com.gamo.travelfund.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary              = Teal200,
    onPrimary            = Teal800,
    primaryContainer     = Teal800,
    onPrimaryContainer   = Teal100,

    secondary            = Green200,
    onSecondary          = Color(0xFF173404),
    secondaryContainer   = Color(0xFF27500A),
    onSecondaryContainer = Green100,

    tertiary             = Amber200,
    onTertiary           = Color(0xFF412402),
    tertiaryContainer    = Color(0xFF633806),
    onTertiaryContainer  = Amber100,

    error                = ErrorRedDark,
    onError              = Color(0xFF501313),
    errorContainer       = Color(0xFF791F1F),
    onErrorContainer     = Color(0xFFF7C1C1),

    surface              = Color(0xFF1C2420),
    onSurface            = Color(0xFFE4F0EC),
    surfaceVariant       = Color(0xFF253330),
    onSurfaceVariant     = Color(0xFFA8C4BC),
    outline              = Color(0xFF4A6660),
    outlineVariant       = Color(0xFF2E4440),

    background           = Color(0xFF11181C),  // sin tocar
    onBackground         = Color(0xFFE4F0EC),
)

private val LightColorScheme = lightColorScheme(
    primary              = Teal400,
    onPrimary            = Color.White,
    primaryContainer     = Teal50,
    onPrimaryContainer   = Teal600,

    secondary            = Green400,
    onSecondary          = Color.White,
    secondaryContainer   = Green50,
    onSecondaryContainer = Green600,

    tertiary             = Amber400,
    onTertiary           = Color.White,
    tertiaryContainer    = Amber50,
    onTertiaryContainer  = Amber600,

    error                = ErrorRed,
    onError              = Color.White,
    errorContainer       = ErrorRedLight,
    onErrorContainer     = Color(0xFFA32D2D),

    surface              = Color.White,
    onSurface            = Color(0xFF1A1A1A),
    surfaceVariant       = NeutralLight,
    onSurfaceVariant     = NeutralGray,
    outline              = Color(0xFFB4B2A9),
    outlineVariant       = Color(0xFFE5E2DE),

    background           = Color(0xFFE5E4E6),  // sin tocar
    onBackground         = Color(0xFF1A1A1A),
)

@Composable
fun TravelFundTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}