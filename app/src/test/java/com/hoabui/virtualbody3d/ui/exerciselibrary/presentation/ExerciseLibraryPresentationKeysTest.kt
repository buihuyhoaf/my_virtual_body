package com.hoabui.virtualbody3d.ui.exerciselibrary.presentation

import android.content.Context
import com.hoabui.virtualbody3d.domain.model.common.ImageSource
import com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseCategory
import com.hoabui.virtualbody3d.domain.model.exercise.EquipmentType
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.model.exercise.GymLocation
import com.hoabui.virtualbody3d.domain.model.exercise.InstantInterval
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSchedule
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.ExerciseLibraryCatalogUiMapper
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.LibraryFilterState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.SessionBookingInput
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.SessionBookingSheetState
import java.time.LocalDateTime
import java.time.ZoneId
import io.mockk.mockk
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

/**
 * Semantic cache keys: stable across collection identity; booking schedule slice matches density use case.
 */
class ExerciseLibraryPresentationKeysTest {

    private val zoneUtc: ZoneId = ZoneId.of("UTC")

    private val catalogMapper = ExerciseLibraryCatalogUiMapper(mockk<Context>(relaxed = true))

    private fun catalogState(grouped: Map<BodyRegion, List<Exercise>>) =
        catalogMapper.mapGroupedToCatalogState(grouped)

    private fun sampleExercise(id: String = "ex1") = Exercise(
        id = id,
        name = "Sample",
        image = ImageSource.LocalResource("placeholder"),
        category = ExerciseCategory.Strength,
        bodyRegion = BodyRegion.Chest,
        description = "",
        equipment = EquipmentType.Barbell,
        safetyNotes = "",
        measurementMode = ExerciseMeasurementMode.Strength,
    )

    private fun minimalBookingState(
        selectedDateMillis: Long,
        locationId: String = "gym1",
        searchQuery: String = "",
    ): ExerciseLibraryUiState {
        val input = SessionBookingInput(
            selectedDateMillis = selectedDateMillis,
            selectedLocationId = locationId,
            selectedSlotStarts = persistentSetOf(),
            bookingExerciseSnapshot = persistentListOf(),
            longSessionAcknowledged = false,
            pendingLongSessionWarning = false,
            isConfirming = false,
            showSlotConflict = false,
        )
        return ExerciseLibraryUiState(
            filters = LibraryFilterState(searchQuery = searchQuery),
            sessionBooking = SessionBookingSheetState(input = input),
        )
    }

    private fun scheduleForLocal(
        id: String,
        localDateTime: LocalDateTime,
        locationId: String,
    ): WorkoutSchedule = WorkoutSchedule(
        id = id,
        exerciseId = "ex1",
        scheduledAt = localDateTime,
        sets = 3,
        reps = 10,
        weightKg = 0.0,
        restSeconds = 60,
        notes = null,
        locationId = locationId,
    )

    @Test
    fun sectionRebuildKey_sameCatalogIds_differentMapAndListInstances_equal() {
        val ex = sampleExercise()
        val groupedA = mapOf(BodyRegion.Chest to listOf(ex))
        val groupedB = mapOf(BodyRegion.Chest to listOf(ex.copy(name = "Renamed")))
        val filters = ExerciseLibraryUiState()
        val k1 = exerciseLibrarySectionRebuildKey(catalogState(groupedA), filters)
        val k2 = exerciseLibrarySectionRebuildKey(catalogState(groupedB), filters)
        assertEquals(k1.catalogContentSignature, k2.catalogContentSignature)
        assertEquals(k1, k2)
    }

    @Test
    fun sectionRebuildKey_differentExerciseId_notEqual() {
        val filters = ExerciseLibraryUiState()
        val k1 = exerciseLibrarySectionRebuildKey(
            catalogState(mapOf(BodyRegion.Chest to listOf(sampleExercise("a")))),
            filters,
        )
        val k2 = exerciseLibrarySectionRebuildKey(
            catalogState(mapOf(BodyRegion.Chest to listOf(sampleExercise("b")))),
            filters,
        )
        assertNotEquals(k1, k2)
    }

    @Test
    fun bookingPresentationKey_sameGymsDifferentImmutableListInstances_equal() {
        val dayMillis = LocalDateTime.of(2024, 6, 10, 12, 0)
            .atZone(zoneUtc)
            .toInstant()
            .toEpochMilli()
        val state = minimalBookingState(selectedDateMillis = dayMillis)
        val busy = persistentListOf<InstantInterval>()
        val schedules = persistentListOf<WorkoutSchedule>()
        val gymsA = persistentListOf(
            GymLocation(id = "g1", displayName = "One"),
            GymLocation(id = "g2", displayName = "Two"),
        )
        val gymsB = persistentListOf(
            GymLocation(id = "g1", displayName = "One"),
            GymLocation(id = "g2", displayName = "Two"),
        )
        val keyA = exerciseLibraryBookingPresentationKey(state, busy, schedules, gymsA, zoneUtc)
        val keyB = exerciseLibraryBookingPresentationKey(state, busy, schedules, gymsB, zoneUtc)
        assertEquals(keyA, keyB)
    }

    @Test
    fun bookingPresentationKey_extraScheduleOnOtherDay_doesNotChangeKey() {
        val selectedLocal = LocalDateTime.of(2024, 7, 1, 8, 0)
        val dayMillis = selectedLocal.atZone(zoneUtc).toInstant().toEpochMilli()
        val loc = "facility"
        val state = minimalBookingState(selectedDateMillis = dayMillis, locationId = loc)
        val busy = persistentListOf<InstantInterval>()
        val gyms = persistentListOf(GymLocation(id = loc, displayName = "Gym"))

        val onSelectedDay = scheduleForLocal(
            id = "s-day",
            localDateTime = LocalDateTime.of(2024, 7, 1, 10, 0),
            locationId = loc,
        )
        val onOtherDay = scheduleForLocal(
            id = "s-other",
            localDateTime = LocalDateTime.of(2024, 7, 2, 10, 0),
            locationId = loc,
        )

        val keyOnlySelected = exerciseLibraryBookingPresentationKey(
            state,
            busy,
            listOf(onSelectedDay).toImmutableList(),
            gyms,
            zoneUtc,
        )
        val keyWithNoise = exerciseLibraryBookingPresentationKey(
            state,
            busy,
            listOf(onSelectedDay, onOtherDay).toImmutableList(),
            gyms,
            zoneUtc,
        )
        assertEquals(keyOnlySelected, keyWithNoise)
    }

    @Test
    fun bookingPresentationKey_searchOnlyChange_sameDedupeKey() {
        val dayMillis = LocalDateTime.of(2024, 8, 1, 9, 0)
            .atZone(zoneUtc)
            .toInstant()
            .toEpochMilli()
        val base = minimalBookingState(selectedDateMillis = dayMillis, searchQuery = "alpha")
        val withOtherSearch = minimalBookingState(selectedDateMillis = dayMillis, searchQuery = "beta")
        val busy = persistentListOf<InstantInterval>()
        val gyms = persistentListOf(GymLocation(id = "gym1", displayName = "Gym"))
        val keyA = exerciseLibraryBookingPresentationKey(base, busy, persistentListOf(), gyms, zoneUtc)
        val keyB = exerciseLibraryBookingPresentationKey(withOtherSearch, busy, persistentListOf(), gyms, zoneUtc)
        assertEquals(keyA, keyB)
    }
}
