package com.hoabui.virtualbody3d.ui.exerciselibrary.components.internal

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButton
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
internal fun SelectionBarFooter(
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val token = GymTheme.token
    GButton(
        text = stringResource(R.string.exercise_library_add_to_session),
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(token.spacing.sm),
        enabled = enabled,
    )
}
