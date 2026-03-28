package com.hoabui.virtualbody3d.ui.exerciselibrary.viewmodel

import androidx.lifecycle.viewModelScope
import com.hoabui.virtualbody3d.core.base.UiStateViewModel
import com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion
import com.hoabui.virtualbody3d.domain.model.exercise.EquipmentType
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseCategory
import com.hoabui.virtualbody3d.ui.exerciselibrary.ExerciseLibraryQuickChip
import com.hoabui.virtualbody3d.domain.model.exercise.matchesLibrarySearch
import com.hoabui.virtualbody3d.domain.model.exercise.normalizeExerciseLibraryQuery
import com.hoabui.virtualbody3d.domain.usecase.GetExerciseLibraryUseCase
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.toExerciseUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseSectionUiItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ExerciseLibraryViewModel @Inject constructor(
    private val getExerciseLibraryUseCase: GetExerciseLibraryUseCase
) : UiStateViewModel<ExerciseLibraryUiState, Unit>() {

    private val groupedExercises = MutableStateFlow<Map<BodyRegion, List<Exercise>>>(emptyMap())
    private val filterState = MutableStateFlow(ExerciseLibraryUiState())
    private val selectedExerciseId = MutableStateFlow<String?>(null)

    init {
        getExerciseLibraryUseCase()
            .onEach { groupedExercises.value = it }
            .catch { setError(it.message ?: "Unknown error") }
            .launchIn(viewModelScope)

        combine(groupedExercises, filterState, selectedExerciseId) { grouped, filters, selectedId ->
            val sections = buildSections(
                grouped,
                filters.searchQuery,
                filters.selectedExerciseCategory,
                filters.selectedEquipment,
            )
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
                ExerciseLibraryQuickChip.Dumbbell -> state.copy(
                    selectedExerciseCategory = null,
                    selectedEquipment = EquipmentType.Dumbbell,
                )
            }
        }
    }

    fun selectExerciseForDetail(exerciseId: String) {
        selectedExerciseId.value = exerciseId
    }

    fun clearExerciseDetail() {
        selectedExerciseId.value = null
    }

    private fun buildSections(
        grouped: Map<BodyRegion, List<Exercise>>,
        searchQuery: String,
        selectedExerciseCategory: ExerciseCategory?,
        selectedEquipment: EquipmentType?,
    ): List<ExerciseSectionUiItem> {
        val query = normalizeExerciseLibraryQuery(searchQuery)
        return BodyRegion.entries.mapNotNull { region ->
            val exercises = grouped[region]
                ?.filter { ex ->
                    ex.matchesLibrarySearch(query) &&
                    (selectedExerciseCategory == null || ex.category == selectedExerciseCategory) &&
                    (selectedEquipment == null || ex.equipment == selectedEquipment)
                }
                ?.map { it.toExerciseUiModel() }
                ?: emptyList()
            if (exercises.isEmpty()) null else ExerciseSectionUiItem(region, exercises)
        }
    }
}
