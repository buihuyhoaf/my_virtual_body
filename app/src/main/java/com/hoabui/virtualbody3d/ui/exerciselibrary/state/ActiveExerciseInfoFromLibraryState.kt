package com.hoabui.virtualbody3d.ui.exerciselibrary.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.util.CaloriesCalculator
import com.hoabui.virtualbody3d.ui.common_ui.organism.exercise.GExerciseCardUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.cart.ActiveExerciseInfo

private const val DEFAULT_BODY_WEIGHT_KG = 70.0

/**
 * Derives [ActiveExerciseInfo] for cart editing surfaces from the current library state.
 */
@Composable
fun rememberActiveExerciseInfoFromLibraryState(
    libraryState: ExerciseLibraryUiState,
    cartItems: List<GExerciseCardUiModel>,
): ActiveExerciseInfo? {
    val activeExerciseInfo by remember(
        libraryState.activeExerciseId,
        libraryState.itemDrafts,
        libraryState.libraryList.exerciseMeasurementById,
        cartItems,
    ) {
        derivedStateOf {
            libraryState.activeExerciseId?.let { id ->
                val draft = libraryState.itemDrafts[id]
                val measurementMode = libraryState.libraryList.exerciseMeasurementById[id]
                    ?: ExerciseMeasurementMode.Strength
                val estimatedCalories = draft?.let {
                    val totalDurationSeconds = it.setRows.sumOf { setRow ->
                        (setRow.minutes * 60) + setRow.seconds
                    }
                    val durationMinutes = totalDurationSeconds / 60.0
                    val totalReps = it.setRows.sumOf { setRow -> setRow.reps }
                    val loadValues = it.setRows.mapNotNull { setRow ->
                        setRow.weightKg.takeIf { value -> value > 0.0 }
                    }
                    val averageLoad = if (loadValues.isNotEmpty()) loadValues.average() else 0.0
                    CaloriesCalculator.estimateCalories(
                        exerciseId = id,
                        measurementMode = measurementMode,
                        durationMinutes = durationMinutes,
                        totalReps = totalReps,
                        averageLoadKg = averageLoad,
                        bodyWeightKg = DEFAULT_BODY_WEIGHT_KG,
                        leanBodyMassKg = null,
                    )
                } ?: 0f
                ActiveExerciseInfo(
                    id = id,
                    title = cartItems.firstOrNull { it.id == id }?.title,
                    draft = draft,
                    measurementMode = measurementMode,
                    estimatedCalories = estimatedCalories,
                )
            }
        }
    }
    return activeExerciseInfo
}

@Composable
fun rememberCartItemsFromLibraryState(libraryState: ExerciseLibraryUiState): List<GExerciseCardUiModel> {
    return remember(libraryState.libraryList.sections, libraryState.draftOrder) {
        val byId = libraryState.libraryList.sections.flatMap { it.items }.associateBy { it.id }
        libraryState.draftOrder.mapNotNull { byId[it] }
    }
}
