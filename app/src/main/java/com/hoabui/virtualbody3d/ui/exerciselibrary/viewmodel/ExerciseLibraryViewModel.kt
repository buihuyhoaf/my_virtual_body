package com.hoabui.virtualbody3d.ui.exerciselibrary.viewmodel

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.hoabui.virtualbody3d.core.base.UiStateViewModel
import com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion
import com.hoabui.virtualbody3d.domain.model.exercise.EquipmentType
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseCategory
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSchedule
import com.hoabui.virtualbody3d.domain.model.exercise.matchesLibrarySearch
import com.hoabui.virtualbody3d.domain.model.exercise.normalizeExerciseLibraryQuery
import com.hoabui.virtualbody3d.domain.usecase.AddWorkoutUseCase
import com.hoabui.virtualbody3d.domain.usecase.GetExerciseLibraryUseCase
import com.hoabui.virtualbody3d.ui.exerciselibrary.ExerciseLibraryQuickChip
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.toLibraryCardUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseSectionUiItem
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.defaultExerciseLibraryCartDateMillis
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.update
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ExerciseLibraryViewModel @Inject constructor(
    private val getExerciseLibraryUseCase: GetExerciseLibraryUseCase,
    private val addWorkoutUseCase: AddWorkoutUseCase,
    @ApplicationContext private val appContext: Context,
) : UiStateViewModel<ExerciseLibraryUiState, Unit>() {

    private val groupedExercises = MutableStateFlow<Map<BodyRegion, List<Exercise>>>(emptyMap())
    private val filterState = MutableStateFlow(ExerciseLibraryUiState())
    private val detailExerciseId = MutableStateFlow<String?>(null)

    init {
        getExerciseLibraryUseCase()
            .onEach { groupedExercises.value = it }
            .catch { setError(it.message ?: "Unknown error") }
            .launchIn(viewModelScope)

        combine(groupedExercises, filterState, detailExerciseId) { grouped, filters, selectedId ->
            val sections = buildSections(grouped, filters)
            val selectedExercise = selectedId?.let { id ->
                grouped.values.flatten().find { it.id == id }
            }
            filters.copy(
                sections = sections,
                selectedExerciseForDetail = selectedExercise
            )
        }.onEach { fullState ->
            setSuccess(fullState)
        }.launchIn(viewModelScope)
    }

    fun updateSearchQuery(query: String) {
        filterState.update { it.copy(searchQuery = query) }
    }

    fun selectQuickChip(chip: ExerciseLibraryQuickChip?) {
        filterState.update { state ->
            when (chip) {
                null -> state.copy(selectedExerciseCategory = null, selectedEquipment = null)
                ExerciseLibraryQuickChip.Strength -> state.copy(
                    selectedExerciseCategory = ExerciseCategory.Strength,
                    selectedEquipment = null,
                )
                ExerciseLibraryQuickChip.Mobility -> state.copy(
                    selectedExerciseCategory = ExerciseCategory.Mobility,
                    selectedEquipment = null,
                )
                ExerciseLibraryQuickChip.Cardio -> state.copy(
                    selectedExerciseCategory = ExerciseCategory.Cardio,
                    selectedEquipment = null,
                )
                ExerciseLibraryQuickChip.Bodyweight -> state.copy(
                    selectedExerciseCategory = null,
                    selectedEquipment = EquipmentType.Bodyweight,
                )
            }
        }
    }

    /** Single-select toggle for instant-add flow. */
    fun onQuickAddToWorkout(exerciseId: String) {
        filterState.update { state ->
            if (state.selectedExerciseId == exerciseId) {
                state.copy(selectedExerciseId = null)
            } else {
                state.copy(
                    selectedExerciseId = exerciseId,
                    globalSets = DEFAULT_SETS,
                    globalReps = DEFAULT_REPS,
                    selectedDate = defaultExerciseLibraryCartDateMillis(),
                )
            }
        }
    }

    fun updateGlobalDraft(reps: Int, sets: Int, date: Long) {
        filterState.update { state ->
            state.copy(
                globalReps = reps.coerceAtLeast(1),
                globalSets = sets.coerceAtLeast(1),
                selectedDate = date,
            )
        }
    }

    fun confirmSingleToWorkout() {
        launchSafely {
            val filters = filterState.value
            val selectedId = filters.selectedExerciseId ?: return@launchSafely
            val exercisesById = groupedExercises.value.values.flatten().associateBy { it.id }
            val zone = ZoneId.systemDefault()
            val date = Instant.ofEpochMilli(filters.selectedDate).atZone(zone).toLocalDate()
            val scheduledAt = LocalDateTime.of(date, LocalTime.now())
            val ex = exercisesById[selectedId] ?: return@launchSafely
            addWorkoutUseCase(
                WorkoutSchedule(
                    id = UUID.randomUUID().toString(),
                    exerciseId = selectedId,
                    scheduledAt = scheduledAt,
                    sets = filters.globalSets,
                    reps = filters.globalReps,
                    weightKg = ex.lastWeightKg ?: 0.0,
                    restSeconds = 90,
                    notes = null,
                ),
            )
            filterState.update { state ->
                state.copy(
                    selectedExerciseId = null,
                    globalSets = DEFAULT_SETS,
                    globalReps = DEFAULT_REPS,
                    selectedDate = defaultExerciseLibraryCartDateMillis(),
                )
            }
        }
    }

    fun selectExerciseForDetail(exerciseId: String) {
        detailExerciseId.value = exerciseId
    }

    fun clearExerciseDetail() {
        detailExerciseId.value = null
    }

    private fun buildSections(
        grouped: Map<BodyRegion, List<Exercise>>,
        filters: ExerciseLibraryUiState,
    ): ImmutableList<ExerciseSectionUiItem> {
        val query = normalizeExerciseLibraryQuery(filters.searchQuery)
        val category = filters.selectedExerciseCategory
        val equipment = filters.selectedEquipment
        val selectedExerciseId = filters.selectedExerciseId
        return BodyRegion.entries.mapNotNull { region ->
            val items = grouped[region]
                ?.filter { ex ->
                    ex.matchesLibrarySearch(query) &&
                        (category == null || ex.category == category) &&
                        (equipment == null || ex.equipment == equipment)
                }
                ?.map { ex ->
                    ex.toLibraryCardUiModel(appContext, selectedExerciseId)
                }
                ?: emptyList()
            if (items.isEmpty()) null else ExerciseSectionUiItem(region, items.toImmutableList())
        }.toImmutableList()
    }

    private companion object {
        const val DEFAULT_SETS = 3
        const val DEFAULT_REPS = 10
    }
}
