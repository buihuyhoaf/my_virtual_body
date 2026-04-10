package com.hoabui.virtualbody3d.ui.exerciselibrary.presentation

import android.content.Context
import com.hoabui.virtualbody3d.domain.model.common.ImageSource
import com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseCategory
import com.hoabui.virtualbody3d.domain.model.exercise.EquipmentType
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.model.exercise.GymLocation
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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

/**
 * Semantic cache keys: stable across collection identity; booking key excludes search-only changes.
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
        val gymsA = persistentListOf(
            GymLocation(id = "g1", displayName = "One"),
            GymLocation(id = "g2", displayName = "Two"),
        )
        val gymsB = persistentListOf(
            GymLocation(id = "g1", displayName = "One"),
            GymLocation(id = "g2", displayName = "Two"),
        )
        val keyA = exerciseLibraryBookingPresentationKey(state, gymsA)
        val keyB = exerciseLibraryBookingPresentationKey(state, gymsB)
        assertEquals(keyA, keyB)
    }

    @Test
    fun bookingPresentationKey_searchOnlyChange_sameDedupeKey() {
        val dayMillis = LocalDateTime.of(2024, 8, 1, 9, 0)
            .atZone(zoneUtc)
            .toInstant()
            .toEpochMilli()
        val base = minimalBookingState(selectedDateMillis = dayMillis, searchQuery = "alpha")
        val withOtherSearch = minimalBookingState(selectedDateMillis = dayMillis, searchQuery = "beta")
        val gyms = persistentListOf(GymLocation(id = "gym1", displayName = "Gym"))
        val keyA = exerciseLibraryBookingPresentationKey(base, gyms)
        val keyB = exerciseLibraryBookingPresentationKey(withOtherSearch, gyms)
        assertEquals(keyA, keyB)
    }
}
