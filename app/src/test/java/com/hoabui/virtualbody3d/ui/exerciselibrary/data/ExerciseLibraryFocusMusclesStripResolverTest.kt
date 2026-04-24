package com.hoabui.virtualbody3d.ui.exerciselibrary.data

import com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseCategory
import com.hoabui.virtualbody3d.domain.model.common.ImageSource
import com.hoabui.virtualbody3d.domain.model.exercise.Muscle
import com.hoabui.virtualbody3d.domain.model.exercise.RegionBody
import com.hoabui.virtualbody3d.domain.model.exercise.RegionGroup
import com.hoabui.virtualbody3d.domain.model.exercise.TestMuscleDictionary
import org.junit.Assert.assertEquals
import org.junit.Test

class ExerciseLibraryFocusMusclesStripResolverTest {
    private val dictionary = TestMuscleDictionary()

    @Test
    fun chestAndBellyInCart_frontUpperShowsChestArtOnly() {
        val names = focusMusclesStripImageNamesForSelectionMap(
            selectionMap = mapOf(
                RegionGroup.UpperFront to mapOf(
                    RegionBody.Chest to setOf(Muscle.UPPER_PECTORALIS, Muscle.LOWER_PECTORALIS),
                    RegionBody.Belly to setOf(Muscle.UPPER_ABS, Muscle.LOWER_ABS),
                ),
            ),
            muscleDictionary = dictionary,
        )
        assertEquals("chest_full_chest", names[0])
    }

    @Test
    fun cartAndClickSelectionMerge_clickCompletesChestBody_usesFullChestImage() {
        val chestFromCart = ex(
            id = "c",
            region = BodyRegion.Chest,
            focus = listOf(Muscle.UPPER_PECTORALIS),
        )
        val names = focusMusclesStripImageNamesForCartExercises(
            cartExerciseIds = listOf("c"),
            exercisesById = mapOf("c" to chestFromCart),
            muscleDictionary = dictionary,
            clickSelectionMap = mapOf(
                RegionGroup.UpperFront to mapOf(
                    RegionBody.Chest to setOf(Muscle.MIDDLE_PECTORALIS, Muscle.LOWER_PECTORALIS),
                ),
            ),
        )
        assertEquals("chest_full_chest", names[0])
    }

    @Test
    fun selectionMap_whenUpperBackGroupFullySelected_usesBackFullBack() {
        val names = focusMusclesStripImageNamesForSelectionMap(
            selectionMap = mapOf(
                RegionGroup.UpperBack to mapOf(
                    RegionBody.Back to setOf(
                        Muscle.LATISSIMUS_DORSI,
                        Muscle.MIDDLE_TRAPEZIUS,
                        Muscle.UPPER_TRAPEZIUS,
                        Muscle.INFRASPINATUS,
                        Muscle.ERECTOR_SPINAE,
                    ),
                    RegionBody.ShouldersBack to setOf(Muscle.POSTERIOR_DELTOID),
                    RegionBody.ArmsBack to setOf(Muscle.TRICEPS_BRACHII),
                ),
            ),
            muscleDictionary = dictionary,
        )
        assertEquals("back_full_back", names[1])
    }

    private fun ex(
        id: String,
        region: BodyRegion,
        focus: List<Muscle>,
    ) = Exercise(
        id = id,
        name = id,
        image = ImageSource.LocalResource("t"),
        category = ExerciseCategory.Strength,
        bodyRegion = region,
        focusMuscles = focus,
        description = "",
        equipment = null,
        safetyNotes = "",
    )
}
