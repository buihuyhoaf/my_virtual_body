package com.hoabui.virtualbody3d.ui.theme.tokens.primitive

/**
 * Alpha values for layered UI (gradients, soft icon tints). Single source for subtle 80% treatment.
 */
object PrimitiveAlphaTokens {
    const val SUBTLE_LAYER: Float = 0.8f
    /** Hero floating chips — glassy fill over [SemanticColorTokens.surfaceOverlay]. */
    const val HERO_CHIP_GLASS_FILL: Float = 0.7f
    /** Sticky list headers (e.g. exercise library body region) over scrolling content. */
    const val STICKY_HEADER_SCRIM: Float = 0.9f
    /** Inner radial highlight on premium surfaces ([drawBehind], single pass). */
    const val SURFACE_INNER_RADIAL_DEPTH: Float = 0.09f
    /** Gradient rim on hero-tier surfaces — high stop along top edge. */
    const val SURFACE_GRADIENT_RIM_HIGH: Float = 0.42f
    /** Gradient rim — low stop toward bottom. */
    const val SURFACE_GRADIENT_RIM_LOW: Float = 0.06f
}
