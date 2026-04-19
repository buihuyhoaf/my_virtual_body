package com.hoabui.virtualbody3d.ui.exerciselibrary.components.internal

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hoabui.virtualbody3d.ui.common_ui.organism.exercise.GExerciseCardUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.CartDragHandle
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.CartThumbnailRow
import com.hoabui.virtualbody3d.ui.exerciselibrary.model.ActiveExerciseInfo
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibraryActions
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
internal fun SelectionBarHeader(
    cartItems: List<GExerciseCardUiModel>,
    activeExerciseInfo: ActiveExerciseInfo?,
    actions: ExerciseLibraryActions,
    dragModifier: Modifier,
    isSelectionBarEditMode: Boolean,
) {
    val token = GymTheme.token
    CartDragHandle(modifier = dragModifier)
    CartThumbnailRow(
        cartItems = cartItems,
        activeExerciseId = activeExerciseInfo?.id,
        onSelectCartItem = actions.onSelectCartItem,
        onRemoveCartItem = actions.onRemoveCartItem,
        onClearAll = actions.onClearCart,
        showClearAllButton = !isSelectionBarEditMode,
        showRemoveOnThumbnail = !isSelectionBarEditMode,
        modifier = Modifier.padding(horizontal = token.spacing.sm),
    )
}
