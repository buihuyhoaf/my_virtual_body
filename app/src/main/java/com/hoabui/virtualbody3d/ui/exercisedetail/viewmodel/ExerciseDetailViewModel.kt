package com.hoabui.virtualbody3d.ui.exercisedetail.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.hoabui.virtualbody3d.core.base.UiStateViewModel
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.domain.usecase.GetExerciseByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class ExerciseDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getExerciseByIdUseCase: GetExerciseByIdUseCase
) : UiStateViewModel<Exercise, Unit>() {

    private val exerciseId: String = savedStateHandle.get<String>("exerciseId").orEmpty()

    init {
        launchSafely {
            setLoading()
            getExerciseByIdUseCase(exerciseId).collectLatest { exercise ->
                if (exercise != null) {
                    setSuccess(exercise)
                } else {
                    setError("Exercise not found")
                }
            }
        }
    }
}
