package com.deshlet.bloodconnectju.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Anchored on the web app's --radius: 0.75rem (12dp) token as Material3's
// "medium" role, which is what buttons/text fields use by default — the
// rounded, soft-card look carries over instead of Material3's stock corners.
val BloodConnectShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp),
)
