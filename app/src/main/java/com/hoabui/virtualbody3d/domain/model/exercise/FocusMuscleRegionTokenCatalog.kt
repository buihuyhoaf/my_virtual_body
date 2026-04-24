package com.hoabui.virtualbody3d.domain.model.exercise

/**
 * Catalog tokens and “full” rules for focus-muscle strip art per [BodyRegion].
 * Uses the same normalized strings as [MuscleGroupUtils.normalizeFocusMuscleToken] (incl. pectoral fold).
 */
object FocusMuscleRegionTokenCatalog {

    const val UPPER_PECTORALIS: String = "upper_pectoralis"
    const val LOWER_PECTORALIS: String = "lower_pectoralis"

    /** Pectoral pair required for “full chest” strip art. */
    val chestFullRequires: Set<String> = setOf(UPPER_PECTORALIS, LOWER_PECTORALIS)

    const val UPPER_ABS: String = "upper_abs"
    const val LOWER_ABS: String = "lower_abs"

    /** Rectus “full” belly strip art when both upper/lower abs are present. */
    val bellyRectusFullRequires: Set<String> = setOf(UPPER_ABS, LOWER_ABS)

    val backCatalogTokens: Set<String> = setOf(
        "infraspinatus",
        "latissimus_dorsi",
        "middle_trapezius",
    )

    val chestCatalogTokens: Set<String> = setOf(UPPER_PECTORALIS, LOWER_PECTORALIS)

    const val OBLIQUES: String = "obliques"

    val bellyArtTokens: Set<String> = setOf(
        UPPER_ABS,
        LOWER_ABS,
        "transverse_abdominis",
        "serratus_anterior",
        OBLIQUES,
    )
}
