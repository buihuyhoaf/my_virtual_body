package com.hoabui.virtualbody3d.ui.workoutcalendar.mapper

import com.hoabui.virtualbody3d.domain.util.CaloriesCalculator
import com.hoabui.virtualbody3d.ui.common_ui.organism.exercise.GExerciseCardUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.cart.ActiveExerciseInfo
import com.hoabui.virtualbody3d.ui.exerciselibrary.cart.SelectionBarExerciseMeasurementKind
import com.hoabui.virtualbody3d.ui.exerciselibrary.cart.toDomainMeasurementMode
import com.hoabui.virtualbody3d.ui.exerciselibrary.cart.toSelectionBarExerciseMeasurementKind
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

private const val DEFAULT_BODY_WEIGHT_KG = 70.0

class EditExerciseBarUiMapper @Inject constructor() {
    fun draftThumbnailCards(state: ExerciseLibraryUiState): ImmutableList<GExerciseCardUiModel> {
        val byId =
            state.libraryList.sections.asSequence()
                .flatMap { section -> section.items.asSequence() }
                .associateBy { it.id }
        return state.draftOrder.mapNotNull { id -> byId[id] }.toImmutableList()
    }

    fun emptyDraftThumbnails(): ImmutableList<GExerciseCardUiModel> = persistentListOf()

    fun activeExerciseForEditBar(
        state: ExerciseLibraryUiState,
        cartItems: List<GExerciseCardUiModel>,
        measurementFallbackKind: SelectionBarExerciseMeasurementKind,
    ): ActiveExerciseInfo? {
        val id = state.activeExerciseId ?: return null
        val draft = state.itemDrafts[id] ?: return null
        val domainMode =
            state.libraryList.exerciseMeasurementById[id]
                ?: measurementFallbackKind.toDomainMeasurementMode()
        val measurementKind = domainMode.toSelectionBarExerciseMeasurementKind()
        val estimatedCalories =
            draft.let {
                val totalDurationSeconds = it.setRows.sumOf { row -> row.minutes * 60 + row.seconds }
                val durationMinutes = totalDurationSeconds / 60.0
                val totalReps = it.setRows.sumOf { row -> row.reps }
                val loadValues = it.setRows.mapNotNull { row -> row.weightKg.takeIf { v -> v > 0.0 } }
                val averageLoad = if (loadValues.isNotEmpty()) loadValues.average() else 0.0
                CaloriesCalculator.estimateCalories(
                    exerciseId = id,
                    measurementMode = domainMode,
                    durationMinutes = durationMinutes,
                    totalReps = totalReps,
                    averageLoadKg = averageLoad,
                    bodyWeightKg = DEFAULT_BODY_WEIGHT_KG,
                    leanBodyMassKg = null,
                )
            }
        return ActiveExerciseInfo(
            id = id,
            title = cartItems.firstOrNull { it.id == id }?.title,
            draft = draft,
            measurementKind = measurementKind,
            estimatedCalories = estimatedCalories,
        )
    }
}
