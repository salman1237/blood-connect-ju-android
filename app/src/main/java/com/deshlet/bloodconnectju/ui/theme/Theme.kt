package com.deshlet.bloodconnectju.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// The web app is light-only (no dark-mode palette defined in app.css), so
// this theme is too — one scheme, matching it exactly, rather than
// inventing a dark variant that doesn't exist on the site.
//
// Every role below is set explicitly, including the surfaceContainer*/
// *Container ones the web app has no real equivalent for — Card and
// several other Material3 components default to those roles for their
// background rather than plain `surface`, and lightColorScheme() silently
// fills in any role you don't pass with Material3's own baseline purple
// seed palette. Confirmed live on-device: unset containers rendered every
// card with a visible lavender tint despite every other color being
// correct — same class of bug as Phase 1's dynamic-color issue, just one
// layer deeper (an unset role, not an overridden one).
private val BloodConnectColorScheme = lightColorScheme(
    primary = BcPrimary,
    onPrimary = BcPrimaryForeground,
    primaryContainer = BcAccent,
    onPrimaryContainer = BcAccentForeground,
    inversePrimary = BcPrimary,
    secondary = BcSecondary,
    onSecondary = BcSecondaryForeground,
    secondaryContainer = BcSecondary,
    onSecondaryContainer = BcSecondaryForeground,
    tertiary = BcAccent,
    onTertiary = BcAccentForeground,
    tertiaryContainer = BcAccent,
    onTertiaryContainer = BcAccentForeground,
    background = BcBackground,
    onBackground = BcForeground,
    surface = BcCard,
    onSurface = BcForeground,
    surfaceVariant = BcSurface,
    onSurfaceVariant = BcMutedForeground,
    surfaceTint = BcPrimary,
    surfaceBright = BcCard,
    surfaceDim = BcSurface,
    surfaceContainerLowest = BcCard,
    surfaceContainerLow = BcSurface,
    surfaceContainer = BcSurface,
    surfaceContainerHigh = BcSecondary,
    surfaceContainerHighest = BcSecondary,
    inverseSurface = BcForeground,
    inverseOnSurface = BcBackground,
    error = BcDestructive,
    onError = BcDestructiveForeground,
    errorContainer = BcAccent,
    onErrorContainer = BcAccentForeground,
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
