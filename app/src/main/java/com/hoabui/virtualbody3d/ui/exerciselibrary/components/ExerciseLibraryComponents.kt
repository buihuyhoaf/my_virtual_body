package com.hoabui.virtualbody3d.ui.exerciselibrary.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
fun ExerciseLibraryEmptyState(modifier: Modifier = Modifier) {
    val token = GymTheme.token
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = token.spacing.lg, vertical = token.spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        GText(
            text = stringResource(R.string.exercise_library_empty_title),
            style = token.typography.titleMedium,
            color = token.colors.textPrimary,
        )
        GText(
            text = stringResource(R.string.exercise_library_empty_message),
            style = token.typography.bodyMedium,
            color = token.colors.textSecondary,
            modifier = Modifier.padding(top = token.spacing.sm),
        )
    }
}
