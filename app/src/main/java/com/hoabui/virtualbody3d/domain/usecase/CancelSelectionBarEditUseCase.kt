package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.ui.exerciselibrary.manager.ExerciseLibraryCartManager
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryChromeMode
import javax.inject.Inject

class CancelSelectionBarEditUseCase @Inject constructor(
    private val cartManager: ExerciseLibraryCartManager,
) {
    operator fun invoke() {
        val mode = cartManager.chromeMode.value as? ExerciseLibraryChromeMode.EditingScheduleRow ?: return
        cartManager.setChromeIdle()
        if (mode.isIsolatedScheduleRowSelectionEdit) {
            cartManager.clearCartForIsolatedSelectionEdit()
        } else {
            cartManager.restoreCartFromBaseline(
                itemDrafts = mode.baselineCart.itemDrafts,
                draftOrder = mode.baselineCart.draftOrder,
                activeExerciseId = mode.baselineCart.activeExerciseId,
                isCartExpanded = false,
            )
        }
    }
}
