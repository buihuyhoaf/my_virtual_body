package com.hoabui.virtualbody3d.domain.usecase

import javax.inject.Inject

class SetInitialExerciseCategoryFilterUseCase @Inject constructor(
    private val exerciseLibrarySearchManager: com.hoabui.virtualbody3d.ui.exerciselibrary.manager.ExerciseLibrarySearchManager
) {
    operator fun invoke(category: com.hoabui.virtualbody3d.domain.model.exercise.ExerciseCategory) {
        exerciseLibrarySearchManager.setInitialExerciseCategoryFilter(category)
    }
}
