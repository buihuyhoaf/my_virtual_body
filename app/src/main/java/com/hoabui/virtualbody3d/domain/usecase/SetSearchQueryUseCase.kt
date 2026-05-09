package com.hoabui.virtualbody3d.domain.usecase

import javax.inject.Inject

class SetSearchQueryUseCase @Inject constructor(
    private val exerciseLibrarySearchManager: com.hoabui.virtualbody3d.ui.exerciselibrary.manager.ExerciseLibrarySearchManager
) {
    operator fun invoke(query: String) {
        exerciseLibrarySearchManager.setSearchQuery(query)
    }
}
