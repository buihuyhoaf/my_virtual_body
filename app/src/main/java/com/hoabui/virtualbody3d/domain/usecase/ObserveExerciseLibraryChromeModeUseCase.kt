package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.ui.exerciselibrary.manager.ExerciseLibraryCartManager
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryChromeMode
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@ActivityRetainedScoped
class ObserveExerciseLibraryChromeModeUseCase @Inject constructor(
    private val exerciseLibraryCartManager: ExerciseLibraryCartManager,
) {
    val chromeMode: StateFlow<ExerciseLibraryChromeMode>
        get() = exerciseLibraryCartManager.chromeMode
}
