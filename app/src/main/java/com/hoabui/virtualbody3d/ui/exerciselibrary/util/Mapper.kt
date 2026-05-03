package com.hoabui.virtualbody3d.ui.exerciselibrary.util

import android.content.Context
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.model.calendar.caloriesToVisualLevel
import com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseLibraryCartSnapshot
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.model.exercise.LibraryCartDraft
import com.hoabui.virtualbody3d.domain.model.exercise.LibraryExerciseLineDraft
import com.hoabui.virtualbody3d.domain.model.exercise.PendingSessionBooking
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSchedule
import com.hoabui.virtualbody3d.domain.usecase.CommitLibrarySessionBookingResult
import com.hoabui.virtualbody3d.domain.util.CaloriesCalculator
import com.hoabui.virtualbody3d.ui.common_ui.organism.exercise.GExerciseCardUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.cart.AddExerciseSuccessSummary
import com.hoabui.virtualbody3d.ui.exerciselibrary.cart.ExerciseDraft
import com.hoabui.virtualbody3d.ui.exerciselibrary.cart.SetRowDraft
import com.hoabui.virtualbody3d.ui.exerciselibrary.catalog.ExerciseLibraryCatalogEntryUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.catalog.ExerciseLibraryCatalogGrouped
import com.hoabui.virtualbody3d.ui.exerciselibrary.catalog.toExerciseLibraryCardImage
import com.hoabui.virtualbody3d.ui.exerciselibrary.sessionbooking.SessionBookingInput
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryUiState
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.collections.immutable.toPersistentMap

// ── Catalog grouping (from ExerciseLibraryCatalogUiMapper) ───────────────────

fun mapGroupedToCatalogGrouped(grouped: Map<BodyRegion, List<Exercise>>): ExerciseLibraryCatalogGrouped =
    grouped
        .mapValues { (_, exercises) ->
            exercises.map { it.toExerciseLibraryCatalogEntryUiModel() }.toImmutableList()
        }
        .toPersistentMap()

private fun Exercise.toExerciseLibraryCatalogEntryUiModel(): ExerciseLibraryCatalogEntryUiModel =
    ExerciseLibraryCatalogEntryUiModel(
        id = id,
        name = name,
        category = category,
        equipment = equipment,
        bodyRegion = bodyRegion,
        focusMuscles = focusMuscles,
        measurementMode = measurementMode,
        image = image.toExerciseLibraryCardImage(),
    )

// ── Catalog search (from ExerciseLibraryCatalogSearch) ───────────────────────

/**
 * Same matching rules as [com.hoabui.virtualbody3d.domain.model.exercise.Exercise.matchesLibrarySearch],
 * using catalog entry fields only (no domain [Exercise]).
 */
fun ExerciseLibraryCatalogEntryUiModel.matchesLibrarySearch(normalizedQuery: String): Boolean {
    if (normalizedQuery.isEmpty()) return true
    if (name.lowercase().contains(normalizedQuery)) return true
    if (bodyRegion.name.lowercase().contains(normalizedQuery)) return true
    if (focusMuscles.any { it.wireKey.contains(normalizedQuery) }) return true
    val equipmentName = equipment?.name?.lowercase() ?: return false
    return equipmentName.contains(normalizedQuery)
}

// ── Card UI (from ExerciseLibraryCardMapping) ────────────────────────────────

fun ExerciseLibraryCatalogEntryUiModel.toGExerciseCardUiModel(
    context: Context,
    cartExerciseIds: Set<String>,
    activeExerciseId: String?,
): GExerciseCardUiModel {
    val inCart = id in cartExerciseIds
    val uptoKcal = CaloriesCalculator.estimateLibraryUptoKcal(id, measurementMode)
    return GExerciseCardUiModel(
        id = id,
        image = image,
        title = name,
        subtitle = context.getString(
            R.string.exercise_library_card_upto_kcal,
            uptoKcal,
        ),
        libraryUptoKcal = uptoKcal,
        subtitleCaloriesVisualLevel = caloriesToVisualLevel(uptoKcal.toFloat()),
        badgeText = null,
        isSelected = inCart && id == activeExerciseId,
        isInCartInactive = inCart && id != activeExerciseId,
    )
}

// ── Session booking success (from CommitLibrarySessionBookingSuccessUiMapper) ─

/**
 * Maps domain booking commit success to chrome [AddExerciseSuccessSummary] (reducer stays free of mapping logic).
 */
fun CommitLibrarySessionBookingResult.Success.toAddExerciseSuccessSummary(): AddExerciseSuccessSummary =
    AddExerciseSuccessSummary(
        sessionStartInstant = session.startInstant,
        sessionEndInstant = session.endInstant,
        scheduledDateMillis = scheduledDateMillis,
        exerciseCount = scheduledCount,
        primaryExerciseTitle = primaryExerciseTitle,
        locationDisplayName = locationDisplayName,
    )

