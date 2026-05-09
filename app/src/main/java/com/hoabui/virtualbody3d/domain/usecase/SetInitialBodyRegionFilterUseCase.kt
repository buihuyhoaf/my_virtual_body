package com.hoabui.virtualbody3d.domain.usecase

import javax.inject.Inject

class SetInitialBodyRegionFilterUseCase @Inject constructor(
    private val exerciseLibrarySearchManager: com.hoabui.virtualbody3d.ui.exerciselibrary.manager.ExerciseLibrarySearchManager
) {
    operator fun invoke(regions: kotlinx.collections.immutable.ImmutableSet<com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion>) {
        exerciseLibrarySearchManager.setInitialBodyRegionFilter(regions)
    }
}
