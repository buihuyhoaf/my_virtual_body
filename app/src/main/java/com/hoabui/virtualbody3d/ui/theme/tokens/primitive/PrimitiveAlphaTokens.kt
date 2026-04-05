package com.hoabui.virtualbody3d.ui.theme.tokens.primitive

/**
 * Alpha values for layered UI (gradients, soft icon tints). Single source for subtle 80% treatment.
 */
object PrimitiveAlphaTokens {
    const val DISABLED = 0.38f
    const val LOW = 0.2f
    const val MEDIUM = 0.5f
    const val SUBTLE_LAYER: Float = 0.8f
    /** Hero floating chips — glassy fill over [SemanticColorTokens.surfaceOverlay]. */
    const val HERO_CHIP_GLASS_FILL: Float = 0.7f
    /** Image card: soften secondary line / glass badge fill (non-library subtitle, holistic badge). */
    const val IMAGE_CARD_OVERLAY_MEDIUM: Float = 0.7f
    /** Dense overlay surface (e.g. legacy trailing panel on library tile previews). */
    const val IMAGE_CARD_TRAILING_OVERLAY_SURFACE: Float = 0.92f
    /** Sticky list headers (e.g. exercise library body region) over scrolling content. */
    const val STICKY_HEADER_SCRIM: Float = 0.9f
    /** Inner radial highlight on premium surfaces ([drawBehind], single pass). */
    const val SURFACE_INNER_RADIAL_DEPTH: Float = 0.09f
    /** Gradient rim on hero-tier surfaces — high stop along top edge. */
    const val SURFACE_GRADIENT_RIM_HIGH: Float = 0.42f
    /** Gradient rim — low stop toward bottom. */
    const val SURFACE_GRADIENT_RIM_LOW: Float = 0.06f
    const val THINKING_CARD_BACKGROUND = 0.96f
    const val TIMELINE_ROW_SURFACE = LOW
    const val TIMELINE_UNSELECTED_ITEM = MEDIUM
    const val TIMELINE_AVATAR_SELECTED_SCALE = 1.05f
    const val TIMELINE_PAGER_SNAP_POSITIONAL_THRESHOLD = 0.4f
    const val IMAGE_CARD_SELECTED_TINT = 0.08f
    /** Weaker in-cart (non-active) tint on exercise library tiles. */
    const val IMAGE_CARD_WEAK_SELECTION_TINT = 0.04f
}
