package com.deshlet.bloodconnectju.ui.components

import androidx.compose.material3.FilterChipDefaults
import androidx.compose.runtime.Composable
import com.deshlet.bloodconnectju.ui.theme.BcAccent
import com.deshlet.bloodconnectju.ui.theme.BcAccentForeground

/**
 * A selected FilterChip's default colors come from Material3's
 * `secondaryContainer`/`onSecondaryContainer` — mapped to `BcSecondary`
 * (`#F3EDE9`) in this app's theme, which is nearly identical to `Card`'s own
 * default background (`BcSurface`, `#F6F3EF`). Any selected chip placed
 * inside a `Card` (first hit in the Profile screen) becomes visually
 * invisible — no border, no fill contrast, just bare unstyled-looking text.
 * Confirmed live on-device, then confirmed against the web app's own CSS
 * (`has-[:checked]:bg-accent has-[:checked]:border-primary`): web's real
 * "selected" treatment was never `secondary` at all — it's `accent`, which
 * has real contrast against every surface in this palette. Every FilterChip
 * in the app should use this instead of the Material3 default.
 */
@Composable
fun selectedChipColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = BcAccent,
    selectedLabelColor = BcAccentForeground,
)
