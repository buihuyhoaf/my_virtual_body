package com.hoabui.virtualbody3d.ui.exerciselibrary.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import android.content.res.Configuration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButton
import com.hoabui.virtualbody3d.ui.common_ui.atom.surface.GSurface
import com.hoabui.virtualbody3d.ui.common_ui.organism.exercise.GExerciseCardUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.cart.CartSetField
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.wiring.WorkoutBuilderActions
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.ui.theme.tokens.component.GSurfaceTreatment

/**
 * Slim bottom bar: cart thumbnails, clear all, and primary CTA to navigate to session booking.
 */
@Composable
fun ExerciseLibraryCartBar(
    libraryState: ExerciseLibraryUiState,
    actions: WorkoutBuilderActions,
    cartItems: List<GExerciseCardUiModel>,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    val topRadius = token.bodyAnalysis.exerciseLibraryBookingSheetTopCornerRadius
    val shape = RoundedCornerShape(
        topStart = topRadius,
        topEnd = topRadius,
    )
    GSurface(
        modifier = modifier.fillMaxWidth(),
        color = token.colors.surface,
        shadowElevation = token.elevation.level3,
        treatment = GSurfaceTreatment.Flat,
        border = null,
        shape = shape,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            CartThumbnailRow(
                cartItems = cartItems,
                activeExerciseId = libraryState.activeExerciseId,
                onSelectCartItem = actions.onSelectCartItem,
                onRemoveCartItem = actions.onRemoveCartItem,
                onClearAll = actions.onClearCart,
                showClearAllButton = true,
                showRemoveOnThumbnail = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = token.spacing.md,
                        vertical = token.spacing.sm,
                    ),
            )
            GButton(
                text = stringResource(R.string.exercise_library_add_to_session),
                onClick = actions.onNavigateToSessionBooking,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = token.spacing.sm)
                    .padding(
                        top = token.spacing.xs,
                        bottom = token.spacing.sm,
                    ),
                enabled = libraryState.libraryList.isAddToSessionEnabled,
            )
        }
    }
}

@Preview(showBackground = true, name = "ExerciseLibraryCartBar — Light")
@Composable
private fun PreviewExerciseLibraryCartBarLight() {
    GymTheme {
        ExerciseLibraryCartBar(
            libraryState = ExerciseLibraryUiState(),
            actions = previewStubExerciseLibraryActions(),
            cartItems = emptyList(),
        )
    }
}

@Preview(showBackground = true, name = "ExerciseLibraryCartBar — Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewExerciseLibraryCartBarDark() {
    GymTheme(darkTheme = true) {
        ExerciseLibraryCartBar(
            libraryState = ExerciseLibraryUiState(),
            actions = previewStubExerciseLibraryActions(),
            cartItems = emptyList(),
        )
    }
}

private fun previewStubExerciseLibraryActions(): WorkoutBuilderActions {
    val noop: () -> Unit = {}
    val noopS: (String) -> Unit = {}
    val noop2: (String, Int, CartSetField, Int) -> Unit = { _, _, _, _ -> }
    val noop3: (String, Int, CartSetField, String) -> Unit = { _, _, _, _ -> }
    return WorkoutBuilderActions(
        onSelectCartItem = noopS,
        onRemoveCartItem = noopS,
        onClearCart = noop,
        onNavigateToSessionBooking = noop,
        onNavigateToWorkoutCalendar = noop,
        onStepCartField = noop2,
        onSetCartFieldManual = noop3,
        onToggleCartExpanded = noop,
        onConfirmSelectionBarEdit = noop,
        onCancelSelectionBarEdit = noop,
    )
}
