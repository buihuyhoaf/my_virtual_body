package com.hoabui.virtualbody3d.ui.exerciselibrary.data

import com.hoabui.virtualbody3d.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ChestFocusStripDrawableMapperTest {

    @Test
    fun noChestTokens_returnsNull() {
        assertNull(ChestFocusStripDrawableMapper.drawableResForChestFocusMuscles(listOf("latissimus_dorsi")))
    }

    @Test
    fun single_upper_pectoralis() {
        assertEquals(
            R.drawable.chest_upper_pectoralis,
            ChestFocusStripDrawableMapper.drawableResForChestFocusMuscles(listOf("upper_pectoralis")),
        )
    }

    @Test
    fun single_lower_pectoralis() {
        assertEquals(
            R.drawable.chest_lower_pectoralis,
            ChestFocusStripDrawableMapper.drawableResForChestFocusMuscles(listOf("lower_pectoralis")),
        )
    }

    @Test
    fun both_fullChest() {
        assertEquals(
            R.drawable.chest_full_chest,
            ChestFocusStripDrawableMapper.drawableResForChestFocusMuscles(
                listOf("upper_pectoralis", "lower_pectoralis"),
            ),
        )
    }

    /** Strict pipeline: compound token entries are invalid and must not resolve. */
    @Test
    fun compoundOneListEntry_bothPectoral_returnsNull() {
        assertNull(
            ChestFocusStripDrawableMapper.drawableResForChestFocusMuscles(
                listOf("upper_pectoralis, lower_pectoralis"),
            ),
        )
    }

    /** Cart merge: exercise A (upper only) + exercise B (lower only) → set union = full chest. */
    @Test
    fun mergedFromTwoExercises_oneUpperOneLower_resolvesToFullChest() {
        val mergedAsFromCart = listOf("upper_pectoralis", "lower_pectoralis")
        assertEquals(
            R.drawable.chest_full_chest,
            ChestFocusStripDrawableMapper.drawableResForChestFocusMuscles(mergedAsFromCart),
        )
    }

    @Test
    fun mergedFromTwoExercises_withUnrelatedMusclesBetween_stillFullChest() {
        assertEquals(
            R.drawable.chest_full_chest,
            ChestFocusStripDrawableMapper.drawableResForChestFocusMuscles(
                listOf("upper_pectoralis", "latissimus_dorsi", "lower_pectoralis"),
            ),
        )
    }

    @Test
    fun misspelledUpperPectori_alisWithCorrectLower_returnsLowerOnly() {
        assertEquals(
            R.drawable.chest_lower_pectoralis,
            ChestFocusStripDrawableMapper.drawableResForChestFocusMuscles(
                listOf("upper_pectorialis", "lower_pectoralis"),
            ),
        )
    }

    @Test
    fun bothTokensMisspelledPectori_alis_returnsNull() {
        assertNull(
            ChestFocusStripDrawableMapper.drawableResForChestFocusMuscles(
                listOf("upper_pectorialis", "lower_pectorialis"),
            ),
        )
    }

    @Test
    fun ignoresNonChestMuscles() {
        assertEquals(
            R.drawable.chest_upper_pectoralis,
            ChestFocusStripDrawableMapper.drawableResForChestFocusMuscles(
                listOf("latissimus_dorsi", "upper_pectoralis"),
            ),
        )
    }
}
