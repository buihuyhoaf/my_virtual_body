package com.hoabui.virtualbody3d.ui.exercisedetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoabui.virtualbody3d.ui.components.UiStateContent
import com.hoabui.virtualbody3d.ui.exercisedetail.viewmodel.ExerciseDetailViewModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.ExerciseDetailDialog

@Composable
fun ExerciseDetailScreen(
    onBack: () -> Unit,
    viewModel: ExerciseDetailViewModel = hiltViewModel(),
) {
    val screenState by viewModel.state.collectAsStateWithLifecycle()
    UiStateContent(
        state = screenState,
        successContent = { _, exercise ->
            ExerciseDetailDialog(
                exercise = exercise,
                onDismiss = onBack,
            )
        },
    )
}
