package com.example.pocket20.ui.theme

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

val AppGray = Color(0xFF606060)
val PureWhite = Color(0xFFFFFFFF)
val PureBlack = Color(0xFF000000)


private val DarkColorScheme = darkColorScheme(
    primary = PureWhite,
    background = PureWhite,
    surface = PureWhite,

    surfaceContainer = PureWhite,
    secondaryContainer = PureWhite,
    onSecondaryContainer = PureBlack,

    onPrimary = PureBlack,
    onBackground = PureBlack,
    onSurface = PureBlack
)

private val LightColorScheme = lightColorScheme(
    primary = PureWhite,
    background = PureWhite,
    surface = PureWhite,

    surfaceContainer = PureWhite,
    secondaryContainer = PureWhite,
    onSecondaryContainer = PureBlack,

    onPrimary = PureBlack,
    onBackground = PureBlack,
    onSurface = PureBlack
)

@Composable
fun Pocket20Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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