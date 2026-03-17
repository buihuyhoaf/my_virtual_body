package com.hoabui.virtualbody3d.ui.workoutfeed.viewmodel

import androidx.lifecycle.viewModelScope
import com.hoabui.virtualbody3d.core.base.UiStateViewModel
import com.hoabui.virtualbody3d.domain.usecase.GetWorkoutFeedUseCase
import com.hoabui.virtualbody3d.ui.workoutfeed.state.WorkoutFeedUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class WorkoutFeedViewModel @Inject constructor(
    private val getWorkoutFeedUseCase: GetWorkoutFeedUseCase
) : UiStateViewModel<WorkoutFeedUiState, Unit>() {

    init {
        getWorkoutFeedUseCase()
            .onEach { items ->
                setSuccess(WorkoutFeedUiState(feedItems = items))
            }
            .catch { setError(it.message ?: "Unknown error") }
            .launchIn(viewModelScope)
    }
}
