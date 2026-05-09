package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.ui.exerciselibrary.manager.ExerciseLibraryCartManager
import com.hoabui.virtualbody3d.ui.exerciselibrary.cart.SelectionBarCartBaseline
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryChromeMode
import com.hoabui.virtualbody3d.ui.exerciselibrary.util.toExerciseDraftForSelectionBarEdit
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import javax.inject.Inject

class StartSelectionBarEditFromScheduleRowUseCase @Inject constructor(
    private val getWorkoutScheduleByRowUseCase: GetWorkoutScheduleByRowUseCase,
    private val cartManager: ExerciseLibraryCartManager,
) {
    suspend operator fun invoke(scheduleRowId: Long) {
        val schedule = getWorkoutScheduleByRowUseCase(scheduleRowId) ?: return
        val exerciseId = schedule.exerciseId
        val draft = schedule.toExerciseDraftForSelectionBarEdit()
        val newDrafts = persistentMapOf(exerciseId to draft)
        val newOrder = persistentListOf(exerciseId)
        cartManager.setSelectionBarEditCart(newDrafts, newOrder, exerciseId, isCartExpanded = true)
        val baseline = SelectionBarCartBaseline(
            itemDrafts = newDrafts,
            draftOrder = newOrder,
            activeExerciseId = exerciseId,
            isCartExpanded = true,
        )
        cartManager.setChromeMode(
            ExerciseLibraryChromeMode.EditingScheduleRow(
                scheduleRowId = scheduleRowId,
                baselineCart = baseline,
                isIsolatedScheduleRowSelectionEdit = true,
                measurementMode = schedule.measurementMode,
            ),
        )
    }
}
