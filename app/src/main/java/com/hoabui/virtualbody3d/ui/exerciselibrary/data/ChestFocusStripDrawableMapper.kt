package com.hoabui.virtualbody3d.ui.exerciselibrary.data

import androidx.annotation.DrawableRes
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.model.exercise.FocusMuscleRegionTokenCatalog

/**
 * Maps merged [focusMuscles] to chest strip art: only the two chest catalog tokens participate.
 * In the library, [focusMuscles] is often the **merged list** of several cart exercises; we form
 * the set union of normalized strings, restricted to the two catalog chest tokens, so one exercise with
 * `upper_pectoralis` and another with `lower_pectoralis` together resolve to full-chest art.
 * Other muscle names in the list are ignored for this resolver.
 */
object ChestFocusStripDrawableMapper {

    private val chestTokens: Set<String> = FocusMuscleRegionTokenCatalog.chestCatalogTokens

    /**
     * @return a [R.drawable] for the front-upper (chest) illustration, or null when neither token appears.
     */
    @DrawableRes
    fun drawableResForChestFocusMuscles(focusMuscles: List<String>): Int? =
        drawableResForChestMusclesInternal(focusMuscles)

    @DrawableRes
    fun drawableResForChestFocusMuscles(focusMuscles: Set<String>): Int? =
        drawableResForChestMusclesInternal(focusMuscles.toList())

    @DrawableRes
    private fun drawableResForChestMusclesInternal(focusMuscles: List<String>): Int? {
        val selectedTokens: Set<String> = focusMuscles
            .asSequence()
            .filter { it in chestTokens }
            .toSet()
        return when {
            selectedTokens.isEmpty() -> null
            chestFullPectoralSetContainedIn(selectedTokens) -> R.drawable.chest_full_chest
            else -> when (selectedTokens.singleOrNull()) {
                FocusMuscleRegionTokenCatalog.UPPER_PECTORALIS -> R.drawable.chest_upper_pectoralis
                FocusMuscleRegionTokenCatalog.LOWER_PECTORALIS -> R.drawable.chest_lower_pectoralis
                else -> null
            }
        }
    }

    private fun chestFullPectoralSetContainedIn(selectedTokens: Set<String>): Boolean =
        FocusMuscleRegionTokenCatalog.chestFullRequires.all { it in selectedTokens }
}
