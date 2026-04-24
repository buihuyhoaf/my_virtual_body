package com.hoabui.virtualbody3d.domain.model.exercise

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MuscleDictionaryContractsTest {
    private val dictionary: MuscleDictionary = TestMuscleDictionary()

    @Test
    fun groupAndBodyLookup_areConsistentForMuscle() {
        assertEquals(RegionBody.Chest, dictionary.getBodyForMuscle(Muscle.UPPER_PECTORALIS))
        assertEquals(RegionGroup.UpperFront, dictionary.getGroupForMuscle(Muscle.UPPER_PECTORALIS))
    }

    @Test
    fun allMuscles_containsExpectedTokensForUpperBack() {
        val backMuscles = dictionary.allMuscles(RegionGroup.UpperBack, RegionBody.Back)
        assertTrue(backMuscles.contains(Muscle.LATISSIMUS_DORSI))
        assertTrue(backMuscles.contains(Muscle.MIDDLE_TRAPEZIUS))
    }
}
