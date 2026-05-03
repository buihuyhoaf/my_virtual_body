package com.hoabui.virtualbody3d.ui.exerciselibrary.manager

import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseLibraryCartSnapshot
import com.hoabui.virtualbody3d.domain.usecase.ExerciseLibraryCartCommand
import com.hoabui.virtualbody3d.domain.usecase.ToggleExerciseInCartUseCase
import com.hoabui.virtualbody3d.ui.exerciselibrary.util.toCartSnapshot
import com.hoabui.virtualbody3d.ui.exerciselibrary.util.withCartSnapshot
import com.hoabui.virtualbody3d.ui.exerciselibrary.cart.CartSetField
import com.hoabui.virtualbody3d.ui.exerciselibrary.cart.ExerciseDraft
import com.hoabui.virtualbody3d.ui.exerciselibrary.cart.SetRowDraft
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryUiState
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@ViewModelScoped
class ExerciseLibraryCartManager @Inject constructor(
    private val toggleExerciseInCartUseCase: ToggleExerciseInCartUseCase,
) {

    private val _itemDrafts =
        MutableStateFlow<ImmutableMap<String, ExerciseDraft>>(persistentMapOf())
    val itemDrafts: StateFlow<ImmutableMap<String, ExerciseDraft>> = _itemDrafts.asStateFlow()

    private val _draftOrder = MutableStateFlow<ImmutableList<String>>(persistentListOf())
    val draftOrder: StateFlow<ImmutableList<String>> = _draftOrder.asStateFlow()

    private val _activeExerciseId = MutableStateFlow<String?>(null)
    val activeExerciseId: StateFlow<String?> = _activeExerciseId.asStateFlow()

    private val _isCartExpanded = MutableStateFlow(false)
    val isCartExpanded: StateFlow<Boolean> = _isCartExpanded.asStateFlow()

    fun applyCartSnapshot(syntheticBefore: ExerciseLibraryUiState, snap: ExerciseLibraryCartSnapshot) {
        val updated = syntheticBefore.withCartSnapshot(snap)
        _itemDrafts.value = updated.itemDrafts
        _draftOrder.value = updated.draftOrder
        _activeExerciseId.value = updated.activeExerciseId
    }

    /**
     * @return true if the exercise was added to the cart (for prefill).
     */
    fun toggleCardSelection(synthetic: ExerciseLibraryUiState, exerciseId: String): Boolean {
        val wasInCart = exerciseId in _itemDrafts.value
        val snap = toggleExerciseInCartUseCase(
            synthetic.toCartSnapshot(),
            ExerciseLibraryCartCommand.Toggle(exerciseId),
        )
        applyCartSnapshot(synthetic, snap)
        return !wasInCart && exerciseId in _itemDrafts.value
    }

    fun selectCartItem(synthetic: ExerciseLibraryUiState, exerciseId: String) {
        val snap = toggleExerciseInCartUseCase(
            synthetic.toCartSnapshot(),
            ExerciseLibraryCartCommand.SetActive(exerciseId),
        )
        applyCartSnapshot(synthetic, snap)
    }

    fun removeCartItem(synthetic: ExerciseLibraryUiState, exerciseId: String) {
        val snap = toggleExerciseInCartUseCase(
            synthetic.toCartSnapshot(),
            ExerciseLibraryCartCommand.Remove(exerciseId),
        )
        applyCartSnapshot(synthetic, snap)
    }

    fun clearCartOnly() {
        _itemDrafts.value = persistentMapOf()
        _draftOrder.value = persistentListOf()
        _activeExerciseId.value = null
        _isCartExpanded.value = false
    }

    fun setCartExpanded(expanded: Boolean) {
        _isCartExpanded.value = expanded
    }

    fun setSelectionBarEditCart(
        itemDrafts: ImmutableMap<String, ExerciseDraft>,
        draftOrder: ImmutableList<String>,
        activeExerciseId: String,
        isCartExpanded: Boolean = true,
    ) {
        _itemDrafts.value = itemDrafts
        _draftOrder.value = draftOrder
        _activeExerciseId.value = activeExerciseId
        _isCartExpanded.value = isCartExpanded
    }

    fun restoreCartFromBaseline(
        itemDrafts: ImmutableMap<String, ExerciseDraft>,
        draftOrder: ImmutableList<String>,
        activeExerciseId: String?,
        isCartExpanded: Boolean,
    ) {
        _itemDrafts.value = itemDrafts
        _draftOrder.value = draftOrder
        _activeExerciseId.value = activeExerciseId
        _isCartExpanded.value = isCartExpanded
    }

    fun clearCartForIsolatedSelectionEdit() {
        _itemDrafts.value = persistentMapOf()
        _draftOrder.value = persistentListOf()
        _activeExerciseId.value = null
        _isCartExpanded.value = false
    }

    fun stepCartField(exerciseId: String, setIndex: Int, field: CartSetField, delta: Int) {
        val draft = _itemDrafts.value[exerciseId] ?: return
        val newDraft = when (field) {
            CartSetField.SETS -> {
                if (delta > 0) {
                    val lastRow = draft.setRows.lastOrNull() ?: SetRowDraft()
                    draft.copy(setRows = (draft.setRows + lastRow).toImmutableList())
                } else if (delta < 0 && draft.setRows.size > 1) {
                    draft.copy(setRows = draft.setRows.dropLast(1).toImmutableList())
                } else {
                    return
                }
            }
            else -> {
                val row = draft.setRows.getOrNull(setIndex) ?: return
                val newRow = when (field) {
                    CartSetField.REPS -> row.copy(reps = (row.reps + delta).coerceAtLeast(0))
                    CartSetField.WEIGHT -> row.copy(
                        weightKg = (row.weightKg + delta * WEIGHT_STEP_KG).coerceAtLeast(0.0),
                    )
                    CartSetField.MINUTES -> row.copy(minutes = (row.minutes + delta).coerceAtLeast(0))
                    CartSetField.SECONDS -> {
                        val totalSec = (row.minutes * 60 + row.seconds + delta * SECONDS_STEP)
                            .coerceAtLeast(0)
                        row.copy(minutes = totalSec / 60, seconds = totalSec % 60)
                    }
                    CartSetField.SETS -> return
                }
                val newRows = draft.setRows.mapIndexed { i, r -> if (i == setIndex) newRow else r }
                    .toImmutableList()
                draft.copy(setRows = newRows)
            }
        }
        if (exerciseId !in _itemDrafts.value) return
        _itemDrafts.update { (it + (exerciseId to newDraft)).toImmutableMap() }
    }

    fun setCartFieldManual(exerciseId: String, setIndex: Int, field: CartSetField, value: String) {
        val draft = _itemDrafts.value[exerciseId] ?: return
        when (field) {
            CartSetField.SETS -> {
                val count = value.toIntOrNull()?.coerceAtLeast(1) ?: return
                val current = draft.setRows
                val newRows = when {
                    count == current.size -> return
                    count > current.size -> {
                        val last = current.lastOrNull() ?: SetRowDraft()
                        (current + List(count - current.size) { last }).toImmutableList()
                    }
                    else -> current.take(count).toImmutableList()
                }
                _itemDrafts.update { (it + (exerciseId to draft.copy(setRows = newRows))).toImmutableMap() }
            }
            else -> {
                val row = draft.setRows.getOrNull(setIndex) ?: return
                val newRow = when (field) {
                    CartSetField.REPS -> row.copy(reps = value.toIntOrNull()?.coerceAtLeast(0) ?: return)
                    CartSetField.WEIGHT -> row.copy(
                        weightKg = value.toDoubleOrNull()?.coerceAtLeast(0.0) ?: return,
                    )
                    CartSetField.MINUTES -> row.copy(minutes = value.toIntOrNull()?.coerceAtLeast(0) ?: return)
                    CartSetField.SECONDS -> row.copy(
                        seconds = value.toIntOrNull()?.coerceIn(0, MAX_SECONDS_IN_MINUTE) ?: return,
                    )
                    CartSetField.SETS -> return
                }
                val newRows = draft.setRows.mapIndexed { i, r -> if (i == setIndex) newRow else r }
                    .toImmutableList()
                _itemDrafts.update { (it + (exerciseId to draft.copy(setRows = newRows))).toImmutableMap() }
            }
        }
    }

    fun toggleCartExpanded() {
        _isCartExpanded.update { !it }
    }

    fun prefillFromHistory(exerciseId: String, catalogExercisesById: Map<String, Exercise>) {
        val exercise = catalogExercisesById[exerciseId] ?: return
        val lastWeight = exercise.lastWeightKg ?: return
        val draft = _itemDrafts.value[exerciseId] ?: return
        if (draft.setRows.isEmpty()) return
        val prefilled = draft.copy(
            setRows = draft.setRows.mapIndexed { i, row ->
                if (i == 0) row.copy(weightKg = lastWeight) else row
            }.toImmutableList(),
        )
        if (exerciseId !in _itemDrafts.value) return
        _itemDrafts.update { (it + (exerciseId to prefilled)).toImmutableMap() }
    }

    private companion object {
        const val WEIGHT_STEP_KG = 2.5
        const val SECONDS_STEP = 30
        const val MAX_SECONDS_IN_MINUTE = 59
    }
}
