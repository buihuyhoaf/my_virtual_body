package com.hoabui.virtualbody3d.ui.exerciselibrary.manager

import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryChromeMode
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@ViewModelScoped
class ExerciseLibraryChromeManager @Inject constructor() {

    private val _chromeMode = MutableStateFlow<ExerciseLibraryChromeMode>(ExerciseLibraryChromeMode.Idle)
    val chromeMode: StateFlow<ExerciseLibraryChromeMode> = _chromeMode.asStateFlow()

    fun setChromeMode(mode: ExerciseLibraryChromeMode) {
        _chromeMode.value = mode
    }

    fun setIdle() {
        _chromeMode.value = ExerciseLibraryChromeMode.Idle
    }
}
