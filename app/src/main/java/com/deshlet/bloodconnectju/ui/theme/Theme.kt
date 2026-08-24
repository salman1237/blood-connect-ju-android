package com.deshlet.bloodconnectju.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// The web app is light-only (no dark-mode palette defined in app.css), so
// this theme is too — one scheme, matching it exactly, rather than
// inventing a dark variant that doesn't exist on the site.
private val BloodConnectColorScheme = lightColorScheme(
    primary = BcPrimary,
    onPrimary = BcPrimaryForeground,
    secondary = BcSecondary,
    onSecondary = BcSecondaryForeground,
    tertiary = BcAccent,
    onTertiary = BcAccentForeground,
    background = BcBackground,
    onBackground = BcForeground,
    surface = BcCard,
    onSurface = BcForeground,
    surfaceVariant = BcSurface,
    onSurfaceVariant = BcMutedForeground,
    error = BcDestructive,
    onError = BcDestructiveForeground,
    outline = BcBorder,
    outlineVariant = BcBorder,
)

@Composable
fun BloodConnectJUTheme(content: @Composable () -> Unit) {
    // No dynamic-color / Material You path here on purpose — that reads
    // the device wallpaper on Android 12+ and silently overrides every
    // color below, which is exactly what made the first build look like
    // generic Material Design instead of the actual brand.
    MaterialTheme(
        colorScheme = BloodConnectColorScheme,
        typography = Typography,
        shapes = BloodConnectShapes,
        content = content,
    )
}
