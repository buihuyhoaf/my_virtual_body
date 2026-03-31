package com.hoabui.virtualbody3d.ui.exerciselibrary.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.hoabui.virtualbody3d.core.base.UiStateViewModel
import com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion
import com.hoabui.virtualbody3d.domain.model.exercise.EquipmentType
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseCategory
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSchedule
import com.hoabui.virtualbody3d.domain.model.exercise.normalizeDurationMinutesSeconds
import com.hoabui.virtualbody3d.domain.model.exercise.matchesLibrarySearch
import com.hoabui.virtualbody3d.domain.model.exercise.normalizeExerciseLibraryQuery
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.usecase.AddWorkoutUseCase
import com.hoabui.virtualbody3d.domain.usecase.GetExerciseLibraryUseCase
import com.hoabui.virtualbody3d.ui.exerciselibrary.ExerciseLibraryQuickChip
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.toLibraryCardUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.AddExerciseSuccessSummary
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseDraft
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseSectionUiItem
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.isAnchoredAddEnabled
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.collections.immutable.toPersistentList
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
            val measurementById = grouped.values.flatten()
                .associate { it.id to it.measurementMode }
                .toImmutableMap()
            filters.copy(
                sections = sections,
                selectedExerciseForDetail = selectedExercise,
                exerciseMeasurementById = measurementById,
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

    /** Main list short-press: add to cart or remove if already selected ([itemDrafts]). */
    fun toggleExerciseInCartFromList(exerciseId: String) {
        filterState.update { state ->
            val base = state.itemDrafts.toPersistentMap()
            if (exerciseId !in base) {
                val nextDrafts = base.put(exerciseId, ExerciseDraft())
                val nextOrder = if (exerciseId in state.draftOrder) {
                    state.draftOrder
                } else {
                    state.draftOrder.toPersistentList().add(exerciseId)
                }
                state.copy(
                    itemDrafts = nextDrafts,
                    draftOrder = nextOrder,
                    activeExerciseId = exerciseId,
                )
            } else {
                val nextDrafts = base.remove(exerciseId)
                val orderBefore = state.draftOrder
                val nextOrder = state.draftOrder.filter { it != exerciseId }.toPersistentList()
                val nextActive = resolveActiveExerciseAfterRemoval(
                    removedId = exerciseId,
                    previousActive = state.activeExerciseId,
                    newDrafts = nextDrafts,
                    orderBeforeRemoval = orderBefore,
                    newOrder = nextOrder,
                )
                state.copy(
                    itemDrafts = nextDrafts,
                    draftOrder = nextOrder,
                    activeExerciseId = nextActive,
                )
            }
        }
    }

    /**
     * Detail sheet "Add": never removes. Inserts with empty draft if absent, else only focuses.
     */
    fun ensureInCartAndFocusFromDetail(exerciseId: String) {
        filterState.update { state ->
            val base = state.itemDrafts.toPersistentMap()
            if (exerciseId !in base) {
                val nextDrafts = base.put(exerciseId, ExerciseDraft())
                val nextOrder = if (exerciseId in state.draftOrder) {
                    state.draftOrder
                } else {
                    state.draftOrder.toPersistentList().add(exerciseId)
                }
                state.copy(
                    itemDrafts = nextDrafts,
                    draftOrder = nextOrder,
                    activeExerciseId = exerciseId,
                )
            } else {
                state.copy(activeExerciseId = exerciseId)
            }
        }
    }

    /** Cart thumbnail remove ([X]); does not run for thumbnail body (focus only). */
    fun removeFromCart(exerciseId: String) {
        filterState.update { state ->
            if (exerciseId !in state.itemDrafts) return@update state
            val base = state.itemDrafts.toPersistentMap()
            val nextDrafts = base.remove(exerciseId)
            val orderBefore = state.draftOrder
            val nextOrder = state.draftOrder.filter { it != exerciseId }.toPersistentList()
            val nextActive = resolveActiveExerciseAfterRemoval(
                removedId = exerciseId,
                previousActive = state.activeExerciseId,
                newDrafts = nextDrafts,
                orderBeforeRemoval = orderBefore,
                newOrder = nextOrder,
            )
            state.copy(
                itemDrafts = nextDrafts,
                draftOrder = nextOrder,
                activeExerciseId = nextActive,
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
                draftOrder = persistentListOf(),
                activeExerciseId = null,
                selectedDate = null,
                selectedTime = null,
                addExerciseSuccess = null,
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
                val ex = exercisesById[exerciseId] ?: return@forEach
                when (ex.measurementMode) {
                    ExerciseMeasurementMode.Strength -> {
                        if (draft.sets.isBlank() || draft.reps.isBlank()) return@forEach
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
                                measurementMode = ExerciseMeasurementMode.Strength,
                                durationSeconds = null,
                            ),
                        )
                    }
                    ExerciseMeasurementMode.Duration -> {
                        val minutes = draft.sets.trim().toIntOrNull() ?: 0
                        val seconds = draft.reps.trim().toIntOrNull() ?: 0
                        val total = normalizeDurationMinutesSeconds(minutes, seconds)
                        if (total <= 0) return@forEach
                        addWorkoutUseCase(
                            WorkoutSchedule(
                                id = UUID.randomUUID().toString(),
                                exerciseId = exerciseId,
                                scheduledAt = scheduledAt,
                                sets = 1,
                                reps = 0,
                                weightKg = ex.lastWeightKg ?: 0.0,
                                restSeconds = 90,
                                notes = null,
                                measurementMode = ExerciseMeasurementMode.Duration,
                                durationSeconds = total,
                            ),
                        )
                    }
                }
                scheduledCount++
            }
            if (scheduledCount == 0) return@launchSafely
            val summary = AddExerciseSuccessSummary(
                exerciseCount = scheduledCount,
                scheduledDateMillis = dateMillis,
                scheduledTime = time,
            )
            filterState.update {
                it.copy(
                    itemDrafts = persistentMapOf(),
                    draftOrder = persistentListOf(),
                    activeExerciseId = null,
                    selectedDate = null,
                    selectedTime = null,
                    addExerciseSuccess = summary,
                    workoutPlanFabBadgeCount = it.workoutPlanFabBadgeCount + scheduledCount,
                )
            }
        }
    }

    fun onWorkoutPlanFabClick() {
        Toast.makeText(
            appContext,
            appContext.getString(R.string.exercise_library_workout_plan_stub_toast),
            Toast.LENGTH_SHORT,
        ).show()
    }

    fun dismissAddExerciseSuccess() {
        filterState.update { it.copy(addExerciseSuccess = null) }
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
