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
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseDraft
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseSectionUiItem
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.isAnchoredAddEnabled
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
                selectedExerciseForDetail = selectedExercise,
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

    /** Adds an exercise with an empty draft if new; always sets [ExerciseLibraryUiState.activeExerciseId]. */
    fun onQuickAdd(exerciseId: String) {
        filterState.update { state ->
            val base = state.itemDrafts.toPersistentMap()
            val nextDrafts = if (exerciseId in base) {
                base
            } else {
                base.put(exerciseId, ExerciseDraft())
            }
            state.copy(
                itemDrafts = nextDrafts,
                activeExerciseId = exerciseId,
            )
        }
    }

    fun setActiveCartExercise(exerciseId: String) {
        filterState.update { state ->
            if (exerciseId in state.itemDrafts) {
                state.copy(activeExerciseId = exerciseId)
            } else {
                state
            }
        }
    }

    fun clearAll() {
        filterState.update {
            it.copy(
                itemDrafts = persistentMapOf(),
                activeExerciseId = null,
                selectedDate = null,
                selectedTime = null,
            )
        }
    }

    fun updateActiveDraft(sets: String, reps: String) {
        filterState.update { state ->
            val id = state.activeExerciseId ?: return@update state
            val draft = state.itemDrafts[id] ?: return@update state
            state.copy(
                itemDrafts = state.itemDrafts.toPersistentMap().put(
                    id,
                    draft.copy(sets = sets, reps = reps),
                ),
            )
        }
    }

    fun updateCartDate(dateMillis: Long) {
        filterState.update { it.copy(selectedDate = dateMillis) }
    }

    fun updateCartTime(time: LocalTime) {
        filterState.update { it.copy(selectedTime = time) }
    }

    fun confirmCartToWorkout() {
        launchSafely {
            val filters = filterState.value
            if (!filters.isAnchoredAddEnabled()) return@launchSafely
            val exercisesById = groupedExercises.value.values.flatten().associateBy { it.id }
            val zone = ZoneId.systemDefault()
            val dateMillis = filters.selectedDate!!
            val time = filters.selectedTime!!
            val date = Instant.ofEpochMilli(dateMillis).atZone(zone).toLocalDate()
            val scheduledAt = LocalDateTime.of(date, time)
            var scheduledCount = 0
            filters.itemDrafts.forEach { (exerciseId, draft) ->
                if (draft.sets.isBlank() || draft.reps.isBlank()) return@forEach
                val ex = exercisesById[exerciseId] ?: return@forEach
                val sets = draft.sets.trim().toIntOrNull() ?: return@forEach
                val reps = draft.reps.trim().toIntOrNull() ?: return@forEach
                if (sets <= 0 || reps <= 0) return@forEach
                addWorkoutUseCase(
                    WorkoutSchedule(
                        id = UUID.randomUUID().toString(),
                        exerciseId = exerciseId,
                        scheduledAt = scheduledAt,
                        sets = sets,
                        reps = reps,
                        weightKg = ex.lastWeightKg ?: 0.0,
                        restSeconds = 90,
                        notes = null,
                    ),
                )
                scheduledCount++
            }
            if (scheduledCount == 0) return@launchSafely
            filterState.update {
                it.copy(
                    itemDrafts = persistentMapOf(),
                    activeExerciseId = null,
                    selectedDate = null,
                    selectedTime = null,
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
        val cartIds = filters.itemDrafts.keys
        val activeId = filters.activeExerciseId
        return BodyRegion.entries.mapNotNull { region ->
            val items = grouped[region]
                ?.filter { ex ->
                    ex.matchesLibrarySearch(query) &&
                        (category == null || ex.category == category) &&
                        (equipment == null || ex.equipment == equipment)
                }
                ?.map { ex ->
                    ex.toLibraryCardUiModel(appContext, cartIds, activeId)
                }
                ?: emptyList()
            if (items.isEmpty()) null else ExerciseSectionUiItem(region, items.toImmutableList())
        }.toImmutableList()
    }
}
