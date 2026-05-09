package com.hoabui.virtualbody3d.ui.exerciselibrary.manager

import com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion
import com.hoabui.virtualbody3d.domain.model.exercise.EquipmentType
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseCategory
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@ActivityRetainedScoped
class ExerciseLibrarySearchManager @Inject constructor() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedExerciseCategory = MutableStateFlow<ExerciseCategory?>(null)
    val selectedExerciseCategory: StateFlow<ExerciseCategory?> = _selectedExerciseCategory.asStateFlow()

    private val _selectedBodyRegions = MutableStateFlow<ImmutableSet<BodyRegion>?>(null)
    val selectedBodyRegions: StateFlow<ImmutableSet<BodyRegion>?> = _selectedBodyRegions.asStateFlow()

    private val _selectedEquipment = MutableStateFlow<EquipmentType?>(null)
    val selectedEquipment: StateFlow<EquipmentType?> = _selectedEquipment.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setInitialExerciseCategoryFilter(category: ExerciseCategory) {
        _selectedExerciseCategory.value = category
        _selectedBodyRegions.value = null
    }

    fun setInitialBodyRegionFilter(regions: ImmutableSet<BodyRegion>) {
        _selectedBodyRegions.value = regions
        _selectedExerciseCategory.value = null
    }
}
