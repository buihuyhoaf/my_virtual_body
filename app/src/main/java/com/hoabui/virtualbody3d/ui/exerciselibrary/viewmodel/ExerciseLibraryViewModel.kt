package com.hoabui.virtualbody3d.ui.exerciselibrary.viewmodel

import androidx.lifecycle.viewModelScope
import com.hoabui.virtualbody3d.core.base.UiStateViewModel
import com.hoabui.virtualbody3d.domain.model.BodyRegion
import com.hoabui.virtualbody3d.domain.model.Difficulty
import com.hoabui.virtualbody3d.domain.model.Exercise
import com.hoabui.virtualbody3d.domain.usecase.GetExercisesUseCase
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
    private val getExercisesUseCase: GetExercisesUseCase
) : UiStateViewModel<ExerciseLibraryUiState, Unit>() {

    private val groupedExercises = MutableStateFlow<Map<BodyRegion, List<Exercise>>>(emptyMap())
    private val filterState = MutableStateFlow(ExerciseLibraryUiState())
    private val selectedExerciseId = MutableStateFlow<String?>(null)

    init {
        getExercisesUseCase()
            .onEach { groupedExercises.value = it }
            .catch { setError(it.message ?: "Unknown error") }
            .launchIn(viewModelScope)

        combine(groupedExercises, filterState, selectedExerciseId) { grouped, filters, selectedId ->
            val sections = buildSections(
                grouped,
                filters.searchQuery,
                filters.selectedBodyRegion,
                filters.selectedDifficulty
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

    fun selectBodyRegion(region: BodyRegion?) {
        filterState.update { it.copy(selectedBodyRegion = region) }
    }

    fun selectDifficulty(difficulty: Difficulty?) {
        filterState.update { it.copy(selectedDifficulty = difficulty) }
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
        selectedBodyRegion: BodyRegion?,
        selectedDifficulty: Difficulty?
    ): List<ExerciseSectionUiItem> {
        val query = searchQuery.trim().lowercase()
        val regions = if (selectedBodyRegion != null) listOf(selectedBodyRegion) else BodyRegion.entries
        return regions.mapNotNull { region ->
            val exercises = grouped[region]
                ?.filter { ex ->
                    (query.isEmpty() || ex.name.lowercase().contains(query)) &&
                    (selectedDifficulty == null || ex.difficulty == selectedDifficulty)
                }
                ?.map { it.toExerciseUiModel() }
                ?: emptyList()
            if (exercises.isEmpty()) null else ExerciseSectionUiItem(region, exercises)
        }
    }
}
