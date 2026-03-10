package com.hoabui.virtualbody3d.ui.body.state

import com.hoabui.virtualbody3d.R

/**
 * Stable enum for body region selection in the Home screen.
 * Used by BodyRegionRow for region-specific analysis (detail screen not yet implemented).
 */
enum class BodyRegion(val displayNameRes: Int) {
    UpperBody(R.string.body_region_upper_body),
    Core(R.string.body_region_core),
    Glutes(R.string.body_region_glutes),
    Thighs(R.string.body_region_thighs),
    Arms(R.string.body_region_arms)
}
