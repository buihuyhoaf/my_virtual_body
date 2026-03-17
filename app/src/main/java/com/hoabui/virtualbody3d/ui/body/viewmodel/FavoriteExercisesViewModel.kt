package com.hoabui.virtualbody3d.ui.body.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hoabui.virtualbody3d.domain.model.FavoriteExercise
import com.hoabui.virtualbody3d.domain.usecase.GetFavoriteExercisesUseCase
import com.hoabui.virtualbody3d.ui.body.data.FavoriteExerciseUiItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class FavoriteExercisesViewModel @Inject constructor(
    private val getFavoriteExercisesUseCase: GetFavoriteExercisesUseCase
) : ViewModel() {

    private val _exercises = MutableStateFlow<List<FavoriteExerciseUiItem>>(emptyList())
    val exercises: StateFlow<List<FavoriteExerciseUiItem>> = _exercises.asStateFlow()

    init {
        getFavoriteExercisesUseCase()
            .onEach { list -> _exercises.value = list.map(::toUiItem) }
            .catch { _ -> _exercises.value = emptyList() }
            .launchIn(viewModelScope)
    }

    private fun toUiItem(domain: FavoriteExercise): FavoriteExerciseUiItem =
        FavoriteExerciseUiItem(
            name = domain.name,
            reps = domain.reps,
            sets = domain.sets,
            imageResId = domain.imageResId
        )
}
