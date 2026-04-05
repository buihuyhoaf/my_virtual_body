package com.hoabui.virtualbody3d.ui.body.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hoabui.virtualbody3d.domain.usecase.GetDashboardUpcomingExercisesUseCase
import com.hoabui.virtualbody3d.ui.body.data.UpcomingWorkoutUiItem
import com.hoabui.virtualbody3d.ui.body.data.toUpcomingItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class ExercisesViewModel @Inject constructor(
    private val getDashboardUpcomingExercisesUseCase: GetDashboardUpcomingExercisesUseCase,
) : ViewModel() {

    private val _upcomingWorkouts = MutableStateFlow<List<UpcomingWorkoutUiItem>>(emptyList())
    val upcomingWorkouts: StateFlow<List<UpcomingWorkoutUiItem>> = _upcomingWorkouts.asStateFlow()

    init {
        getDashboardUpcomingExercisesUseCase()
            .onEach { list -> _upcomingWorkouts.value = list.map { it.toUpcomingItem() } }
            .catch { _ -> _upcomingWorkouts.value = emptyList() }
            .launchIn(viewModelScope)
    }

}