// ── UI state / cart (from ExerciseLibraryStateMapping) ───────────────────────

fun ExerciseLibraryUiState.toCartSnapshot(): ExerciseLibraryCartSnapshot =
    ExerciseLibraryCartSnapshot(
        itemDrafts = itemDrafts.mapValues { (id, draft) ->
            val mode = libraryList.exerciseMeasurementById[id] ?: ExerciseMeasurementMode.Strength
            val row = draft.setRows.firstOrNull() ?: SetRowDraft()
            when (mode) {
                ExerciseMeasurementMode.Strength -> LibraryExerciseLineDraft(
                    sets = draft.setRows.size.toString(),
                    reps = (draft.setRows.firstOrNull()?.reps ?: 0).toString(),
                )
                ExerciseMeasurementMode.Duration -> LibraryExerciseLineDraft(
                    sets = row.minutes.toString(),
                    reps = row.seconds.toString(),
                )
            }
        },
        draftOrder = draftOrder.toList(),
        activeExerciseId = activeExerciseId,
    )

fun ExerciseLibraryUiState.withCartSnapshot(snapshot: ExerciseLibraryCartSnapshot): ExerciseLibraryUiState =
    copy(
        itemDrafts = snapshot.itemDrafts
            .mapValues { (id, lineDraft) ->
                itemDrafts[id] ?: defaultDraftFromLineDraft(lineDraft)
            }
            .toImmutableMap(),
        draftOrder = snapshot.draftOrder.toImmutableList(),
        activeExerciseId = snapshot.activeExerciseId,
    )

fun ExerciseLibraryUiState.toLibraryCartDraft(): LibraryCartDraft =
    LibraryCartDraft(
        draftOrder = draftOrder,
        itemDrafts = itemDrafts.mapValues { (id, d) ->
            val mode = libraryList.exerciseMeasurementById[id] ?: ExerciseMeasurementMode.Strength
            val row = d.setRows.firstOrNull() ?: SetRowDraft()
            when (mode) {
                ExerciseMeasurementMode.Strength -> LibraryExerciseLineDraft(
                    sets = d.setRows.size.toString(),
                    reps = (d.setRows.firstOrNull()?.reps ?: 0).toString(),
                )
                ExerciseMeasurementMode.Duration -> LibraryExerciseLineDraft(
                    sets = row.minutes.toString(),
                    reps = row.seconds.toString(),
                )
            }
        },
    )

fun SessionBookingInput.toPendingSessionBooking(): PendingSessionBooking =
    PendingSessionBooking(
        selectedDateMillis = selectedDateMillis,
        selectedLocationId = selectedLocationId,
        selectedSlotStarts = selectedSlotStarts.toList(),
        longSessionAcknowledged = longSessionAcknowledged,
        isConfirming = isConfirming,
    )

private fun defaultDraftFromLineDraft(lineDraft: LibraryExerciseLineDraft): ExerciseDraft {
    val reps = lineDraft.reps.trim().toIntOrNull()?.coerceAtLeast(0) ?: 10
    val minutes = lineDraft.sets.trim().toIntOrNull()?.coerceAtLeast(0) ?: 0
    val seconds = lineDraft.reps.trim().toIntOrNull()?.coerceAtLeast(0) ?: 30
    return ExerciseDraft(
        setRows = listOf(
            SetRowDraft(
                reps = reps,
                weightKg = 0.0,
                minutes = minutes,
                seconds = seconds,
            ),
        ).toImmutableList(),
    )
}

// ── Selection bar / schedule (from WorkoutScheduleSelectionBarMapping) ───────

/**
 * Builds a cart [ExerciseDraft] from a persisted schedule row so the selection bar matches Room
 * before the user edits (aligned with [com.hoabui.virtualbody3d.ui.exerciselibrary.viewmodel.ExerciseLibraryViewModel.onConfirmSelectionBarEdit] mapping).
 */
fun WorkoutSchedule.toExerciseDraftForSelectionBarEdit(): ExerciseDraft =
    when (measurementMode) {
        ExerciseMeasurementMode.Strength -> {
            val setCount = sets.coerceAtLeast(1)
            ExerciseDraft(
                setRows = List(setCount) {
                    SetRowDraft(
                        reps = reps.coerceAtLeast(0),
                        weightKg = weightKg.coerceAtLeast(0.0),
                        minutes = 0,
                        seconds = 0,
                    )
                }.toImmutableList(),
            )
        }
        ExerciseMeasurementMode.Duration -> {
            val total = durationSeconds?.coerceAtLeast(0) ?: 0
            val minutes = total / 60
            val seconds = total % 60
            ExerciseDraft(
                setRows = listOf(
                    SetRowDraft(
                        reps = 0,
                        weightKg = 0.0,
                        minutes = minutes,
                        seconds = seconds,
                    ),
                ).toImmutableList(),
            )
        }
    }
