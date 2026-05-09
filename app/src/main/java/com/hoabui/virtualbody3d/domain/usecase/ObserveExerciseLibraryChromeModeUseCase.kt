package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.ui.exerciselibrary.manager.ExerciseLibraryCartManager
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryChromeMode
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@ActivityRetainedScoped
class ObserveExerciseLibraryChromeModeUseCase @Inject constructor(
    private val cartManager: ExerciseLibraryCartManager,
) {

    private lateinit var chromeModeFlow: StateFlow<ExerciseLibraryChromeMode>

    fun observe(scope: CoroutineScope): StateFlow<ExerciseLibraryChromeMode> {
        if (::chromeModeFlow.isInitialized) {
            return chromeModeFlow
        }
        chromeModeFlow = cartManager.chromeMode.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ExerciseLibraryChromeMode.Idle,
        )
        return chromeModeFlow
    }
}
