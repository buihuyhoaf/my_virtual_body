package com.hoabui.virtualbody3d.domain.model.calendar

import com.hoabui.virtualbody3d.domain.model.common.ImageSource
import com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseCategory
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkoutCalendarLineImageTest {

    @Test
    fun `local snapshot wins over url`() {
        val img = resolveWorkoutCalendarLineImage(
            exerciseLocalImageName = "foo_drawable",
            exerciseImageResUrl = "https://example.com/a.png",
            catalogExercise = null,
        )
        assertTrue(img is ImageSource.LocalResource)
        assertEquals("foo_drawable", (img as ImageSource.LocalResource).name)
    }

    @Test
    fun `network url when no local snapshot`() {
        val img = resolveWorkoutCalendarLineImage(
            exerciseLocalImageName = null,
            exerciseImageResUrl = "https://example.com/b.png",
            catalogExercise = null,
        )
        assertTrue(img is ImageSource.Network)
        assertEquals("https://example.com/b.png", (img as ImageSource.Network).url)
    }

    @Test
    fun `content uri when url starts with content`() {
        val img = resolveWorkoutCalendarLineImage(
            exerciseLocalImageName = null,
            exerciseImageResUrl = "content://media/1",
            catalogExercise = null,
        )
        assertTrue(img is ImageSource.ContentUri)
        assertEquals("content://media/1", (img as ImageSource.ContentUri).uriString)
    }

    @Test
    fun `falls back to catalog when snapshot empty`() {
        val catalog = sampleExercise(ImageSource.Network("https://catalog"))
        val img = resolveWorkoutCalendarLineImage(
            exerciseLocalImageName = null,
            exerciseImageResUrl = null,
            catalogExercise = catalog,
        )
        assertTrue(img is ImageSource.Network)
        assertEquals("https://catalog", (img as ImageSource.Network).url)
    }

    @Test
    fun `final fallback body_unsplash`() {
        val img = resolveWorkoutCalendarLineImage(
            exerciseLocalImageName = null,
            exerciseImageResUrl = null,
            catalogExercise = null,
        )
        assertTrue(img is ImageSource.LocalResource)
        assertEquals(WORKOUT_CALENDAR_FALLBACK_DRAWABLE_NAME, (img as ImageSource.LocalResource).name)
    }

    private fun sampleExercise(image: ImageSource) = Exercise(
        id = "1",
        name = "X",
        image = image,
        category = ExerciseCategory.Strength,
        bodyRegion = BodyRegion.Chest,
        description = "",
        equipment = null,
        safetyNotes = "",
        measurementMode = ExerciseMeasurementMode.Strength,
    )
}
