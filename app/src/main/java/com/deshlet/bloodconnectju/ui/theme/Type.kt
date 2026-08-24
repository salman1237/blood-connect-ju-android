package com.deshlet.bloodconnectju.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.deshlet.bloodconnectju.R

// Same font the web app loads (resources/css/app.css: font-family: 'Public
// Sans', ...) — bundled locally as a single variable font (one file covers
// the whole weight axis) rather than fetched via the Downloadable Fonts
// provider, which silently falls back to the system font on any device
// without a signed-in Google account (confirmed on a fresh emulator: Play
// Services short-circuited the resolution to failure). Bundling guarantees
// every user actually sees the real brand font, not a maybe.
@OptIn(ExperimentalTextApi::class)
private fun publicSans(weight: FontWeight) = Font(
    resId = R.font.public_sans,
    weight = weight,
    variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)),
)

private val PublicSans = FontFamily(
    publicSans(FontWeight.Normal),
    publicSans(FontWeight.Medium),
    publicSans(FontWeight.SemiBold),
    publicSans(FontWeight.Bold),
)

private val baseTypography = Typography()

val Typography = Typography(
    displayLarge = baseTypography.displayLarge.copy(fontFamily = PublicSans),
    displayMedium = baseTypography.displayMedium.copy(fontFamily = PublicSans),
    displaySmall = baseTypography.displaySmall.copy(fontFamily = PublicSans),
    headlineLarge = baseTypography.headlineLarge.copy(fontFamily = PublicSans, fontWeight = FontWeight.SemiBold),
    headlineMedium = baseTypography.headlineMedium.copy(fontFamily = PublicSans, fontWeight = FontWeight.SemiBold),
    headlineSmall = baseTypography.headlineSmall.copy(fontFamily = PublicSans, fontWeight = FontWeight.SemiBold),
    titleLarge = baseTypography.titleLarge.copy(fontFamily = PublicSans, fontWeight = FontWeight.SemiBold),
    titleMedium = baseTypography.titleMedium.copy(fontFamily = PublicSans, fontWeight = FontWeight.Medium),
    titleSmall = baseTypography.titleSmall.copy(fontFamily = PublicSans, fontWeight = FontWeight.Medium),
    bodyLarge = baseTypography.bodyLarge.copy(fontFamily = PublicSans),
    bodyMedium = baseTypography.bodyMedium.copy(fontFamily = PublicSans),
    bodySmall = baseTypography.bodySmall.copy(fontFamily = PublicSans),
    labelLarge = baseTypography.labelLarge.copy(fontFamily = PublicSans, fontWeight = FontWeight.Medium),
    labelMedium = baseTypography.labelMedium.copy(fontFamily = PublicSans, fontWeight = FontWeight.Medium),
    labelSmall = baseTypography.labelSmall.copy(fontFamily = PublicSans, fontWeight = FontWeight.Medium),
)
