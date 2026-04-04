package com.hoabui.virtualbody3d.ui.exerciselibrary.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButton
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButtonVariant
import com.hoabui.virtualbody3d.ui.common_ui.atom.dialog.GDialog
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
fun LongSessionWarningDialog(
    onDismissRequest: () -> Unit,
    onEditSession: () -> Unit,
    onProceedAnyway: () -> Unit,
) {
    val token = GymTheme.token
    GDialog(
        onDismissRequest = onDismissRequest,
        title = stringResource(R.string.exercise_library_long_session_title),
        description = stringResource(R.string.exercise_library_long_session_body),
        buttons = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(token.spacing.sm),
            ) {
                GButton(
                    text = stringResource(R.string.exercise_library_long_session_proceed),
                    onClick = onProceedAnyway,
                    modifier = Modifier.fillMaxWidth(),
                    variant = GButtonVariant.Primary,
                )
                GButton(
                    text = stringResource(R.string.exercise_library_long_session_edit),
                    onClick = onEditSession,
                    modifier = Modifier.fillMaxWidth(),
                    variant = GButtonVariant.Outlined,
                )
            }
        },
    )
}
