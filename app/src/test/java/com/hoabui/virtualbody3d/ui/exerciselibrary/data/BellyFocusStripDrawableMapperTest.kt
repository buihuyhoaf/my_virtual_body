package com.hoabui.virtualbody3d.ui.exerciselibrary.data

import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.model.exercise.FocusMuscleRegionTokenCatalog
import org.junit.Assert.assertEquals
import org.junit.Test

class BellyFocusStripDrawableMapperTest {

    @Test
    fun upperAndLowerAbs_fullBelly() {
        assertEquals(
            R.drawable.belly_full_belly,
            BellyFocusStripDrawableMapper.drawableResForBellyFocusMuscles(
                setOf(
                    FocusMuscleRegionTokenCatalog.UPPER_ABS,
                    FocusMuscleRegionTokenCatalog.LOWER_ABS,
                ),
            ),
        )
    }
}
