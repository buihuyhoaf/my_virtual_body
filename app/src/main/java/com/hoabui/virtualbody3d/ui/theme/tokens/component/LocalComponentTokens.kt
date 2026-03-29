package com.hoabui.virtualbody3d.ui.theme.tokens.component

import androidx.compose.runtime.staticCompositionLocalOf
import com.hoabui.virtualbody3d.ui.theme.tokens.ElevationTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveRadiusTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveSpacingTokens

val LocalButtonTokens = staticCompositionLocalOf { gymButtonTokens(
    spacing = PrimitiveSpacingTokens.default(),
    radius = PrimitiveRadiusTokens.default()
) }

val LocalCardTokens = staticCompositionLocalOf { gymCardTokens(
    spacing = PrimitiveSpacingTokens.default(),
    radius = PrimitiveRadiusTokens.default(),
    elevation = ElevationTokens.default(),
) }

val LocalSurfaceTokens = staticCompositionLocalOf {
    gymSurfaceTokens(elevation = ElevationTokens.default())
}
