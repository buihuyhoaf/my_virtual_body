package com.hoabui.virtualbody3d.ui.exerciselibrary.data

import com.hoabui.virtualbody3d.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BackFocusStripDrawableMapperTest {

    @Test
    fun noBackTokens_returnsNull() {
        assertNull(BackFocusStripDrawableMapper.drawableResForBackFocusMuscles(listOf("Pectoralis Major")))
    }

    @Test
    fun single_infraspinatus() {
        assertEquals(
            R.drawable.back_infraspinatus,
            BackFocusStripDrawableMapper.drawableResForBackFocusMuscles(listOf("infraspinatus")),
        )
    }

    @Test
    fun single_latissimus_dorsi() {
        assertEquals(
            R.drawable.back_latissimus_dorsi,
            BackFocusStripDrawableMapper.drawableResForBackFocusMuscles(listOf("latissimus_dorsi")),
        )
    }

    @Test
    fun single_middle_trapezius() {
        assertEquals(
            R.drawable.back_middle_trapezius,
            BackFocusStripDrawableMapper.drawableResForBackFocusMuscles(listOf("middle_trapezius")),
        )
    }

    @Test
    fun pair_infraspinatus_middleTrapezius() {
        assertEquals(
            R.drawable.back_infraspinatus_middle_trapezius,
            BackFocusStripDrawableMapper.drawableResForBackFocusMuscles(
                listOf("infraspinatus", "middle_trapezius"),
            ),
        )
    }

    @Test
    fun pair_infraspinatus_latissimusDorsi() {
        assertEquals(
            R.drawable.back_infraspinatus_latissimus_dorsi,
            BackFocusStripDrawableMapper.drawableResForBackFocusMuscles(
                listOf("latissimus_dorsi", "infraspinatus"),
            ),
        )
    }

    @Test
    fun pair_latissimusDorsi_middleTrapezius() {
        assertEquals(
            R.drawable.back_latissimus_dorsi_middle_trapezius,
            BackFocusStripDrawableMapper.drawableResForBackFocusMuscles(
                listOf("latissimus_dorsi", "middle_trapezius"),
            ),
        )
    }

    @Test
    fun allThree_fullBack() {
        assertEquals(
            R.drawable.back_full_back,
            BackFocusStripDrawableMapper.drawableResForBackFocusMuscles(
                listOf("infraspinatus", "middle_trapezius", "latissimus_dorsi"),
            ),
        )
    }

    @Test
    fun ignoresNonBackMuscles() {
        assertEquals(
            R.drawable.back_latissimus_dorsi,
            BackFocusStripDrawableMapper.drawableResForBackFocusMuscles(
                listOf("Pectoralis Major", "latissimus_dorsi"),
            ),
        )
    }
}
