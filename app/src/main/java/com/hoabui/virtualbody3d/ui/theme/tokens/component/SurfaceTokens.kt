package com.hoabui.virtualbody3d.ui.theme.tokens.component

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import com.hoabui.virtualbody3d.ui.theme.tokens.ElevationTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveAlphaTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.semantic.gymSurfaceEffectSemantics

/**
 * Med-Tech premium surface styling: defaults for borders, inner depth, and hero-only elevation.
 * Consumed by surface and card atoms.
 */
enum class GSurfaceTreatment {
    /** Legacy flat — no default border, no inner radial, no gradient rim. */
    Flat,
    /** Weighted default — subtle border, inner radial depth. */
    Standard,
    /** Focal / hero — standard plus gradient rim; prefer with [heroShadowElevation]. */
    Hero,
}

@Immutable
data class SurfaceTokens(
    /** When true, atoms apply hairline [borderSubtle] if [border] param is null. */
    val applyDefaultSubtleBorder: Boolean,
    val innerRadialDepthAlpha: Float,
    /** Radial gradient radius as a fraction of container height. */
    val innerRadialRadiusFraction: Float,
    /** Radial center Y as fraction of height (−1…1), negative shifts toward top. */
    val innerRadialCenterYFraction: Float,
    val gradientRimAlphaHigh: Float,
    val gradientRimAlphaLow: Float,
    val heroShadowElevation: Dp,
)

fun gymSurfaceTokens(
    elevation: ElevationTokens,
): SurfaceTokens {
    val effect = gymSurfaceEffectSemantics()
    return SurfaceTokens(
        applyDefaultSubtleBorder = true,
        innerRadialDepthAlpha = PrimitiveAlphaTokens.SURFACE_INNER_RADIAL_DEPTH,
        innerRadialRadiusFraction = effect.innerRadialRadiusFraction,
        innerRadialCenterYFraction = effect.innerRadialCenterYFraction,
        gradientRimAlphaHigh = PrimitiveAlphaTokens.SURFACE_GRADIENT_RIM_HIGH,
        gradientRimAlphaLow = PrimitiveAlphaTokens.SURFACE_GRADIENT_RIM_LOW,
        heroShadowElevation = elevation.level3,
    )
}
