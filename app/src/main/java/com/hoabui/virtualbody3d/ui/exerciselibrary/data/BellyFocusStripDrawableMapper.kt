package com.hoabui.virtualbody3d.ui.exerciselibrary.data

import androidx.annotation.DrawableRes
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.model.exercise.FocusMuscleRegionTokenCatalog

/**
 * Belly / anterior-core strip art for cart region focus sets (DB seed: [FocusMuscleRegionTokenCatalog.bellyArtTokens]).
 */
object BellyFocusStripDrawableMapper {

    @DrawableRes
    fun drawableResForBellyFocusMuscles(focusMuscles: List<String>): Int? =
        drawableResForBellyFocusMuscles(focusMuscles.toSet())

    @DrawableRes
    fun drawableResForBellyFocusMuscles(focusMuscles: Set<String>): Int? {
        val selected: Set<String> = focusMuscles
            .asSequence()
            .filter { it in FocusMuscleRegionTokenCatalog.bellyArtTokens }
            .toSet()
        if (selected.isEmpty()) return null
        if (FocusMuscleRegionTokenCatalog.bellyRectusFullRequires.all { it in selected }) {
            return R.drawable.belly_full_belly
        }
        if (FocusMuscleRegionTokenCatalog.LOWER_ABS in selected &&
            FocusMuscleRegionTokenCatalog.OBLIQUES in selected
        ) {
            return R.drawable.belly_external_obliques
        }
        if (FocusMuscleRegionTokenCatalog.OBLIQUES in selected) {
            return R.drawable.belly_external_obliques
        }
        return R.drawable.belly_rectus_abdominis
    }
}
