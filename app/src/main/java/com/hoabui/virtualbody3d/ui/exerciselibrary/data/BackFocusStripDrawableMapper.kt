package com.hoabui.virtualbody3d.ui.exerciselibrary.data

import androidx.annotation.DrawableRes
import com.hoabui.virtualbody3d.R

/**
 * Maps [focusMuscles] to back strip art: only the three back catalog tokens participate.
 * [com.hoabui.virtualbody3d.data.local.db.seed.CatalogSeedData] back exercises use these tokens; other
 * muscles in the list are ignored for this resolver.
 */
object BackFocusStripDrawableMapper {

    private val backTokens: Set<String> = setOf(
        "infraspinatus",
        "latissimus_dorsi",
        "middle_trapezius",
    )

    /**
     * @return a [R.drawable] for the back-upper illustration, or null when none of the three tokens appear.
     */
    @DrawableRes
    fun drawableResForBackFocusMuscles(focusMuscles: List<String>): Int? {
        val selected: List<String> = focusMuscles
            .asSequence()
            .filter { it in backTokens }
            .distinct()
            .sorted()
            .toList()
        return when (selected.size) {
            0 -> null
            1 -> when (selected[0]) {
                "infraspinatus" -> R.drawable.back_infraspinatus
                "latissimus_dorsi" -> R.drawable.back_latissimus_dorsi
                "middle_trapezius" -> R.drawable.back_middle_trapezius
                else -> null
            }
            2 -> when {
                selected.containsAll(listOf("infraspinatus", "middle_trapezius")) ->
                    R.drawable.back_infraspinatus_middle_trapezius
                selected.containsAll(listOf("infraspinatus", "latissimus_dorsi")) ->
                    R.drawable.back_infraspinatus_latissimus_dorsi
                selected.containsAll(listOf("latissimus_dorsi", "middle_trapezius")) ->
                    R.drawable.back_latissimus_dorsi_middle_trapezius
                else -> null
            }
            3 -> if (selected.toSet() == backTokens) {
                R.drawable.back_full_back
            } else {
                null
            }
            else -> null
        }
    }
}
