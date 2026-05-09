package com.hoabui.virtualbody3d.ui.exerciselibrary.viewmodel

import androidx.lifecycle.viewModelScope
import com.hoabui.virtualbody3d.core.base.UiStateViewModel
import com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseCategory
import com.hoabui.virtualbody3d.domain.usecase.ClearCartUseCase
import com.hoabui.virtualbody3d.domain.usecase.ObserveExerciseCatalogUseCase
import com.hoabui.virtualbody3d.domain.usecase.ObserveExerciseLibraryUiStateUseCase
import com.hoabui.virtualbody3d.domain.usecase.RemoveCartItemUseCase
import com.hoabui.virtualbody3d.domain.usecase.SelectCartItemUseCase
import com.hoabui.virtualbody3d.domain.usecase.SetCartFieldManualUseCase
import com.hoabui.virtualbody3d.domain.usecase.SetInitialBodyRegionFilterUseCase
import com.hoabui.virtualbody3d.domain.usecase.SetInitialExerciseCategoryFilterUseCase
import com.hoabui.virtualbody3d.domain.usecase.SetSearchQueryUseCase
import com.hoabui.virtualbody3d.domain.usecase.StepCartFieldUseCase
import com.hoabui.virtualbody3d.domain.usecase.ToggleCardSelectionUseCase
import com.hoabui.virtualbody3d.domain.usecase.ToggleCartExpandedUseCase
import com.hoabui.virtualbody3d.ui.exerciselibrary.cart.CartSetField
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryEvent
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ExerciseLibraryViewModel @Inject constructor(
    private val observeExerciseCatalogUseCase: ObserveExerciseCatalogUseCase,
    private val observeExerciseLibraryUiStateUseCase: ObserveExerciseLibraryUiStateUseCase,
    private val setSearchQueryUseCase: SetSearchQueryUseCase,
    private val setInitialExerciseCategoryFilterUseCase: SetInitialExerciseCategoryFilterUseCase,
    private val setInitialBodyRegionFilterUseCase: SetInitialBodyRegionFilterUseCase,
    private val toggleCardSelectionUseCase: ToggleCardSelectionUseCase,
    private val selectCartItemUseCase: SelectCartItemUseCase,
    private val removeCartItemUseCase: RemoveCartItemUseCase,
    private val clearCartUseCase: ClearCartUseCase,
    private val stepCartFieldUseCase: StepCartFieldUseCase,
    private val setCartFieldManualUseCase: SetCartFieldManualUseCase,
    private val toggleCartExpandedUseCase: ToggleCartExpandedUseCase,
) : UiStateViewModel<ExerciseLibraryUiState, ExerciseLibraryEvent>() {


    init {
        observeExerciseCatalogUseCase.startCollection(viewModelScope) {
            setError(it.message ?: "Unknown error")
        }
        observeExerciseLibraryUiStateUseCase.observe(viewModelScope)
            .onEach { setSuccess(it) }
            .launchIn(viewModelScope)
    }

    fun setSearchQuery(query: String) {
        sendEvent(ExerciseLibraryEvent.SearchQueryChanged(query))
        setSearchQueryUseCase(query)
    }

    fun setInitialExerciseCategoryFilter(category: ExerciseCategory) {
        sendEvent(ExerciseLibraryEvent.CategoryFilterChanged(category))
        setInitialExerciseCategoryFilterUseCase(category)
    }

    fun setInitialBodyRegionFilter(regions: ImmutableSet<BodyRegion>) {
        sendEvent(ExerciseLibraryEvent.BodyRegionFilterChanged(regions))
        setInitialBodyRegionFilterUseCase(regions)
    }

    fun toggleCardSelection(exerciseId: String) {
        sendEvent(ExerciseLibraryEvent.CardSelectionToggled(exerciseId))
        toggleCardSelectionUseCase(exerciseId)
    }

    fun selectCartItem(exerciseId: String) {
        sendEvent(ExerciseLibraryEvent.CartItemSelected(exerciseId))
        selectCartItemUseCase(observeExerciseLibraryUiStateUseCase.snapshotForCartActions(), exerciseId)
    }

    fun removeCartItem(exerciseId: String) {
        sendEvent(ExerciseLibraryEvent.CartItemRemoved(exerciseId))
        removeCartItemUseCase(observeExerciseLibraryUiStateUseCase.snapshotForCartActions(), exerciseId)
    }

    fun clearCart() {
        sendEvent(ExerciseLibraryEvent.CartCleared)
        clearCartUseCase()
    }

    fun stepCartField(exerciseId: String, setIndex: Int, field: CartSetField, delta: Int) {
        sendEvent(ExerciseLibraryEvent.CartFieldStepped(exerciseId, setIndex, field, delta))
        stepCartFieldUseCase(exerciseId, setIndex, field, delta)
    }

    fun setCartFieldManual(exerciseId: String, setIndex: Int, field: CartSetField, value: String) {
        sendEvent(ExerciseLibraryEvent.CartFieldManualSet(exerciseId, setIndex, field, value))
        setCartFieldManualUseCase(exerciseId, setIndex, field, value)
    }

    fun toggleCartExpanded() {
        sendEvent(ExerciseLibraryEvent.CartExpandedToggled)
        toggleCartExpandedUseCase()
    }
}
